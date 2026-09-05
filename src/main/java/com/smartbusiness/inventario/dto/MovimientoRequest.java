package com.smartbusiness.inventario.dto;

import com.smartbusiness.inventario.model.TipoMovimiento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovimientoRequest {

    @NotNull
    private Long idProducto;

    @NotNull
    private TipoMovimiento tipoMovimiento;

    @NotNull
    @Positive
    private Integer cantidad;

    private String motivo;
}
