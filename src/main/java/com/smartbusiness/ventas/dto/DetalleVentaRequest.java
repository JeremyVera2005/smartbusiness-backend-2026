package com.smartbusiness.ventas.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetalleVentaRequest {

    @NotNull
    private Long idProducto;

    @NotNull
    @Positive
    private Integer cantidad;
}
