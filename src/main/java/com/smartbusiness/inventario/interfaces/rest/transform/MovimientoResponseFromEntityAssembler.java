package com.smartbusiness.inventario.interfaces.rest.transform;

import com.smartbusiness.inventario.domain.model.aggregates.MovimientoInventario;
import com.smartbusiness.inventario.interfaces.rest.resources.MovimientoResponse;

public class MovimientoResponseFromEntityAssembler {

    private MovimientoResponseFromEntityAssembler() {
    }

    public static MovimientoResponse toResponse(MovimientoInventario m) {
        return MovimientoResponse.builder()
                .idMovimiento(m.getIdMovimiento())
                .idProducto(m.getIdProducto())
                .tipoMovimiento(m.getTipoMovimiento())
                .cantidad(m.getCantidad())
                .stockResultante(m.getStockResultante())
                .motivo(m.getMotivo())
                .fechaMovimiento(m.getFechaMovimiento())
                .build();
    }
}
