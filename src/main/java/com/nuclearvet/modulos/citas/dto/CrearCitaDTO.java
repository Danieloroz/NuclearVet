package com.nuclearvet.modulos.citas.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para crear una nueva cita.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearCitaDTO {

    @NotNull(message = "El ID del paciente es obligatorio")
    private Long pacienteId;

    @NotNull(message = "El ID del veterinario es obligatorio")
    private Long veterinarioId;

    @NotNull(message = "La fecha y hora son obligatorias")
    @Future(message = "La cita debe ser en una fecha futura")
    private LocalDateTime fechaHora;

    @NotBlank(message = "El tipo de servicio es obligatorio")
    private String tipoServicio;

    private String motivo;

    @Positive(message = "La duración debe ser positiva")
    private Integer duracionMinutos;

    private String observaciones;
}
