package com.nuclearvet.modulos.inventario.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para registrar un movimiento de inventario.
 * RF4.4 - Registro de movimientos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarMovimientoDTO {
    
    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;
    
    @NotBlank(message = "El tipo de movimiento es obligatorio")
    @Pattern(regexp = "ENTRADA|SALIDA|AJUSTE|DEVOLUCION", message = "Tipo de movimiento inválido")
    private String tipoMovimiento;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser positiva")
    private Integer cantidad;
    
    @NotBlank(message = "El motivo es obligatorio")
    @Size(max = 50, message = "El motivo no puede exceder 50 caracteres")
    private String motivo; // COMPRA, VENTA, VENCIDO, DAÑADO, PERDIDO, DONACION, etc.
    
    private String observaciones;
    
    @Size(max = 100, message = "El número de documento no puede exceder 100 caracteres")
    private String numeroDocumento;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "El costo unitario debe ser mayor a 0")
    private BigDecimal costoUnitario;
    
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;
    
    // Solo para movimientos de tipo ENTRADA
    private Long proveedorId;
}
