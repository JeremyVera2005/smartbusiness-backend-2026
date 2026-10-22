package com.smartbusiness.negocio.infrastructure.persistence.jpa.repositories;

import com.smartbusiness.negocio.domain.model.aggregates.UsuarioNegocio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioNegocioRepository extends JpaRepository<UsuarioNegocio, UsuarioNegocio.UsuarioNegocioId> {

    List<UsuarioNegocio> findByIdIdUsuario(Long idUsuario);

    List<UsuarioNegocio> findByIdIdNegocio(Long idNegocio);

    Optional<UsuarioNegocio> findByIdIdUsuarioAndIdIdNegocio(Long idUsuario, Long idNegocio);
}
