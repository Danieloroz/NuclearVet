package com.nuclearvet.modulos.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para Movimiento de Inventario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoInventarioDTO {
    
    private Long id;
    private Long productoId;
    private String productoNombre;
    private String productoCodigo;
    private String tipoMovimiento; // ENTRADA, SALIDA, AJUSTE, DEVOLUCION
    private Integer cantidad;
    private Integer stockAnterior;
    private Integer stockNuevo;
    private LocalDateTime fechaMovimiento;
    private String motivo;
    private String observaciones;
    private String numeroDocumento;
    private BigDecimal costoUnitario;
    private BigDecimal costoTotal;
    private Long usuarioId;
    private String usuarioNombre;
    private Long proveedorId;
    private String proveedorNombre;
}
