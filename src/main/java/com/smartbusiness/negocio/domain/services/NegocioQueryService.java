package com.smartbusiness.negocio.domain.services;

import com.smartbusiness.negocio.domain.model.aggregates.TipoNegocioConfig;
import com.smartbusiness.negocio.domain.model.queries.ObtenerConfigTipoNegocioQuery;
import com.smartbusiness.negocio.domain.model.queries.ObtenerMisNegociosQuery;
import com.smartbusiness.negocio.domain.model.queries.ObtenerNegocioPorIdQuery;
import com.smartbusiness.negocio.domain.model.readmodels.NegocioConRol;

import java.util.List;

/** Consultas sobre los negocios (lado Query de CQRS). */
public interface NegocioQueryService {

    List<NegocioConRol> handle(ObtenerMisNegociosQuery query);

    NegocioConRol handle(ObtenerNegocioPorIdQuery query);

    TipoNegocioConfig handle(ObtenerConfigTipoNegocioQuery query);
}
