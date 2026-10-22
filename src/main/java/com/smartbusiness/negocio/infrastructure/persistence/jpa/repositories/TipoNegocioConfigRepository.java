package com.smartbusiness.negocio.infrastructure.persistence.jpa.repositories;

import com.smartbusiness.negocio.domain.model.aggregates.TipoNegocioConfig;
import com.smartbusiness.negocio.domain.model.valueobjects.TipoNegocio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoNegocioConfigRepository extends JpaRepository<TipoNegocioConfig, TipoNegocio> {
}
