package com.smartbusiness.usuario.controller;

import com.smartbusiness.usuario.dto.LoginRequest;
import com.smartbusiness.usuario.dto.LoginResponse;
import com.smartbusiness.usuario.dto.RegistroDuenoRequest;
import com.smartbusiness.usuario.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Rutas públicas (ver SecurityConfig: /api/auth/** está en permitAll())
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/registro")
    public ResponseEntity<LoginResponse> registrarDueno(@Valid @RequestBody RegistroDuenoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrarDueno(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
