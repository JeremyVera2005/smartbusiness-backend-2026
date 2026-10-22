package com.smartbusiness.negocio.interfaces.rest;

import com.smartbusiness.negocio.domain.model.queries.ObtenerConfigTipoNegocioQuery;
import com.smartbusiness.negocio.domain.model.queries.ObtenerMisNegociosQuery;
import com.smartbusiness.negocio.domain.model.queries.ObtenerNegocioPorIdQuery;
import com.smartbusiness.negocio.domain.model.valueobjects.RolNegocio;
import com.smartbusiness.negocio.domain.model.valueobjects.TipoNegocio;
import com.smartbusiness.negocio.domain.services.NegocioCommandService;
import com.smartbusiness.negocio.domain.services.NegocioQueryService;
import com.smartbusiness.negocio.interfaces.rest.resources.CrearNegocioRequest;
import com.smartbusiness.negocio.interfaces.rest.resources.NegocioResponse;
import com.smartbusiness.negocio.interfaces.rest.resources.TipoNegocioConfigResponse;
import com.smartbusiness.negocio.interfaces.rest.transform.CrearNegocioCommandFromRequestAssembler;
import com.smartbusiness.negocio.interfaces.rest.transform.NegocioResponseFromEntityAssembler;
import com.smartbusiness.negocio.interfaces.rest.transform.TipoNegocioConfigResponseFromEntityAssembler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/negocios")
public class NegocioController {

    private final NegocioCommandService negocioCommandService;
    private final NegocioQueryService negocioQueryService;
    private final TipoNegocioConfigResponseFromEntityAssembler configAssembler;

    public NegocioController(NegocioCommandService negocioCommandService,
                             NegocioQueryService negocioQueryService,
                             TipoNegocioConfigResponseFromEntityAssembler configAssembler) {
        this.negocioCommandService = negocioCommandService;
        this.negocioQueryService = negocioQueryService;
        this.configAssembler = configAssembler;
    }

    // POST /api/negocios/crear  -> pantalla "Crear negocio" (wizard)
    @PostMapping("/crear")
    public ResponseEntity<NegocioResponse> crearNegocio(@Valid @RequestBody CrearNegocioRequest request) {
        Long idUsuario = obtenerIdUsuarioAutenticado();
        var negocio = negocioCommandService.handle(
                CrearNegocioCommandFromRequestAssembler.toCommand(idUsuario, request));
        NegocioResponse response = NegocioResponseFromEntityAssembler.toResponse(negocio, RolNegocio.DUENO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/negocios  -> pantalla "Mis negocios" (menú principal)
    @GetMapping
    public ResponseEntity<List<NegocioResponse>> listarMisNegocios() {
        Long idUsuario = obtenerIdUsuarioAutenticado();
        var negocios = negocioQueryService.handle(new ObtenerMisNegociosQuery(idUsuario));
        return ResponseEntity.ok(negocios.stream().map(NegocioResponseFromEntityAssembler::toResponse).toList());
    }

    // GET /api/negocios/{id}  -> detalle de un negocio puntual
    @GetMapping("/{id}")
    public ResponseEntity<NegocioResponse> obtenerNegocio(@PathVariable("id") Long idNegocio) {
        Long idUsuario = obtenerIdUsuarioAutenticado();
        var negocio = negocioQueryService.handle(new ObtenerNegocioPorIdQuery(idNegocio, idUsuario));
        return ResponseEntity.ok(NegocioResponseFromEntityAssembler.toResponse(negocio));
    }

    // GET /api/negocios/config/{tipoNegocio} -> config para armar el CRUD dinámico en Angular
    @GetMapping("/config/{tipoNegocio}")
    public ResponseEntity<TipoNegocioConfigResponse> obtenerConfig(@PathVariable TipoNegocio tipoNegocio) {
        var config = negocioQueryService.handle(new ObtenerConfigTipoNegocioQuery(tipoNegocio));
        return ResponseEntity.ok(configAssembler.toResponse(config));
    }

    private Long obtenerIdUsuarioAutenticado() {
        // El JwtAuthFilter deja el idUsuario como "principal" del Authentication
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
