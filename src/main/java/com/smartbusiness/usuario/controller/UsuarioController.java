package com.smartbusiness.usuario.controller;

import com.smartbusiness.usuario.dto.CambiarPasswordRequest;
import com.smartbusiness.usuario.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

// Rutas de la cuenta del usuario autenticado (requieren JWT)
@RestController
@RequestMapping("/api/usuarios/me")
public class UsuarioController {

    @Autowired
    private AuthService authService;

    // PUT /api/usuarios/me/password -> pantalla "Mi cuenta" (cambiar contraseña)
    @PutMapping("/password")
    public ResponseEntity<Void> cambiarPassword(@Valid @RequestBody CambiarPasswordRequest request) {
        authService.cambiarPassword(obtenerIdUsuarioAutenticado(), request);
        return ResponseEntity.noContent().build();
    }

    private Long obtenerIdUsuarioAutenticado() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
