package com.smartbusiness.ventas.application.internal.queryservices;

import com.smartbusiness.shared.domain.exceptions.AccesoNoAutorizadoException;
import com.smartbusiness.shared.infrastructure.tenant.TenantContext;
import com.smartbusiness.ventas.domain.model.aggregates.Venta;
import com.smartbusiness.ventas.domain.model.queries.ObtenerVentasQuery;
import com.smartbusiness.ventas.domain.services.VentaQueryService;
import com.smartbusiness.ventas.infrastructure.persistence.jpa.repositories.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class VentaQueryServiceImpl implements VentaQueryService {

    private final VentaRepository ventaRepository;

    public VentaQueryServiceImpl(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    @Override
    public List<Venta> handle(ObtenerVentasQuery query) {
        Long idNegocio = obtenerNegocioActivo();
        return ventaRepository.findByIdNegocioOrderByFechaVentaDesc(idNegocio);
    }

    private Long obtenerNegocioActivo() {
        Long idNegocio = TenantContext.getNegocioActual();
        if (idNegocio == null) {
            throw new AccesoNoAutorizadoException("No se especificó un negocio activo (header X-Id-Negocio)");
        }
        return idNegocio;
    }
}
