package com.nuclearvet.modulos.pacientes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para Historia Clínica completa.
 * Implementa RF2.6 - Ver evolución del paciente
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoriaClinicaDTO {
    
    private Long id;
    private String numeroHistoria;
    private LocalDateTime fechaApertura;
    private String observacionesGenerales;
    
    // Información del paciente
    private PacienteDTO paciente;
    
    // Lista de todas las consultas (evolución del paciente)
    private List<ConsultaDTO> consultas;
    
    private Long totalConsultas;
}
