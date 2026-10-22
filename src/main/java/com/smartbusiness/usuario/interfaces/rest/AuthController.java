package com.smartbusiness.usuario.interfaces.rest;

import com.smartbusiness.usuario.domain.model.commands.RegistrarDuenoCommand;
import com.smartbusiness.usuario.domain.model.queries.IniciarSesionQuery;
import com.smartbusiness.usuario.domain.services.AuthCommandService;
import com.smartbusiness.usuario.domain.services.AuthQueryService;
import com.smartbusiness.usuario.interfaces.rest.resources.LoginRequest;
import com.smartbusiness.usuario.interfaces.rest.resources.LoginResponse;
import com.smartbusiness.usuario.interfaces.rest.resources.RegistroDuenoRequest;
import com.smartbusiness.usuario.interfaces.rest.transform.LoginResponseFromSesionAssembler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Rutas públicas (ver SecurityConfig: /api/auth/** está en permitAll())
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthCommandService authCommandService;
    private final AuthQueryService authQueryService;

    public AuthController(AuthCommandService authCommandService, AuthQueryService authQueryService) {
        this.authCommandService = authCommandService;
        this.authQueryService = authQueryService;
    }

    @PostMapping("/registro")
    public ResponseEntity<LoginResponse> registrarDueno(@Valid @RequestBody RegistroDuenoRequest request) {
        var sesion = authCommandService.handle(
                new RegistrarDuenoCommand(request.getNombre(), request.getEmail(), request.getPassword()));
        return ResponseEntity.status(HttpStatus.CREATED).body(LoginResponseFromSesionAssembler.toResponse(sesion));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        var sesion = authQueryService.handle(new IniciarSesionQuery(request.getEmail(), request.getPassword()));
        return ResponseEntity.ok(LoginResponseFromSesionAssembler.toResponse(sesion));
    }
}
