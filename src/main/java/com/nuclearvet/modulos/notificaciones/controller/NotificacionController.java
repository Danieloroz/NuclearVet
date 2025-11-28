package com.nuclearvet.modulos.notificaciones.controller;

import com.nuclearvet.modulos.notificaciones.dto.CrearNotificacionDTO;
import com.nuclearvet.modulos.notificaciones.dto.NotificacionDTO;
import com.nuclearvet.modulos.notificaciones.entity.CanalNotificacion;
import com.nuclearvet.modulos.notificaciones.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de notificaciones.
 * RF5.1: Enviar notificaciones a usuarios
 */
@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "API para gestión de notificaciones del sistema")
public class NotificacionController {

    private final NotificacionService notificacionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @Operation(summary = "Crear y enviar notificación", description = "RF5.1: Crea y envía una notificación a un usuario")
    public ResponseEntity<NotificacionDTO> enviarNotificacion(@Valid @RequestBody CrearNotificacionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificacionService.crearNotificacion(dto));
    }

    @GetMapping("/destinatario/{destinatarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA', 'CLIENTE')")
    @Operation(summary = "Listar notificaciones de un usuario", description = "Obtiene todas las notificaciones de un usuario")
    public ResponseEntity<List<NotificacionDTO>> listarPorDestinatario(@PathVariable Long destinatarioId) {
        return ResponseEntity.ok(notificacionService.listarPorDestinatario(destinatarioId));
    }

    @GetMapping("/destinatario/{destinatarioId}/no-leidas")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA', 'CLIENTE')")
    @Operation(summary = "Listar notificaciones no leídas", description = "Obtiene las notificaciones no leídas de un usuario")
    public ResponseEntity<List<NotificacionDTO>> listarNoLeidas(@PathVariable Long destinatarioId) {
        return ResponseEntity.ok(notificacionService.listarNoLeidas(destinatarioId));
    }

    @GetMapping("/destinatario/{destinatarioId}/canal/{canal}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA', 'CLIENTE')")
    @Operation(summary = "Listar por canal", description = "Obtiene notificaciones filtradas por canal")
    public ResponseEntity<List<NotificacionDTO>> listarPorCanal(
            @PathVariable Long destinatarioId,
            @PathVariable CanalNotificacion canal) {
        return ResponseEntity.ok(notificacionService.listarPorCanal(destinatarioId, canal));
    }

    @PutMapping("/{id}/marcar-leida")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA', 'CLIENTE')")
    @Operation(summary = "Marcar como leída", description = "Marca una notificación como leída")
    public ResponseEntity<NotificacionDTO> marcarComoLeida(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.marcarComoLeida(id));
    }

    @PutMapping("/destinatario/{destinatarioId}/marcar-todas-leidas")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA', 'CLIENTE')")
    @Operation(summary = "Marcar todas como leídas", description = "Marca todas las notificaciones de un usuario como leídas")
    public ResponseEntity<Void> marcarTodasComoLeidas(@PathVariable Long destinatarioId) {
        notificacionService.marcarTodasComoLeidas(destinatarioId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reenviar")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @Operation(summary = "Reenviar notificación", description = "Reintenta enviar una notificación fallida")
    public ResponseEntity<NotificacionDTO> reenviarNotificacion(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.reenviarNotificacion(id));
    }

    @GetMapping("/destinatario/{destinatarioId}/count-no-leidas")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA', 'CLIENTE')")
    @Operation(summary = "Contar no leídas", description = "Cuenta las notificaciones no leídas de un usuario")
    public ResponseEntity<Long> contarNoLeidas(@PathVariable Long destinatarioId) {
        return ResponseEntity.ok(notificacionService.contarNoLeidas(destinatarioId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar notificación", description = "Desactiva una notificación")
    public ResponseEntity<Void> eliminarNotificacion(@PathVariable Long id) {
        notificacionService.desactivarNotificacion(id);
        return ResponseEntity.noContent().build();
    }
}
