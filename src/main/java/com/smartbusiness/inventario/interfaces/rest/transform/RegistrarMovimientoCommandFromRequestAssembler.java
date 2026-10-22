package com.smartbusiness.inventario.interfaces.rest.transform;

import com.smartbusiness.inventario.domain.model.commands.RegistrarMovimientoCommand;
import com.smartbusiness.inventario.interfaces.rest.resources.MovimientoRequest;

public class RegistrarMovimientoCommandFromRequestAssembler {

    private RegistrarMovimientoCommandFromRequestAssembler() {
    }

    public static RegistrarMovimientoCommand toCommand(MovimientoRequest request) {
        return new RegistrarMovimientoCommand(
                request.getIdProducto(),
                request.getTipoMovimiento(),
                request.getCantidad(),
                request.getMotivo());
    }
}
