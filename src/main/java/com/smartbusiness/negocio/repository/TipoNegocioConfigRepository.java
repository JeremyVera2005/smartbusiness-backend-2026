package com.smartbusiness.negocio.repository;

import com.smartbusiness.negocio.model.TipoNegocio;
import com.smartbusiness.negocio.model.TipoNegocioConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoNegocioConfigRepository extends JpaRepository<TipoNegocioConfig, TipoNegocio> {
}
