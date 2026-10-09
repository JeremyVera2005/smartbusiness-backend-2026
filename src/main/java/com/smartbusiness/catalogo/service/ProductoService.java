package com.smartbusiness.catalogo.service;

import com.smartbusiness.negocio.model.RolNegocio;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbusiness.catalogo.dto.ProductoRequest;
import com.smartbusiness.catalogo.dto.ProductoResponse;
import com.smartbusiness.catalogo.model.Producto;
import com.smartbusiness.catalogo.repository.ProductoRepository;
import com.smartbusiness.common.exception.AccesoNoAutorizadoException;
import com.smartbusiness.common.exception.ResourceNotFoundException;
import com.smartbusiness.config.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        Long idNegocio = obtenerNegocioActivo();
        validarEsDueno();

        Producto producto = Producto.builder()
                .idNegocio(idNegocio)
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precioVenta(request.getPrecioVenta())
                .precioCompra(request.getPrecioCompra())
                .stockActual(request.getStockActual())
                .stockMinimo(request.getStockMinimo())
                .categoria(request.getCategoria())
                .atributosExtra(serializar(request.getAtributosExtra()))
                .build();

        producto = productoRepository.save(producto);
        return mapearAResponse(producto);
    }

    public List<ProductoResponse> listar() {
        Long idNegocio = obtenerNegocioActivo();
        return productoRepository.findByIdNegocioAndActivoTrue(idNegocio).stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    public ProductoResponse obtenerPorId(Long idProducto) {
        Producto producto = buscarYValidarPertenencia(idProducto);
        return mapearAResponse(producto);
    }

    @Transactional
    public ProductoResponse actualizar(Long idProducto, ProductoRequest request) {
        validarEsDueno();
        Producto producto = buscarYValidarPertenencia(idProducto);

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecioVenta(request.getPrecioVenta());
        producto.setPrecioCompra(request.getPrecioCompra());
        producto.setStockActual(request.getStockActual());
        producto.setStockMinimo(request.getStockMinimo());
        producto.setCategoria(request.getCategoria());
        producto.setAtributosExtra(serializar(request.getAtributosExtra()));

        producto = productoRepository.save(producto);
        return mapearAResponse(producto);
    }

    @Transactional
    public void eliminar(Long idProducto) {
        validarEsDueno();
        Producto producto = buscarYValidarPertenencia(idProducto);
        producto.setActivo(false); // borrado lógico: no perder histórico de ventas
        productoRepository.save(producto);
    }

    public List<ProductoResponse> listarConStockBajo() {
        Long idNegocio = obtenerNegocioActivo();
        return productoRepository.findConStockBajo(idNegocio).stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    private Producto buscarYValidarPertenencia(Long idProducto) {
        Long idNegocio = obtenerNegocioActivo();
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + idProducto));

        if (!producto.getIdNegocio().equals(idNegocio)) {
            throw new AccesoNoAutorizadoException("El producto no pertenece al negocio activo");
        }
        return producto;
    }

    // El empleado consulta el catálogo, vende y registra movimientos, pero
    // crear, editar y desactivar productos queda reservado al dueño.
    private void validarEsDueno() {
        if (TenantContext.getRolActual() != RolNegocio.DUENO) {
            throw new AccesoNoAutorizadoException("Solo el dueño del negocio puede crear, editar o desactivar productos");
        }
    }

    private Long obtenerNegocioActivo() {
        Long idNegocio = TenantContext.getNegocioActual();
        if (idNegocio == null) {
            throw new AccesoNoAutorizadoException("No se especificó un negocio activo (header X-Id-Negocio)");
        }
        return idNegocio;
    }

    private String serializar(Map<String, Object> atributos) {
        try {
            return atributos == null ? "{}" : objectMapper.writeValueAsString(atributos);
        } catch (Exception e) {
            throw new RuntimeException("Error al serializar atributos extra", e);
        }
    }

    @SuppressWarnings("unchecked")
    private ProductoResponse mapearAResponse(Producto producto) {
        Map<String, Object> atributos;
        try {
            atributos = objectMapper.readValue(
                    producto.getAtributosExtra() == null ? "{}" : producto.getAtributosExtra(), Map.class);
        } catch (Exception e) {
            atributos = Map.of();
        }

        return ProductoResponse.builder()
                .idProducto(producto.getIdProducto())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precioVenta(producto.getPrecioVenta())
                .precioCompra(producto.getPrecioCompra())
                .stockActual(producto.getStockActual())
                .stockMinimo(producto.getStockMinimo())
                .categoria(producto.getCategoria())
                .atributosExtra(atributos)
                .activo(producto.getActivo())
                .build();
    }
}
