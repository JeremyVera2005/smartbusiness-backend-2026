package com.smartbusiness.inventario.interfaces.rest.resources;

import com.smartbusiness.inventario.domain.model.valueobjects.TipoMovimiento;
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
