package com.smartbusiness.inventario.interfaces.rest;

import com.smartbusiness.inventario.domain.model.queries.ObtenerHistorialMovimientosQuery;
import com.smartbusiness.inventario.domain.services.MovimientoInventarioCommandService;
import com.smartbusiness.inventario.domain.services.MovimientoInventarioQueryService;
import com.smartbusiness.inventario.interfaces.rest.resources.MovimientoRequest;
import com.smartbusiness.inventario.interfaces.rest.resources.MovimientoResponse;
import com.smartbusiness.inventario.interfaces.rest.transform.MovimientoResponseFromEntityAssembler;
import com.smartbusiness.inventario.interfaces.rest.transform.RegistrarMovimientoCommandFromRequestAssembler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final MovimientoInventarioCommandService commandService;
    private final MovimientoInventarioQueryService queryService;

    public InventarioController(MovimientoInventarioCommandService commandService,
                                MovimientoInventarioQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping("/movimientos")
    public ResponseEntity<MovimientoResponse> registrarMovimiento(@Valid @RequestBody MovimientoRequest request) {
        var movimiento = commandService.handle(RegistrarMovimientoCommandFromRequestAssembler.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(MovimientoResponseFromEntityAssembler.toResponse(movimiento));
    }

    @GetMapping("/movimientos/producto/{idProducto}")
    public ResponseEntity<List<MovimientoResponse>> historial(@PathVariable Long idProducto) {
        var movimientos = queryService.handle(new ObtenerHistorialMovimientosQuery(idProducto));
        return ResponseEntity.ok(movimientos.stream().map(MovimientoResponseFromEntityAssembler::toResponse).toList());
    }
}
