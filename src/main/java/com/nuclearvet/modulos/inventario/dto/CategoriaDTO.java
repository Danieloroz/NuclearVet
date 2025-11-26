package com.nuclearvet.modulos.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para Categoría.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {
    
    private Long id;
    private String nombre;
    private String descripcion;
    private String tipoCategoria;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private Long totalProductos; // Cantidad de productos en esta categoría
}
