package com.smartbusiness.inventario.interfaces.rest.resources;

import com.smartbusiness.inventario.domain.model.valueobjects.TipoMovimiento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoResponse {
    private Long idMovimiento;
    private Long idProducto;
    private TipoMovimiento tipoMovimiento;
    private Integer cantidad;
    private Integer stockResultante;
    private String motivo;
    private LocalDateTime fechaMovimiento;
}
