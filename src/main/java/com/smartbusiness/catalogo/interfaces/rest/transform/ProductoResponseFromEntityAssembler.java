package com.smartbusiness.catalogo.interfaces.rest.transform;

import com.smartbusiness.catalogo.domain.model.aggregates.Producto;
import com.smartbusiness.catalogo.interfaces.rest.resources.ProductoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

/** Convierte el agregado Producto en el JSON que recibe el frontend. */
@Component
public class ProductoResponseFromEntityAssembler {

    private final ObjectMapper objectMapper;

    public ProductoResponseFromEntityAssembler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("unchecked")
    public ProductoResponse toResponse(Producto producto) {
        Map<String, Object> atributos;
        try {
            atributos = objectMapper.readValue(
                    producto.getAtributosExtra() == null ? "{}" : producto.getAtributosExtra(), Map.class);
        } catch (Exception e) {
            atributos = Map.of();
        }

        return ProductoResponse.builder()
                .idProducto(producto.getIdProducto())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precioVenta(producto.getPrecioVenta())
                .precioCompra(producto.getPrecioCompra())
                .stockActual(producto.getStockActual())
                .stockMinimo(producto.getStockMinimo())
                .categoria(producto.getCategoria())
                .atributosExtra(atributos)
                .activo(producto.getActivo())
                .build();
    }
}
