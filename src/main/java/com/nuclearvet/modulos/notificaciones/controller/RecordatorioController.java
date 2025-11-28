package com.nuclearvet.modulos.notificaciones.controller;

import com.nuclearvet.modulos.notificaciones.dto.CrearRecordatorioDTO;
import com.nuclearvet.modulos.notificaciones.dto.RecordatorioDTO;
import com.nuclearvet.modulos.notificaciones.service.RecordatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para gestión de recordatorios.
 * RF5.2: Programar recordatorios automáticos de citas
 */
@RestController
@RequestMapping("/api/recordatorios")
@RequiredArgsConstructor
@Tag(name = "Recordatorios", description = "API para gestión de recordatorios automáticos")
public class RecordatorioController {

    private final RecordatorioService recordatorioService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @Operation(summary = "Crear recordatorio", description = "RF5.2: Programa un recordatorio automático")
    public ResponseEntity<RecordatorioDTO> crearRecordatorio(@Valid @RequestBody CrearRecordatorioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recordatorioService.crearRecordatorio(dto));
    }

    @PostMapping("/cita/{citaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @Operation(summary = "Crear recordatorio de cita", description = "Crea recordatorio automático para una cita")
    public ResponseEntity<RecordatorioDTO> crearRecordatorioCita(
            @PathVariable Long citaId,
            @RequestParam(defaultValue = "24") int horasAntes) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recordatorioService.crearRecordatorioCita(citaId, horasAntes));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @Operation(summary = "Actualizar recordatorio", description = "Actualiza un recordatorio programado")
    public ResponseEntity<RecordatorioDTO> actualizarRecordatorio(
            @PathVariable Long id,
            @Valid @RequestBody CrearRecordatorioDTO dto) {
        return ResponseEntity.ok(recordatorioService.actualizarRecordatorio(id, dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA', 'CLIENTE')")
    @Operation(summary = "Obtener recordatorio", description = "Obtiene un recordatorio por ID")
    public ResponseEntity<RecordatorioDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(recordatorioService.obtenerPorId(id));
    }

    @GetMapping("/destinatario/{destinatarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA', 'CLIENTE')")
    @Operation(summary = "Listar por destinatario", description = "Lista recordatorios de un usuario")
    public ResponseEntity<List<RecordatorioDTO>> listarPorDestinatario(@PathVariable Long destinatarioId) {
        return ResponseEntity.ok(recordatorioService.listarPorDestinatario(destinatarioId));
    }

    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @Operation(summary = "Listar pendientes", description = "Lista recordatorios pendientes de envío")
    public ResponseEntity<List<RecordatorioDTO>> listarPendientes() {
        return ResponseEntity.ok(recordatorioService.listarPendientes());
    }

    @GetMapping("/fecha")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @Operation(summary = "Listar por rango de fechas", description = "Lista recordatorios en un rango de fechas")
    public ResponseEntity<List<RecordatorioDTO>> listarPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(recordatorioService.listarPorRangoFechas(inicio, fin));
    }

    @PostMapping("/{id}/marcar-enviado")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @Operation(summary = "Marcar como enviado", description = "Marca manualmente un recordatorio como enviado")
    public ResponseEntity<RecordatorioDTO> marcarComoEnviado(@PathVariable Long id) {
        return ResponseEntity.ok(recordatorioService.marcarComoEnviado(id));
    }

    @PostMapping("/procesar-pendientes")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Procesar pendientes", description = "Procesa y envía recordatorios pendientes")
    public ResponseEntity<Void> procesarPendientes() {
        recordatorioService.procesarRecordatoriosPendientes();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @Operation(summary = "Cancelar recordatorio", description = "Cancela/desactiva un recordatorio")
    public ResponseEntity<Void> cancelarRecordatorio(@PathVariable Long id) {
        recordatorioService.cancelarRecordatorio(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cita/{citaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA', 'CLIENTE')")
    @Operation(summary = "Listar por cita", description = "Lista recordatorios de una cita específica")
    public ResponseEntity<List<RecordatorioDTO>> listarPorCita(@PathVariable Long citaId) {
        return ResponseEntity.ok(recordatorioService.listarPorCita(citaId));
    }
}
