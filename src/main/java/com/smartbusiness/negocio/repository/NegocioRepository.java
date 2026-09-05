package com.smartbusiness.negocio.repository;

import com.smartbusiness.negocio.model.Negocio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NegocioRepository extends JpaRepository<Negocio, Long> {

    List<Negocio> findByIdUsuarioPropietario(Long idUsuarioPropietario);
}
