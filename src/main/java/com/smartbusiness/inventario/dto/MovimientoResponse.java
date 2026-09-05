package com.smartbusiness.inventario.dto;

import com.smartbusiness.inventario.model.TipoMovimiento;
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
