package com.nuclearvet.modulos.administrativo.entity;

import com.nuclearvet.common.entity.EntidadBase;
import com.nuclearvet.modulos.usuarios.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad para registro de pagos realizados a facturas.
 * RF6.2 - Registro de pagos
 * RF6.3 - Control de pagos parciales
 */
@Entity
@Table(name = "pagos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Pago extends EntidadBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura;

    @Column(name = "numero_recibo", unique = true, length = 20)
    private String numeroRecibo;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 20)
    private MetodoPago metodoPago;

    @Column(name = "referencia_transaccion", length = 100)
    private String referenciaTransaccion; // Número de transferencia, cheque, etc.

    @Column(name = "banco", length = 100)
    private String banco; // Para transferencias o cheques

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recibido_por_id", nullable = false)
    private Usuario recibidoPor; // Usuario que registró el pago
}
