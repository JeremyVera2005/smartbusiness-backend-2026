package com.smartbusiness.inventario.controller;

import com.smartbusiness.inventario.dto.MovimientoRequest;
import com.smartbusiness.inventario.dto.MovimientoResponse;
import com.smartbusiness.inventario.service.InventarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @PostMapping("/movimientos")
    public ResponseEntity<MovimientoResponse> registrarMovimiento(@Valid @RequestBody MovimientoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.registrarMovimiento(request));
    }

    @GetMapping("/movimientos/producto/{idProducto}")
    public ResponseEntity<List<MovimientoResponse>> historial(@PathVariable Long idProducto) {
        return ResponseEntity.ok(inventarioService.historialPorProducto(idProducto));
    }
}
