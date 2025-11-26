package com.nuclearvet.modulos.inventario.entity;

import com.nuclearvet.common.entity.EntidadBase;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad para productos del inventario.
 * RF4.1, RF4.3 - Gestión de inventario y control de stock
 */
@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Producto extends EntidadBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo; // Código único del producto (SKU)

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    @Column(name = "precio_compra", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCompra;

    @Column(name = "precio_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo; // Para alertas de bajo stock (RF4.3)

    @Column(name = "stock_maximo")
    private Integer stockMaximo;

    @Column(name = "unidad_medida", length = 50)
    private String unidadMedida; // UNIDAD, CAJA, FRASCO, TABLETA, ML, GR, etc.

    @Column(name = "lote", length = 100)
    private String lote;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "ubicacion", length = 100)
    private String ubicacion; // Ubicación física en el almacén

    @Column(name = "requiere_prescripcion")
    private Boolean requierePrescripcion; // Para medicamentos controlados

    @Column(name = "principio_activo", length = 200)
    private String principioActivo; // Para medicamentos

    @Column(name = "presentacion", length = 100)
    private String presentacion; // Ej: "Caja x 20 tabletas"

    @Column(name = "registro_sanitario", length = 100)
    private String registroSanitario;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    /**
     * Verifica si el producto está por debajo del stock mínimo.
     */
    public boolean bajosEnStock() {
        return stockActual != null && stockMinimo != null && stockActual <= stockMinimo;
    }

    /**
     * Verifica si el producto está próximo a vencer (menos de 30 días).
     */
    public boolean proximoAVencer() {
        if (fechaVencimiento == null) return false;
        return fechaVencimiento.isBefore(LocalDate.now().plusDays(30));
    }

    /**
     * Verifica si el producto está vencido.
     */
    public boolean vencido() {
        if (fechaVencimiento == null) return false;
        return fechaVencimiento.isBefore(LocalDate.now());
    }
}
