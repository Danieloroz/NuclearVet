package com.nuclearvet.modulos.inventario.entity;

/**
 * Tipos de movimiento de inventario
 */
public enum TipoMovimiento {
    ENTRADA,      // Compra o recepción de productos
    SALIDA,       // Venta o consumo de productos
    AJUSTE,       // Ajuste manual de inventario
    DEVOLUCION    // Devolución de productos
}
