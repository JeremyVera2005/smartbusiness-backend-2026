package com.smartbusiness.usuario.controller;

import com.smartbusiness.usuario.dto.CrearEmpleadoRequest;
import com.smartbusiness.usuario.dto.EmpleadoResponse;
import com.smartbusiness.usuario.service.EmpleadoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Colgado directamente de /api/negocios/{idNegocio}/empleados: el idNegocio
 * va en la URL (no en el header X-Id-Negocio) porque el dueño puede estar
 * gestionando personal de un negocio sin necesidad de "entrar" a operarlo.
 */
@RestController
@RequestMapping("/api/negocios/{idNegocio}/empleados")
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    @PostMapping
    public ResponseEntity<EmpleadoResponse> crearEmpleado(@PathVariable Long idNegocio,
                                                            @Valid @RequestBody CrearEmpleadoRequest request) {
        Long idUsuarioSolicitante = obtenerIdUsuarioAutenticado();
        EmpleadoResponse response = empleadoService.crearEmpleado(idNegocio, idUsuarioSolicitante, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<EmpleadoResponse>> listarEmpleados(@PathVariable Long idNegocio) {
        Long idUsuarioSolicitante = obtenerIdUsuarioAutenticado();
        return ResponseEntity.ok(empleadoService.listarEmpleados(idNegocio, idUsuarioSolicitante));
    }

    @DeleteMapping("/{idUsuarioEmpleado}")
    public ResponseEntity<Void> quitarEmpleado(@PathVariable Long idNegocio,
                                                @PathVariable Long idUsuarioEmpleado) {
        Long idUsuarioSolicitante = obtenerIdUsuarioAutenticado();
        empleadoService.quitarEmpleado(idNegocio, idUsuarioEmpleado, idUsuarioSolicitante);
        return ResponseEntity.noContent().build();
    }

    private Long obtenerIdUsuarioAutenticado() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
