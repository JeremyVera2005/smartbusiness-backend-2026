package com.smartbusiness.ventas.interfaces.rest.transform;

import com.smartbusiness.ventas.domain.model.aggregates.Venta;
import com.smartbusiness.ventas.interfaces.rest.resources.VentaResponse;

import java.util.List;
import java.util.stream.Collectors;

public class VentaResponseFromEntityAssembler {

    private VentaResponseFromEntityAssembler() {
    }

    public static VentaResponse toResponse(Venta venta) {
        List<VentaResponse.DetalleVentaResponse> detalles = venta.getDetalles().stream()
                .map(d -> VentaResponse.DetalleVentaResponse.builder()
                        .idProducto(d.getIdProducto())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotal(d.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return VentaResponse.builder()
                .idVenta(venta.getIdVenta())
                .total(venta.getTotal())
                .fechaVenta(venta.getFechaVenta())
                .detalles(detalles)
                .build();
    }
}
