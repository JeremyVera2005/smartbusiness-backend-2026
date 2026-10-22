package com.smartbusiness.ventas.interfaces.rest.transform;

import com.smartbusiness.ventas.domain.model.commands.RegistrarVentaCommand;
import com.smartbusiness.ventas.interfaces.rest.resources.VentaRequest;

public class RegistrarVentaCommandFromRequestAssembler {

    private RegistrarVentaCommandFromRequestAssembler() {
    }

    public static RegistrarVentaCommand toCommand(VentaRequest request) {
        return new RegistrarVentaCommand(request.getDetalles().stream()
                .map(d -> new RegistrarVentaCommand.Detalle(d.getIdProducto(), d.getCantidad()))
                .toList());
    }
}
