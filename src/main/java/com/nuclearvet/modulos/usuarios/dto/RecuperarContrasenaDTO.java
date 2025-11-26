package com.nuclearvet.modulos.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitar recuperación de contraseña.
 * Implementa RF1.4 - Recuperación de acceso
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecuperarContrasenaDTO {
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    private String email;
}
