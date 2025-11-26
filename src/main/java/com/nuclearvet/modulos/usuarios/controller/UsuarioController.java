package com.nuclearvet.modulos.usuarios.controller;

import com.nuclearvet.common.dto.RespuestaExitosa;
import com.nuclearvet.modulos.usuarios.dto.*;
import com.nuclearvet.modulos.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestión de usuarios.
 * Expone endpoints REST para el módulo de Usuarios y Accesos.
 * 
 * Patrón: MVC Controller
 */
@Tag(name = "Usuarios", description = "API para gestión de usuarios y autenticación")
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    /**
     * Crea un nuevo usuario (solo ADMIN).
     */
    @Operation(
            summary = "Crear nuevo usuario",
            description = "Crea un nuevo usuario en el sistema. Solo accesible por administradores."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RespuestaExitosa<UsuarioDTO>> crearUsuario(
            @Valid @RequestBody CrearUsuarioDTO dto) {
        
        UsuarioDTO usuario = usuarioService.crearUsuario(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(RespuestaExitosa.exito("Usuario creado exitosamente, parce", usuario));
    }
    
    /**
     * Obtiene todos los usuarios.
     */
    @Operation(summary = "Listar todos los usuarios")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RespuestaExitosa<List<UsuarioDTO>>> obtenerTodos() {
        List<UsuarioDTO> usuarios = usuarioService.obtenerTodos();
        return ResponseEntity.ok(RespuestaExitosa.exito(usuarios));
    }
    
    /**
     * Obtiene un usuario por ID.
     */
    @Operation(summary = "Obtener usuario por ID")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RespuestaExitosa<UsuarioDTO>> obtenerPorId(@PathVariable Long id) {
        UsuarioDTO usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(RespuestaExitosa.exito(usuario));
    }
    
    /**
     * Actualiza un usuario existente.
     */
    @Operation(summary = "Actualizar usuario")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RespuestaExitosa<UsuarioDTO>> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarUsuarioDTO dto) {
        
        UsuarioDTO usuario = usuarioService.actualizarUsuario(id, dto);
        return ResponseEntity.ok(RespuestaExitosa.exito("Usuario actualizado, llave", usuario));
    }
    
    /**
     * Desactiva un usuario.
     */
    @Operation(summary = "Desactivar usuario")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RespuestaExitosa<Void>> desactivarUsuario(@PathVariable Long id) {
        usuarioService.desactivarUsuario(id);
        return ResponseEntity.ok(RespuestaExitosa.exito("Usuario desactivado correctamente", null));
    }
    
    /**
     * Obtiene usuarios por rol.
     */
    @Operation(summary = "Obtener usuarios por rol")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/rol/{nombreRol}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RespuestaExitosa<List<UsuarioDTO>>> obtenerPorRol(
            @PathVariable String nombreRol) {
        
        List<UsuarioDTO> usuarios = usuarioService.obtenerPorRol(nombreRol);
        return ResponseEntity.ok(RespuestaExitosa.exito(usuarios));
    }
}
