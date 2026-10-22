package com.smartbusiness.catalogo.application.internal.queryservices;

import com.smartbusiness.catalogo.domain.model.aggregates.Producto;
import com.smartbusiness.catalogo.domain.model.queries.ObtenerProductoPorIdQuery;
import com.smartbusiness.catalogo.domain.model.queries.ObtenerProductosActivosQuery;
import com.smartbusiness.catalogo.domain.model.queries.ObtenerProductosConStockBajoQuery;
import com.smartbusiness.catalogo.domain.services.ProductoQueryService;
import com.smartbusiness.catalogo.infrastructure.persistence.jpa.repositories.ProductoRepository;
import com.smartbusiness.shared.domain.exceptions.AccesoNoAutorizadoException;
import com.smartbusiness.shared.domain.exceptions.ResourceNotFoundException;
import com.smartbusiness.shared.infrastructure.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductoQueryServiceImpl implements ProductoQueryService {

    private final ProductoRepository productoRepository;

    public ProductoQueryServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Producto> handle(ObtenerProductosActivosQuery query) {
        Long idNegocio = obtenerNegocioActivo();
        return productoRepository.findByIdNegocioAndActivoTrue(idNegocio);
    }

    @Override
    public Producto handle(ObtenerProductoPorIdQuery query) {
        Long idNegocio = obtenerNegocioActivo();
        Producto producto = productoRepository.findById(query.idProducto())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + query.idProducto()));

        if (!producto.getIdNegocio().equals(idNegocio)) {
            throw new AccesoNoAutorizadoException("El producto no pertenece al negocio activo");
        }
        return producto;
    }

    @Override
    public List<Producto> handle(ObtenerProductosConStockBajoQuery query) {
        Long idNegocio = obtenerNegocioActivo();
        return productoRepository.findConStockBajo(idNegocio);
    }

    private Long obtenerNegocioActivo() {
        Long idNegocio = TenantContext.getNegocioActual();
        if (idNegocio == null) {
            throw new AccesoNoAutorizadoException("No se especificó un negocio activo (header X-Id-Negocio)");
        }
        return idNegocio;
    }
}
