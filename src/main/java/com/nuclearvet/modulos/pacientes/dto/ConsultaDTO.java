package com.nuclearvet.modulos.pacientes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para Consulta.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaDTO {
    
    private Long id;
    private Long historiaClinicaId;
    private String numeroHistoria;
    private String nombrePaciente;
    private Long veterinarioId;
    private String nombreVeterinario;
    private LocalDateTime fechaConsulta;
    private String tipoServicio;
    private String motivoConsulta;
    private String anamnesis;
    private String examenFisico;
    private String diagnosticoPrincipal;
    private String diagnosticosSecundarios;
    private String tratamiento;
    private String recomendaciones;
    private LocalDateTime proximaCita;
    private String archivosAdjuntos;
    private Double costoConsulta;
    private String estado;
    private LocalDateTime fechaCreacion;
}
