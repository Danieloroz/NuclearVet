package com.nuclearvet.modulos.citas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para visualizar información de una cita.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CitaDTO {

    private Long id;
    private Long pacienteId;
    private String pacienteNombre;
    private Long veterinarioId;
    private String veterinarioNombre;
    private LocalDateTime fechaHora;
    private String tipoServicio;
    private String estado;
    private String motivo;
    private Integer duracionMinutos;
    private String observaciones;
    private String motivoCancelacion;
    private LocalDateTime fechaCreacion;
}
