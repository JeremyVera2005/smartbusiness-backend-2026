package com.smartbusiness.negocio.infrastructure.persistence.jpa.repositories;

import com.smartbusiness.negocio.domain.model.aggregates.Negocio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NegocioRepository extends JpaRepository<Negocio, Long> {

    List<Negocio> findByIdUsuarioPropietario(Long idUsuarioPropietario);
}
