package com.nuclearvet.modulos.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para Producto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Long categoriaId;
    private String categoriaNombre;
    private Long proveedorId;
    private String proveedorNombre;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private Integer stockActual;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private String unidadMedida;
    private String lote;
    private LocalDate fechaVencimiento;
    private String ubicacion;
    private Boolean requierePrescripcion;
    private String principioActivo;
    private String presentacion;
    private String registroSanitario;
    private String observaciones;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    
    // Campos calculados
    private Boolean bajoStock;
    private Boolean proximoAVencer;
    private Boolean vencido;
}
