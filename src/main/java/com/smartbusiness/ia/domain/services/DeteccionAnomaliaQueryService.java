package com.smartbusiness.ia.domain.services;

import com.smartbusiness.ia.domain.model.queries.ObtenerAlertasDemandaQuery;
import com.smartbusiness.ia.domain.model.valueobjects.AlertaDemanda;

import java.util.List;

public interface DeteccionAnomaliaQueryService {

    List<AlertaDemanda> handle(ObtenerAlertasDemandaQuery query);
}
