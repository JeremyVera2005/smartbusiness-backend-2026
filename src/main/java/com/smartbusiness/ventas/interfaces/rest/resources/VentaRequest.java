package com.smartbusiness.ventas.interfaces.rest.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VentaRequest {

    @NotEmpty(message = "La venta debe tener al menos un detalle")
    @Valid
    private List<DetalleVentaRequest> detalles;
}
