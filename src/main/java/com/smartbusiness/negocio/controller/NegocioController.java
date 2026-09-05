package com.smartbusiness.negocio.controller;

import com.smartbusiness.negocio.dto.CrearNegocioRequest;
import com.smartbusiness.negocio.dto.NegocioResponse;
import com.smartbusiness.negocio.dto.TipoNegocioConfigResponse;
import com.smartbusiness.negocio.model.TipoNegocio;
import com.smartbusiness.negocio.service.NegocioService;
import com.smartbusiness.negocio.service.TipoNegocioConfigService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/negocios")
public class NegocioController {

    @Autowired
    private NegocioService negocioService;

    @Autowired
    private TipoNegocioConfigService tipoNegocioConfigService;

    // POST /api/negocios/crear  -> pantalla "Crear negocio" (wizard)
    @PostMapping("/crear")
    public ResponseEntity<NegocioResponse> crearNegocio(@Valid @RequestBody CrearNegocioRequest request) {
        Long idUsuario = obtenerIdUsuarioAutenticado();
        NegocioResponse response = negocioService.crearNegocio(idUsuario, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/negocios  -> pantalla "Mis negocios" (menú principal)
    @GetMapping
    public ResponseEntity<List<NegocioResponse>> listarMisNegocios() {
        Long idUsuario = obtenerIdUsuarioAutenticado();
        return ResponseEntity.ok(negocioService.listarMisNegocios(idUsuario));
    }

    // GET /api/negocios/{id}  -> detalle de un negocio puntual
    @GetMapping("/{id}")
    public ResponseEntity<NegocioResponse> obtenerNegocio(@PathVariable("id") Long idNegocio) {
        Long idUsuario = obtenerIdUsuarioAutenticado();
        return ResponseEntity.ok(negocioService.obtenerPorId(idNegocio, idUsuario));
    }

    // GET /api/negocios/config/{tipoNegocio} -> config para armar el CRUD dinámico en Angular
    @GetMapping("/config/{tipoNegocio}")
    public ResponseEntity<TipoNegocioConfigResponse> obtenerConfig(@PathVariable TipoNegocio tipoNegocio) {
        return ResponseEntity.ok(tipoNegocioConfigService.obtenerConfig(tipoNegocio));
    }

    private Long obtenerIdUsuarioAutenticado() {
        // El JwtAuthFilter deja el idUsuario como "principal" del Authentication
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
