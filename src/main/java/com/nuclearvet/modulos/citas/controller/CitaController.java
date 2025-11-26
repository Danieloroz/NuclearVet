package com.nuclearvet.modulos.citas.controller;

import com.nuclearvet.common.dto.RespuestaExitosa;
import com.nuclearvet.modulos.citas.dto.CancelarCitaDTO;
import com.nuclearvet.modulos.citas.dto.CitaDTO;
import com.nuclearvet.modulos.citas.dto.CrearCitaDTO;
import com.nuclearvet.modulos.citas.service.CitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de citas.
 * Implementa RF3.1 a RF3.5
 */
@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
@Tag(name = "Citas", description = "Endpoints para gestión de citas veterinarias")
@SecurityRequirement(name = "Bearer Authentication")
public class CitaController {

    private final CitaService citaService;

    /**
     * RF3.1: Crear una nueva cita
     */
    @Operation(
            summary = "Crear nueva cita",
            description = "Programa una nueva cita para un paciente con un veterinario. Valida disponibilidad automáticamente."
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<RespuestaExitosa<CitaDTO>> crearCita(
            @Valid @RequestBody CrearCitaDTO dto) {

        CitaDTO cita = citaService.crearCita(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(RespuestaExitosa.crear(cita, "Cita creada exitosamente, parce"));
    }

    /**
     * RF3.2: Actualizar una cita
     */
    @Operation(
            summary = "Actualizar cita",
            description = "Modifica los datos de una cita existente. No permite actualizar citas completadas o canceladas."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<RespuestaExitosa<CitaDTO>> actualizarCita(
            @PathVariable Long id,
            @Valid @RequestBody CrearCitaDTO dto) {

        CitaDTO cita = citaService.actualizarCita(id, dto);
        return ResponseEntity.ok(RespuestaExitosa.crear(cita, "Cita actualizada correctamente"));
    }

    /**
     * RF3.3: Cancelar una cita
     */
    @Operation(
            summary = "Cancelar cita",
            description = "Cancela una cita programada. Requiere motivo de cancelación."
    )
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<RespuestaExitosa<CitaDTO>> cancelarCita(
            @PathVariable Long id,
            @Valid @RequestBody CancelarCitaDTO dto) {

        CitaDTO cita = citaService.cancelarCita(id, dto);
        return ResponseEntity.ok(RespuestaExitosa.crear(cita, "Cita cancelada"));
    }

    /**
     * RF3.4: Consultar agenda de un veterinario
     */
    @Operation(
            summary = "Consultar agenda",
            description = "Obtiene todas las citas de un veterinario para una fecha específica."
    )
    @GetMapping("/agenda/{veterinarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<RespuestaExitosa<List<CitaDTO>>> consultarAgenda(
            @PathVariable Long veterinarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        List<CitaDTO> citas = citaService.consultarAgenda(veterinarioId, fecha);
        return ResponseEntity.ok(RespuestaExitosa.crear(citas));
    }

    /**
     * RF3.5: Verificar disponibilidad
     */
    @Operation(
            summary = "Verificar disponibilidad",
            description = "Verifica si un veterinario está disponible en una fecha y hora específica."
    )
    @GetMapping("/disponibilidad/{veterinarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<RespuestaExitosa<Map<String, Object>>> verificarDisponibilidad(
            @PathVariable Long veterinarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora,
            @RequestParam(defaultValue = "30") Integer duracionMinutos) {

        boolean disponible = citaService.verificarDisponibilidad(veterinarioId, fechaHora, duracionMinutos);

        Map<String, Object> response = new HashMap<>();
        response.put("disponible", disponible);
        response.put("veterinarioId", veterinarioId);
        response.put("fechaHora", fechaHora);
        response.put("duracionMinutos", duracionMinutos);

        String mensaje = disponible
                ? "El veterinario está disponible en ese horario"
                : "El veterinario ya tiene una cita en ese horario";

        return ResponseEntity.ok(RespuestaExitosa.crear(response, mensaje));
    }

    /**
     * Obtener cita por ID
     */
    @Operation(summary = "Obtener cita por ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<RespuestaExitosa<CitaDTO>> obtenerPorId(@PathVariable Long id) {
        CitaDTO cita = citaService.obtenerPorId(id);
        return ResponseEntity.ok(RespuestaExitosa.crear(cita));
    }

    /**
     * Listar citas del día actual
     */
    @Operation(
            summary = "Citas del día",
            description = "Obtiene todas las citas programadas para el día actual."
    )
    @GetMapping("/hoy")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<RespuestaExitosa<List<CitaDTO>>> citasDelDia() {
        List<CitaDTO> citas = citaService.citasDelDia();
        return ResponseEntity.ok(RespuestaExitosa.crear(citas, "Citas del día"));
    }

    /**
     * Confirmar una cita
     */
    @Operation(summary = "Confirmar cita")
    @PostMapping("/{id}/confirmar")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<RespuestaExitosa<CitaDTO>> confirmarCita(@PathVariable Long id) {
        CitaDTO cita = citaService.confirmarCita(id);
        return ResponseEntity.ok(RespuestaExitosa.crear(cita, "Cita confirmada"));
    }

    /**
     * Iniciar una cita
     */
    @Operation(summary = "Iniciar cita")
    @PostMapping("/{id}/iniciar")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<RespuestaExitosa<CitaDTO>> iniciarCita(@PathVariable Long id) {
        CitaDTO cita = citaService.iniciarCita(id);
        return ResponseEntity.ok(RespuestaExitosa.crear(cita, "Cita iniciada"));
    }

    /**
     * Completar una cita
     */
    @Operation(summary = "Completar cita")
    @PostMapping("/{id}/completar")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<RespuestaExitosa<CitaDTO>> completarCita(@PathVariable Long id) {
        CitaDTO cita = citaService.completarCita(id);
        return ResponseEntity.ok(RespuestaExitosa.crear(cita, "Cita completada exitosamente"));
    }

    /**
     * Marcar como "no asistió"
     */
    @Operation(summary = "Marcar como no asistió")
    @PostMapping("/{id}/no-asistio")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<RespuestaExitosa<CitaDTO>> marcarNoAsistio(@PathVariable Long id) {
        CitaDTO cita = citaService.marcarNoAsistio(id);
        return ResponseEntity.ok(RespuestaExitosa.crear(cita, "Cita marcada como no asistió"));
    }
}
