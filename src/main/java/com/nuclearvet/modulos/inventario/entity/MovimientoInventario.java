package com.nuclearvet.modulos.inventario.entity;

import com.nuclearvet.common.entity.EntidadBase;
import com.nuclearvet.modulos.usuarios.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad para movimientos de inventario (entradas y salidas).
 * RF4.4 - Registro de movimientos de inventario
 */
@Entity
@Table(name = "movimientos_inventario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class MovimientoInventario extends EntidadBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    private TipoMovimiento tipoMovimiento; // ENTRADA, SALIDA, AJUSTE, DEVOLUCION

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "stock_anterior", nullable = false)
    private Integer stockAnterior;

    @Column(name = "stock_nuevo", nullable = false)
    private Integer stockNuevo;

    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento;

    @Column(length = 50)
    private String motivo; // COMPRA, VENTA, VENCIDO, DAÑADO, PERDIDO, DONACION, etc.

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "numero_documento", length = 100)
    private String numeroDocumento; // Número de factura, orden de compra, etc.

    @Column(name = "costo_unitario", precision = 10, scale = 2)
    private BigDecimal costoUnitario;

    @Column(name = "costo_total", precision = 10, scale = 2)
    private BigDecimal costoTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // Usuario que registró el movimiento

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor; // Solo para movimientos de tipo ENTRADA

    @PrePersist
    protected void onCreate() {
        if (fechaMovimiento == null) {
            fechaMovimiento = LocalDateTime.now();
        }
        
        // Calcular costo total
        if (costoUnitario != null && cantidad != null) {
            costoTotal = costoUnitario.multiply(BigDecimal.valueOf(cantidad));
        }
    }
}
