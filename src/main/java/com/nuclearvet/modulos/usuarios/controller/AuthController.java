package com.nuclearvet.modulos.usuarios.controller;

import com.nuclearvet.common.dto.RespuestaExitosa;
import com.nuclearvet.modulos.usuarios.dto.AuthResponseDTO;
import com.nuclearvet.modulos.usuarios.dto.CambiarContrasenaDTO;
import com.nuclearvet.modulos.usuarios.dto.LoginDTO;
import com.nuclearvet.modulos.usuarios.dto.RecuperarContrasenaDTO;
import com.nuclearvet.modulos.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller para autenticación y recuperación de contraseña.
 * Endpoints públicos (no requieren autenticación).
 */
@Tag(name = "Autenticación", description = "API para login y recuperación de contraseña")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UsuarioService usuarioService;
    
    /**
     * Login - Autentica un usuario y retorna un token JWT.
     */
    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica un usuario y retorna un token JWT para acceder a los recursos protegidos."
    )
    @PostMapping("/login")
    public ResponseEntity<RespuestaExitosa<AuthResponseDTO>> login(
            @Valid @RequestBody LoginDTO loginDTO) {
        
        AuthResponseDTO response = usuarioService.login(loginDTO);
        return ResponseEntity.ok(RespuestaExitosa.crear(response, "Bienvenido, parce!"));
    }
    
    /**
     * Solicita recuperación de contraseña.
     */
    @Operation(
            summary = "Solicitar recuperación de contraseña",
            description = "Envía un email con un token para recuperar la contraseña."
    )
    @PostMapping("/recuperar-contrasena")
    public ResponseEntity<RespuestaExitosa<Void>> solicitarRecuperacion(
            @Valid @RequestBody RecuperarContrasenaDTO dto) {
        
        usuarioService.solicitarRecuperacionContrasena(dto);
        return ResponseEntity.ok(RespuestaExitosa.crear(null,
                "Si el email existe, te llegará un correo para recuperar la contraseña, llave"));
    }
    
    /**
     * Cambia la contraseña usando el token de recuperación.
     */
    @Operation(
            summary = "Cambiar contraseña",
            description = "Cambia la contraseña usando el token recibido por email."
    )
    @PostMapping("/cambiar-contrasena")
    public ResponseEntity<RespuestaExitosa<Void>> cambiarContrasena(
            @Valid @RequestBody CambiarContrasenaDTO dto) {
        
        usuarioService.cambiarContrasenaConToken(dto);
        return ResponseEntity.ok(RespuestaExitosa.crear(null,
                "Contraseña cambiada exitosamente, parce. Ya podés iniciar sesión"));
    }
}
