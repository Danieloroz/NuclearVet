package com.nuclearvet.modulos.pacientes.controller;

import com.nuclearvet.common.dto.RespuestaExitosa;
import com.nuclearvet.modulos.pacientes.dto.ConsultaDTO;
import com.nuclearvet.modulos.pacientes.dto.CrearConsultaDTO;
import com.nuclearvet.modulos.pacientes.dto.HistoriaClinicaDTO;
import com.nuclearvet.modulos.pacientes.service.ConsultaService;
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
import java.util.List;

@RestController
@RequestMapping("/api/consultas")
@RequiredArgsConstructor
@Tag(name = "Consultas", description = "Gestión de consultas y atenciones veterinarias")
@SecurityRequirement(name = "Bearer Authentication")
public class ConsultaController {

    private final ConsultaService consultaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('VETERINARIO', 'ADMIN')")
    @Operation(summary = "Registrar consulta/atención", description = "Registra una consulta con signos vitales, diagnóstico y tratamiento")
    public ResponseEntity<RespuestaExitosa<ConsultaDTO>> registrarConsulta(@Valid @RequestBody CrearConsultaDTO dto) {
        ConsultaDTO consulta = consultaService.registrarConsulta(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(RespuestaExitosa.crear(consulta, "Consulta registrada exitosamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VETERINARIO', 'ADMIN', 'ASISTENTE')")
    @Operation(summary = "Obtener consulta por ID")
    public ResponseEntity<RespuestaExitosa<ConsultaDTO>> obtenerPorId(@PathVariable Long id) {
        ConsultaDTO consulta = consultaService.obtenerPorId(id);
        return ResponseEntity.ok(RespuestaExitosa.crear(consulta));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('VETERINARIO', 'ADMIN')")
    @Operation(summary = "Actualizar consulta")
    public ResponseEntity<RespuestaExitosa<ConsultaDTO>> actualizarConsulta(
            @PathVariable Long id,
            @Valid @RequestBody CrearConsultaDTO dto) {
        ConsultaDTO consulta = consultaService.actualizarConsulta(id, dto);
        return ResponseEntity.ok(RespuestaExitosa.crear(consulta, "Consulta actualizada"));
    }

    @GetMapping("/evolucion/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('VETERINARIO', 'ADMIN', 'ASISTENTE', 'CLIENTE')")
    @Operation(summary = "Consultar evolución del paciente", description = "Retorna la historia clínica completa con todas las consultas")
    public ResponseEntity<RespuestaExitosa<HistoriaClinicaDTO>> obtenerEvolucionPaciente(@PathVariable Long pacienteId) {
        HistoriaClinicaDTO evolucion = consultaService.obtenerEvolucionPaciente(pacienteId);
        return ResponseEntity.ok(RespuestaExitosa.crear(evolucion));
    }

    @GetMapping("/veterinario/{veterinarioId}")
    @PreAuthorize("hasAnyRole('VETERINARIO', 'ADMIN')")
    @Operation(summary = "Listar consultas de un veterinario")
    public ResponseEntity<RespuestaExitosa<List<ConsultaDTO>>> listarPorVeterinario(@PathVariable Long veterinarioId) {
        List<ConsultaDTO> consultas = consultaService.listarPorVeterinario(veterinarioId);
        return ResponseEntity.ok(RespuestaExitosa.crear(consultas));
    }

    @GetMapping("/fechas")
    @PreAuthorize("hasAnyRole('VETERINARIO', 'ADMIN', 'ASISTENTE')")
    @Operation(summary = "Listar consultas por rango de fechas")
    public ResponseEntity<RespuestaExitosa<List<ConsultaDTO>>> listarPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<ConsultaDTO> consultas = consultaService.listarPorRangoFechas(fechaInicio, fechaFin);
        return ResponseEntity.ok(RespuestaExitosa.crear(consultas));
    }

    @GetMapping("/contar/historia/{historiaClinicaId}")
    @PreAuthorize("hasAnyRole('VETERINARIO', 'ADMIN', 'ASISTENTE')")
    @Operation(summary = "Contar consultas de un paciente")
    public ResponseEntity<RespuestaExitosa<Long>> contarConsultasPaciente(@PathVariable Long historiaClinicaId) {
        Long total = consultaService.contarConsultasPaciente(historiaClinicaId);
        return ResponseEntity.ok(RespuestaExitosa.crear(total));
    }
}
