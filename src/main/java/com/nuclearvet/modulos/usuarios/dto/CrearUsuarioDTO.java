package com.nuclearvet.modulos.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO para crear un nuevo usuario.
 * Implementa RF1.1 - Gestión de usuarios
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearUsuarioDTO {
    
    @NotBlank(message = "El nombre es obligatorio, parce")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;
    
    @NotBlank(message = "El apellido es obligatorio, llave")
    @Size(max = 100, message = "El apellido no puede tener más de 100 caracteres")
    private String apellido;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    @Size(max = 150, message = "El email no puede tener más de 150 caracteres")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener mínimo 8 caracteres")
    private String contrasena;
    
    @Size(max = 20, message = "El teléfono no puede tener más de 20 caracteres")
    private String telefono;
    
    @Size(max = 50, message = "El documento no puede tener más de 50 caracteres")
    private String documentoIdentidad;
    
    private Set<String> roles; // ROLE_ADMIN, ROLE_VETERINARIO, etc.
}
