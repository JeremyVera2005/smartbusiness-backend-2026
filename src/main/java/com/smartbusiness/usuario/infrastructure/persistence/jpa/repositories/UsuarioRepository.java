package com.smartbusiness.usuario.infrastructure.persistence.jpa.repositories;

import com.smartbusiness.usuario.domain.model.aggregates.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
