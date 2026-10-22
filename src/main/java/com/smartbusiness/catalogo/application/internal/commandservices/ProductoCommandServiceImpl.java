package com.smartbusiness.catalogo.application.internal.commandservices;

import com.smartbusiness.catalogo.domain.model.aggregates.Producto;
import com.smartbusiness.catalogo.domain.model.commands.ActualizarProductoCommand;
import com.smartbusiness.catalogo.domain.model.commands.CrearProductoCommand;
import com.smartbusiness.catalogo.domain.model.commands.EliminarProductoCommand;
import com.smartbusiness.catalogo.domain.services.ProductoCommandService;
import com.smartbusiness.catalogo.infrastructure.persistence.jpa.repositories.ProductoRepository;
import com.smartbusiness.negocio.domain.model.valueobjects.RolNegocio;
import com.smartbusiness.shared.domain.exceptions.AccesoNoAutorizadoException;
import com.smartbusiness.shared.domain.exceptions.ResourceNotFoundException;
import com.smartbusiness.shared.infrastructure.tenant.TenantContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class ProductoCommandServiceImpl implements ProductoCommandService {

    private final ProductoRepository productoRepository;
    private final ObjectMapper objectMapper;

    public ProductoCommandServiceImpl(ProductoRepository productoRepository, ObjectMapper objectMapper) {
        this.productoRepository = productoRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public Producto handle(CrearProductoCommand command) {
        Long idNegocio = obtenerNegocioActivo();
        validarEsDueno();

        Producto producto = Producto.builder()
                .idNegocio(idNegocio)
                .nombre(command.nombre())
                .descripcion(command.descripcion())
                .precioVenta(command.precioVenta())
                .precioCompra(command.precioCompra())
                .stockActual(command.stockActual())
                .stockMinimo(command.stockMinimo())
                .categoria(command.categoria())
                .atributosExtra(serializar(command.atributosExtra()))
                .build();

        return productoRepository.save(producto);
    }

    @Override
    @Transactional
    public Producto handle(ActualizarProductoCommand command) {
        validarEsDueno();
        Producto producto = buscarYValidarPertenencia(command.idProducto());

        producto.setNombre(command.nombre());
        producto.setDescripcion(command.descripcion());
        producto.setPrecioVenta(command.precioVenta());
        producto.setPrecioCompra(command.precioCompra());
        producto.setStockActual(command.stockActual());
        producto.setStockMinimo(command.stockMinimo());
        producto.setCategoria(command.categoria());
        producto.setAtributosExtra(serializar(command.atributosExtra()));

        return productoRepository.save(producto);
    }

    @Override
    @Transactional
    public void handle(EliminarProductoCommand command) {
        validarEsDueno();
        Producto producto = buscarYValidarPertenencia(command.idProducto());
        producto.setActivo(false); // borrado lógico: no perder histórico de ventas
        productoRepository.save(producto);
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
}
