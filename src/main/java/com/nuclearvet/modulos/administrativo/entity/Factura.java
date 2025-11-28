package com.nuclearvet.modulos.administrativo.entity;

import com.nuclearvet.common.entity.EntidadBase;
import com.nuclearvet.modulos.pacientes.entity.Consulta;
import com.nuclearvet.modulos.pacientes.entity.Paciente;
import com.nuclearvet.modulos.usuarios.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad para facturas de servicios veterinarios.
 * RF6.1 - Gestión de facturación
 * RF6.3 - Control de pagos
 */
@Entity
@Table(name = "facturas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Factura extends EntidadBase {

    @Column(name = "numero_factura", unique = true, nullable = false, length = 20)
    private String numeroFactura; // Número consecutivo de factura

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id", nullable = false)
    private Usuario propietario; // Dueño del paciente

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consulta_id")
    private Consulta consulta; // Consulta asociada (opcional)

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoFactura estado;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemFactura> items = new ArrayList<>();

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal; // Suma de items antes de impuestos

    @Column(name = "porcentaje_impuesto", precision = 5, scale = 2)
    private BigDecimal porcentajeImpuesto; // Ej: 19.00 para 19%

    @Column(name = "valor_impuesto", precision = 10, scale = 2)
    private BigDecimal valorImpuesto; // Valor calculado del impuesto

    @Column(name = "descuento", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal descuento = BigDecimal.ZERO; // Descuento aplicado

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total; // Total a pagar

    @Column(name = "total_pagado", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalPagado = BigDecimal.ZERO; // Total pagado hasta ahora

    @Column(name = "saldo_pendiente", precision = 10, scale = 2)
    private BigDecimal saldoPendiente; // Saldo que falta por pagar

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Pago> pagos = new ArrayList<>();

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emitida_por_id", nullable = false)
    private Usuario emitidaPor; // Usuario que emitió la factura

    /**
     * Método helper para agregar un item a la factura
     */
    public void agregarItem(ItemFactura item) {
        items.add(item);
        item.setFactura(this);
    }

    /**
     * Método helper para calcular el subtotal
     */
    public void calcularSubtotal() {
        this.subtotal = items.stream()
                .map(ItemFactura::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Método helper para calcular el total
     */
    public void calcularTotal() {
        calcularSubtotal();
        
        // Calcular impuesto
        if (porcentajeImpuesto != null && porcentajeImpuesto.compareTo(BigDecimal.ZERO) > 0) {
            this.valorImpuesto = subtotal.multiply(porcentajeImpuesto)
                    .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
        } else {
            this.valorImpuesto = BigDecimal.ZERO;
        }
        
        // Total = Subtotal + Impuesto - Descuento
        this.total = subtotal
                .add(valorImpuesto)
                .subtract(descuento != null ? descuento : BigDecimal.ZERO);
        
        // Actualizar saldo pendiente
        this.saldoPendiente = total.subtract(totalPagado != null ? totalPagado : BigDecimal.ZERO);
    }

    /**
     * Método helper para registrar un pago
     */
    public void registrarPago(Pago pago) {
        pagos.add(pago);
        pago.setFactura(this);
        
        // Actualizar total pagado
        this.totalPagado = pagos.stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Actualizar saldo pendiente
        this.saldoPendiente = total.subtract(totalPagado);
        
        // Actualizar estado
        if (saldoPendiente.compareTo(BigDecimal.ZERO) <= 0) {
            this.estado = EstadoFactura.PAGADA;
        } else if (totalPagado.compareTo(BigDecimal.ZERO) > 0) {
            this.estado = EstadoFactura.PARCIAL;
        }
    }

    /**
     * Verifica si la factura está vencida
     */
    public boolean estaVencida() {
        return fechaVencimiento != null && 
               LocalDate.now().isAfter(fechaVencimiento) && 
               estado != EstadoFactura.PAGADA && 
               estado != EstadoFactura.CANCELADA;
    }
}
