package com.nuclearvet.modulos.inventario.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear/actualizar un proveedor.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearProveedorDTO {
    
    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String nombre;
    
    @Size(max = 150, message = "El nombre del contacto no puede exceder 150 caracteres")
    private String nombreContacto;
    
    @NotBlank(message = "El NIT es obligatorio")
    @Size(max = 50, message = "El NIT no puede exceder 50 caracteres")
    private String nit;
    
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;
    
    @Email(message = "El email debe ser válido")
    @Size(max = 150, message = "El email no puede exceder 150 caracteres")
    private String email;
    
    @Size(max = 300, message = "La dirección no puede exceder 300 caracteres")
    private String direccion;
    
    @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
    private String ciudad;
    
    @Size(max = 100, message = "El país no puede exceder 100 caracteres")
    private String pais;
    
    private String observaciones;
    
    @Min(value = 0, message = "Los días de crédito no pueden ser negativos")
    private Integer diasCredito;
    
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer calificacion;
}
