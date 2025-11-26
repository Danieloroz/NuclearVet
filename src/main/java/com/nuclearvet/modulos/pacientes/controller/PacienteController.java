package com.nuclearvet.modulos.pacientes.controller;

import com.nuclearvet.common.dto.RespuestaExitosa;
import com.nuclearvet.modulos.pacientes.dto.CrearPacienteDTO;
import com.nuclearvet.modulos.pacientes.dto.PacienteDTO;
import com.nuclearvet.modulos.pacientes.service.PacienteService;
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

@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
@Tag(name = "Pacientes", description = "Gestión de pacientes veterinarios")
@SecurityRequirement(name = "Bearer Authentication")
public class PacienteController {

    private final PacienteService pacienteService;

    @PostMapping
    @PreAuthorize("hasAnyRole('VETERINARIO', 'ADMIN', 'ASISTENTE')")
    @Operation(summary = "Registrar nuevo paciente", description = "Crea un paciente y su historia clínica automáticamente")
    public ResponseEntity<RespuestaExitosa<PacienteDTO>> crearPaciente(@Valid @RequestBody CrearPacienteDTO dto) {
        PacienteDTO paciente = pacienteService.crearPaciente(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(RespuestaExitosa.crear(paciente, "Paciente registrado exitosamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('VETERINARIO', 'ADMIN', 'ASISTENTE')")
    @Operation(summary = "Actualizar datos del paciente")
    public ResponseEntity<RespuestaExitosa<PacienteDTO>> actualizarPaciente(
            @PathVariable Long id,
            @Valid @RequestBody CrearPacienteDTO dto) {
        PacienteDTO paciente = pacienteService.actualizarPaciente(id, dto);
        return ResponseEntity.ok(RespuestaExitosa.crear(paciente, "Paciente actualizado"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VETERINARIO', 'ADMIN', 'ASISTENTE', 'CLIENTE')")
    @Operation(summary = "Obtener paciente por ID")
    public ResponseEntity<RespuestaExitosa<PacienteDTO>> obtenerPorId(@PathVariable Long id) {
        PacienteDTO paciente = pacienteService.obtenerPorId(id);
        return ResponseEntity.ok(RespuestaExitosa.crear(paciente));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('VETERINARIO', 'ADMIN', 'ASISTENTE')")
    @Operation(summary = "Listar todos los pacientes activos")
    public ResponseEntity<RespuestaExitosa<List<PacienteDTO>>> listarTodos() {
        List<PacienteDTO> pacientes = pacienteService.listarTodos();
        return ResponseEntity.ok(RespuestaExitosa.crear(pacientes));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('VETERINARIO', 'ADMIN', 'ASISTENTE')")
    @Operation(summary = "Buscar pacientes por nombre")
    public ResponseEntity<RespuestaExitosa<List<PacienteDTO>>> buscarPorNombre(@RequestParam String nombre) {
        List<PacienteDTO> pacientes = pacienteService.buscarPorNombre(nombre);
        return ResponseEntity.ok(RespuestaExitosa.crear(pacientes));
    }

    @GetMapping("/propietario/{propietarioId}")
    @PreAuthorize("hasAnyRole('VETERINARIO', 'ADMIN', 'ASISTENTE', 'CLIENTE')")
    @Operation(summary = "Listar pacientes de un propietario")
    public ResponseEntity<RespuestaExitosa<List<PacienteDTO>>> listarPorPropietario(@PathVariable Long propietarioId) {
        List<PacienteDTO> pacientes = pacienteService.listarPorPropietario(propietarioId);
        return ResponseEntity.ok(RespuestaExitosa.crear(pacientes));
    }

    @GetMapping("/especie/{especie}")
    @PreAuthorize("hasAnyRole('VETERINARIO', 'ADMIN', 'ASISTENTE')")
    @Operation(summary = "Filtrar pacientes por especie")
    public ResponseEntity<RespuestaExitosa<List<PacienteDTO>>> listarPorEspecie(@PathVariable String especie) {
        List<PacienteDTO> pacientes = pacienteService.listarPorEspecie(especie);
        return ResponseEntity.ok(RespuestaExitosa.crear(pacientes));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    @Operation(summary = "Desactivar paciente")
    public ResponseEntity<RespuestaExitosa<Void>> desactivarPaciente(@PathVariable Long id) {
        pacienteService.desactivarPaciente(id);
        return ResponseEntity.ok(RespuestaExitosa.crear(null, "Paciente desactivado"));
    }
}
