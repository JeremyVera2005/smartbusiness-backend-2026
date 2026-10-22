package com.smartbusiness.ia.domain.services;

import com.smartbusiness.ia.domain.model.queries.ObtenerAlertasStockQuery;
import com.smartbusiness.ia.domain.model.valueobjects.AlertaStock;

import java.util.List;

public interface RecomendacionQueryService {

    List<AlertaStock> handle(ObtenerAlertasStockQuery query);
}
