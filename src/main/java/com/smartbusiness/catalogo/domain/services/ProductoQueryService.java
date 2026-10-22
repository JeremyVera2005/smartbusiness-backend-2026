package com.smartbusiness.catalogo.domain.services;

import com.smartbusiness.catalogo.domain.model.aggregates.Producto;
import com.smartbusiness.catalogo.domain.model.queries.ObtenerProductoPorIdQuery;
import com.smartbusiness.catalogo.domain.model.queries.ObtenerProductosActivosQuery;
import com.smartbusiness.catalogo.domain.model.queries.ObtenerProductosConStockBajoQuery;

import java.util.List;

/** Consultas sobre el catálogo (lado Query de CQRS). */
public interface ProductoQueryService {

    List<Producto> handle(ObtenerProductosActivosQuery query);

    Producto handle(ObtenerProductoPorIdQuery query);

    List<Producto> handle(ObtenerProductosConStockBajoQuery query);
}
