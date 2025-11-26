package com.nuclearvet.modulos.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para autenticación.
 * Contiene el token JWT y la información del usuario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    
    private String token;
    private String tipo;
    private UsuarioDTO usuario;
    
    public static AuthResponseDTO from(String token, UsuarioDTO usuario) {
        return AuthResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .usuario(usuario)
                .build();
    }
}
