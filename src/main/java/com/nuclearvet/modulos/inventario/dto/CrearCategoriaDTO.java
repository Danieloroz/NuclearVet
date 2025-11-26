package com.nuclearvet.modulos.inventario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear/actualizar una categoría.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearCategoriaDTO {
    
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;
    
    @Size(max = 50, message = "El tipo de categoría no puede exceder 50 caracteres")
    private String tipoCategoria; // MEDICAMENTO, ALIMENTO, ACCESORIO, EQUIPAMIENTO, SERVICIO
}
