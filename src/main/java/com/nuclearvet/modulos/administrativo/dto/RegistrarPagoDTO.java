package com.nuclearvet.modulos.administrativo.dto;

import com.nuclearvet.modulos.administrativo.entity.MetodoPago;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para registrar un pago a una factura.
 * RF6.2 - Registro de pagos
 * RF6.3 - Pagos parciales
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarPagoDTO {
    
    @NotNull(message = "El ID de la factura es obligatorio")
    private Long facturaId;
    
    @NotNull(message = "La fecha de pago es obligatoria")
    @PastOrPresent(message = "La fecha de pago no puede ser futura")
    private LocalDateTime fechaPago;
    
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;
    
    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPago metodoPago;
    
    @Size(max = 100, message = "La referencia no puede exceder 100 caracteres")
    private String referenciaTransaccion;
    
    @Size(max = 100, message = "El banco no puede exceder 100 caracteres")
    private String banco;
    
    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;
    
    @NotNull(message = "El ID del usuario que recibe es obligatorio")
    private Long recibidoPorId;
}
