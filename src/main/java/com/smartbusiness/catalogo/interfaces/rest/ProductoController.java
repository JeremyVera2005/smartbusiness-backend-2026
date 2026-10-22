package com.smartbusiness.catalogo.interfaces.rest;

import com.smartbusiness.catalogo.domain.model.commands.EliminarProductoCommand;
import com.smartbusiness.catalogo.domain.model.queries.ObtenerProductoPorIdQuery;
import com.smartbusiness.catalogo.domain.model.queries.ObtenerProductosActivosQuery;
import com.smartbusiness.catalogo.domain.model.queries.ObtenerProductosConStockBajoQuery;
import com.smartbusiness.catalogo.domain.services.ProductoCommandService;
import com.smartbusiness.catalogo.domain.services.ProductoQueryService;
import com.smartbusiness.catalogo.interfaces.rest.resources.ProductoRequest;
import com.smartbusiness.catalogo.interfaces.rest.resources.ProductoResponse;
import com.smartbusiness.catalogo.interfaces.rest.transform.ProductoCommandFromRequestAssembler;
import com.smartbusiness.catalogo.interfaces.rest.transform.ProductoResponseFromEntityAssembler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Todas las rutas asumen el header X-Id-Negocio (ver TenantInterceptor)
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoCommandService productoCommandService;
    private final ProductoQueryService productoQueryService;
    private final ProductoResponseFromEntityAssembler responseAssembler;

    public ProductoController(ProductoCommandService productoCommandService,
                              ProductoQueryService productoQueryService,
                              ProductoResponseFromEntityAssembler responseAssembler) {
        this.productoCommandService = productoCommandService;
        this.productoQueryService = productoQueryService;
        this.responseAssembler = responseAssembler;
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        var producto = productoCommandService.handle(ProductoCommandFromRequestAssembler.toCrearCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(responseAssembler.toResponse(producto));
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar() {
        var productos = productoQueryService.handle(new ObtenerProductosActivosQuery());
        return ResponseEntity.ok(productos.stream().map(responseAssembler::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtener(@PathVariable("id") Long idProducto) {
        var producto = productoQueryService.handle(new ObtenerProductoPorIdQuery(idProducto));
        return ResponseEntity.ok(responseAssembler.toResponse(producto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizar(@PathVariable("id") Long idProducto,
                                                       @Valid @RequestBody ProductoRequest request) {
        var producto = productoCommandService.handle(
                ProductoCommandFromRequestAssembler.toActualizarCommand(idProducto, request));
        return ResponseEntity.ok(responseAssembler.toResponse(producto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long idProducto) {
        productoCommandService.handle(new EliminarProductoCommand(idProducto));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoResponse>> listarConStockBajo() {
        var productos = productoQueryService.handle(new ObtenerProductosConStockBajoQuery());
        return ResponseEntity.ok(productos.stream().map(responseAssembler::toResponse).toList());
    }
}
