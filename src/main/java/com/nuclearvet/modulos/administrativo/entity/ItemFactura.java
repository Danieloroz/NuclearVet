package com.nuclearvet.modulos.administrativo.entity;

import com.nuclearvet.common.entity.EntidadBase;
import com.nuclearvet.modulos.inventario.entity.Producto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entidad para items/líneas de detalle de una factura.
 * RF6.1 - Gestión de facturación
 */
@Entity
@Table(name = "items_factura")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ItemFactura extends EntidadBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoItemFactura tipo;

    @Column(name = "descripcion", nullable = false, length = 200)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto; // Si el item es un producto del inventario

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal; // cantidad * precioUnitario

    @Column(name = "observaciones", length = 300)
    private String observaciones;

    /**
     * Método helper para calcular el subtotal
     */
    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {
        this.subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
    }
}
