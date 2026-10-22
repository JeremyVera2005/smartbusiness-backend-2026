package com.smartbusiness.ventas.domain.services;

import com.smartbusiness.ventas.domain.model.aggregates.Venta;
import com.smartbusiness.ventas.domain.model.queries.ObtenerVentasQuery;

import java.util.List;

/** Consultas sobre las ventas (lado Query de CQRS). */
public interface VentaQueryService {

    List<Venta> handle(ObtenerVentasQuery query);
}
