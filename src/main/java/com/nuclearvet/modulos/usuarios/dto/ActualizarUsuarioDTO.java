package com.nuclearvet.modulos.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO para actualizar un usuario existente.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarUsuarioDTO {
    
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;
    
    @Size(max = 100, message = "El apellido no puede tener más de 100 caracteres")
    private String apellido;
    
    @Email(message = "El email no tiene un formato válido")
    @Size(max = 150, message = "El email no puede tener más de 150 caracteres")
    private String email;
    
    @Size(max = 20, message = "El teléfono no puede tener más de 20 caracteres")
    private String telefono;
    
    @Size(max = 50, message = "El documento no puede tener más de 50 caracteres")
    private String documentoIdentidad;
    
    private Set<String> roles;
    
    private Boolean activo;
}
