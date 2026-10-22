package com.smartbusiness.inventario.domain.model.valueobjects;

public enum TipoMovimiento {
    ENTRADA,   // compra a proveedor, ajuste positivo
    SALIDA,    // venta, merma, ajuste negativo
    AJUSTE
}
