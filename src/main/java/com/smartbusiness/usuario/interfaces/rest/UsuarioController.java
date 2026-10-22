package com.smartbusiness.usuario.interfaces.rest;

import com.smartbusiness.usuario.domain.model.commands.CambiarPasswordCommand;
import com.smartbusiness.usuario.domain.services.AuthCommandService;
import com.smartbusiness.usuario.interfaces.rest.resources.CambiarPasswordRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Rutas de la cuenta del usuario autenticado (requieren JWT)
@RestController
@RequestMapping("/api/usuarios/me")
public class UsuarioController {

    private final AuthCommandService authCommandService;

    public UsuarioController(AuthCommandService authCommandService) {
        this.authCommandService = authCommandService;
    }

    // PUT /api/usuarios/me/password -> pantalla "Mi cuenta" (cambiar contraseña)
    @PutMapping("/password")
    public ResponseEntity<Void> cambiarPassword(@Valid @RequestBody CambiarPasswordRequest request) {
        authCommandService.handle(new CambiarPasswordCommand(
                obtenerIdUsuarioAutenticado(), request.getPasswordActual(), request.getPasswordNueva()));
        return ResponseEntity.noContent().build();
    }

    private Long obtenerIdUsuarioAutenticado() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
