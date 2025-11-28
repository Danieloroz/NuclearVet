package com.nuclearvet.modulos.administrativo.dto;

import com.nuclearvet.modulos.administrativo.entity.MetodoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para visualizar pagos.
 * RF6.2 - Registro de pagos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoDTO {
    
    private Long id;
    private Long facturaId;
    private String numeroRecibo;
    private LocalDateTime fechaPago;
    private BigDecimal monto;
    private MetodoPago metodoPago;
    private String referenciaTransaccion;
    private String banco;
    private String observaciones;
    
    // Usuario que recibió el pago
    private Long recibidoPorId;
    private String recibidoPorNombre;
    
    private LocalDateTime fechaCreacion;
    private Boolean activo;
}
