package com.nuclearvet.modulos.administrativo.entity;

/**
 * Estados posibles de una factura.
 * RF6.1 - Gestión de facturación
 */
public enum EstadoFactura {
    PENDIENTE,     // Factura creada, pendiente de pago
    PAGADA,        // Factura completamente pagada
    PARCIAL,       // Factura con pago parcial
    VENCIDA,       // Factura con fecha de vencimiento superada
    CANCELADA      // Factura anulada/cancelada
}
