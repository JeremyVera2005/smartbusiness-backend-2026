package com.smartbusiness.catalogo.controller;

import com.smartbusiness.catalogo.dto.ProductoRequest;
import com.smartbusiness.catalogo.dto.ProductoResponse;
import com.smartbusiness.catalogo.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Todas las rutas asumen el header X-Id-Negocio (ver TenantInterceptor)
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar() {
        return ResponseEntity.ok(productoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtener(@PathVariable("id") Long idProducto) {
        return ResponseEntity.ok(productoService.obtenerPorId(idProducto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizar(@PathVariable("id") Long idProducto,
                                                         @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoService.actualizar(idProducto, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long idProducto) {
        productoService.eliminar(idProducto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoResponse>> listarConStockBajo() {
        return ResponseEntity.ok(productoService.listarConStockBajo());
    }
}
