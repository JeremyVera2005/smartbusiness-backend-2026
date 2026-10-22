package com.smartbusiness.catalogo.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
public class ProductoRequest {

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El precio de venta es obligatorio")
    @PositiveOrZero
    private BigDecimal precioVenta;

    private BigDecimal precioCompra;

    @NotNull
    @PositiveOrZero
    private Integer stockActual;

    private Integer stockMinimo;

    private String categoria;

    // Campos dinámicos según tipo de negocio, ej: {"fechaVencimiento":"2027-01-01"}
    private Map<String, Object> atributosExtra;
}
