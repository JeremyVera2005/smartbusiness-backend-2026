package com.smartbusiness.inventario.domain.services;

import com.smartbusiness.inventario.domain.model.aggregates.MovimientoInventario;
import com.smartbusiness.inventario.domain.model.queries.ObtenerHistorialMovimientosQuery;

import java.util.List;

/** Consultas sobre el inventario (lado Query de CQRS). */
public interface MovimientoInventarioQueryService {

    List<MovimientoInventario> handle(ObtenerHistorialMovimientosQuery query);
}
