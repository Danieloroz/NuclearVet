package com.nuclearvet.modulos.inventario.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para crear/actualizar un producto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearProductoDTO {
    
    @NotBlank(message = "El código del producto es obligatorio")
    @Size(max = 50, message = "El código no puede exceder 50 caracteres")
    private String codigo;
    
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String nombre;
    
    private String descripcion;
    
    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;
    
    private Long proveedorId;
    
    @NotNull(message = "El precio de compra es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio de compra debe ser mayor a 0")
    private BigDecimal precioCompra;
    
    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio de venta debe ser mayor a 0")
    private BigDecimal precioVenta;
    
    @NotNull(message = "El stock actual es obligatorio")
    @Min(value = 0, message = "El stock actual no puede ser negativo")
    private Integer stockActual;
    
    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;
    
    @Min(value = 0, message = "El stock máximo no puede ser negativo")
    private Integer stockMaximo;
    
    @Size(max = 50, message = "La unidad de medida no puede exceder 50 caracteres")
    private String unidadMedida;
    
    @Size(max = 100, message = "El lote no puede exceder 100 caracteres")
    private String lote;
    
    private LocalDate fechaVencimiento;
    
    @Size(max = 100, message = "La ubicación no puede exceder 100 caracteres")
    private String ubicacion;
    
    private Boolean requierePrescripcion;
    
    @Size(max = 200, message = "El principio activo no puede exceder 200 caracteres")
    private String principioActivo;
    
    @Size(max = 100, message = "La presentación no puede exceder 100 caracteres")
    private String presentacion;
    
    @Size(max = 100, message = "El registro sanitario no puede exceder 100 caracteres")
    private String registroSanitario;
    
    private String observaciones;
}
