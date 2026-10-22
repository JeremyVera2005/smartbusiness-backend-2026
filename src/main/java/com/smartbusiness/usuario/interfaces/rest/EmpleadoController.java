package com.smartbusiness.usuario.interfaces.rest;

import com.smartbusiness.usuario.domain.model.commands.CrearEmpleadoCommand;
import com.smartbusiness.usuario.domain.model.commands.QuitarEmpleadoCommand;
import com.smartbusiness.usuario.domain.model.commands.RestablecerPasswordEmpleadoCommand;
import com.smartbusiness.usuario.domain.model.queries.ObtenerEmpleadosQuery;
import com.smartbusiness.usuario.domain.services.EmpleadoCommandService;
import com.smartbusiness.usuario.domain.services.EmpleadoQueryService;
import com.smartbusiness.usuario.interfaces.rest.resources.CrearEmpleadoRequest;
import com.smartbusiness.usuario.interfaces.rest.resources.EmpleadoResponse;
import com.smartbusiness.usuario.interfaces.rest.resources.RestablecerPasswordRequest;
import com.smartbusiness.usuario.interfaces.rest.transform.EmpleadoResponseFromReadModelAssembler;
import jakarta.validation.Valid;
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

    private final EmpleadoCommandService empleadoCommandService;
    private final EmpleadoQueryService empleadoQueryService;

    public EmpleadoController(EmpleadoCommandService empleadoCommandService,
                              EmpleadoQueryService empleadoQueryService) {
        this.empleadoCommandService = empleadoCommandService;
        this.empleadoQueryService = empleadoQueryService;
    }

    @PostMapping
    public ResponseEntity<EmpleadoResponse> crearEmpleado(@PathVariable Long idNegocio,
                                                          @Valid @RequestBody CrearEmpleadoRequest request) {
        Long idUsuarioSolicitante = obtenerIdUsuarioAutenticado();
        var empleado = empleadoCommandService.handle(new CrearEmpleadoCommand(
                idNegocio, idUsuarioSolicitante, request.getNombre(), request.getEmail(), request.getPassword()));
        return ResponseEntity.status(HttpStatus.CREATED).body(EmpleadoResponseFromReadModelAssembler.toResponse(empleado));
    }

    @GetMapping
    public ResponseEntity<List<EmpleadoResponse>> listarEmpleados(@PathVariable Long idNegocio) {
        Long idUsuarioSolicitante = obtenerIdUsuarioAutenticado();
        var empleados = empleadoQueryService.handle(new ObtenerEmpleadosQuery(idNegocio, idUsuarioSolicitante));
        return ResponseEntity.ok(empleados.stream().map(EmpleadoResponseFromReadModelAssembler::toResponse).toList());
    }

    @DeleteMapping("/{idUsuarioEmpleado}")
    public ResponseEntity<Void> quitarEmpleado(@PathVariable Long idNegocio,
                                               @PathVariable Long idUsuarioEmpleado) {
        Long idUsuarioSolicitante = obtenerIdUsuarioAutenticado();
        empleadoCommandService.handle(new QuitarEmpleadoCommand(idNegocio, idUsuarioEmpleado, idUsuarioSolicitante));
        return ResponseEntity.noContent().build();
    }

    // PUT /api/negocios/{idNegocio}/empleados/{id}/password -> el dueño asigna una contraseña temporal
    @PutMapping("/{idUsuarioEmpleado}/password")
    public ResponseEntity<Void> restablecerPassword(@PathVariable Long idNegocio,
                                                    @PathVariable Long idUsuarioEmpleado,
                                                    @Valid @RequestBody RestablecerPasswordRequest request) {
        empleadoCommandService.handle(new RestablecerPasswordEmpleadoCommand(
                idNegocio, idUsuarioEmpleado, obtenerIdUsuarioAutenticado(), request.getPassword()));
        return ResponseEntity.noContent().build();
    }

    private Long obtenerIdUsuarioAutenticado() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
