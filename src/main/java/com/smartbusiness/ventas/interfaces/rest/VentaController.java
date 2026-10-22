package com.smartbusiness.ventas.interfaces.rest;

import com.smartbusiness.ventas.domain.model.queries.ObtenerVentasQuery;
import com.smartbusiness.ventas.domain.services.VentaCommandService;
import com.smartbusiness.ventas.domain.services.VentaQueryService;
import com.smartbusiness.ventas.interfaces.rest.resources.VentaRequest;
import com.smartbusiness.ventas.interfaces.rest.resources.VentaResponse;
import com.smartbusiness.ventas.interfaces.rest.transform.RegistrarVentaCommandFromRequestAssembler;
import com.smartbusiness.ventas.interfaces.rest.transform.VentaResponseFromEntityAssembler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaCommandService ventaCommandService;
    private final VentaQueryService ventaQueryService;

    public VentaController(VentaCommandService ventaCommandService, VentaQueryService ventaQueryService) {
        this.ventaCommandService = ventaCommandService;
        this.ventaQueryService = ventaQueryService;
    }

    @PostMapping
    public ResponseEntity<VentaResponse> registrarVenta(@Valid @RequestBody VentaRequest request) {
        var venta = ventaCommandService.handle(RegistrarVentaCommandFromRequestAssembler.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(VentaResponseFromEntityAssembler.toResponse(venta));
    }

    @GetMapping
    public ResponseEntity<List<VentaResponse>> listar() {
        var ventas = ventaQueryService.handle(new ObtenerVentasQuery());
        return ResponseEntity.ok(ventas.stream().map(VentaResponseFromEntityAssembler::toResponse).toList());
    }
}
