package com.smartbusiness.catalogo.interfaces.rest.resources;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponse {
    private Long idProducto;
    private String nombre;
    private String descripcion;
    private BigDecimal precioVenta;
    private BigDecimal precioCompra;
    private Integer stockActual;
    private Integer stockMinimo;
    private String categoria;
    private Map<String, Object> atributosExtra;
    private Boolean activo;
}
