package com.nuclearvet.modulos.administrativo.entity;

/**
 * Tipos de items que pueden incluirse en una factura.
 * RF6.1 - Gestión de facturación
 */
public enum TipoItemFactura {
    CONSULTA,          // Consulta veterinaria
    PRODUCTO,          // Producto del inventario (medicamentos, alimentos, etc.)
    SERVICIO,          // Servicio adicional (baño, peluquería, etc.)
    PROCEDIMIENTO,     // Procedimiento médico (cirugía, vacunación, etc.)
    HOSPEDAJE          // Hospitalización/hospedaje
}
