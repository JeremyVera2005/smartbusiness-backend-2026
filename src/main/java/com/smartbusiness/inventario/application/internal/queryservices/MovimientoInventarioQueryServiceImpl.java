package com.smartbusiness.inventario.application.internal.queryservices;

import com.smartbusiness.inventario.domain.model.aggregates.MovimientoInventario;
import com.smartbusiness.inventario.domain.model.queries.ObtenerHistorialMovimientosQuery;
import com.smartbusiness.inventario.domain.services.MovimientoInventarioQueryService;
import com.smartbusiness.inventario.infrastructure.persistence.jpa.repositories.MovimientoInventarioRepository;
import com.smartbusiness.shared.domain.exceptions.AccesoNoAutorizadoException;
import com.smartbusiness.shared.infrastructure.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MovimientoInventarioQueryServiceImpl implements MovimientoInventarioQueryService {

    private final MovimientoInventarioRepository movimientoRepository;

    public MovimientoInventarioQueryServiceImpl(MovimientoInventarioRepository movimientoRepository) {
        this.movimientoRepository = movimientoRepository;
    }

    @Override
    public List<MovimientoInventario> handle(ObtenerHistorialMovimientosQuery query) {
        Long idNegocio = obtenerNegocioActivo();
        return movimientoRepository.findByIdNegocioAndIdProductoOrderByFechaMovimientoDesc(idNegocio, query.idProducto());
    }

    private Long obtenerNegocioActivo() {
        Long idNegocio = TenantContext.getNegocioActual();
        if (idNegocio == null) {
            throw new AccesoNoAutorizadoException("No se especificó un negocio activo (header X-Id-Negocio)");
        }
        return idNegocio;
    }
}
