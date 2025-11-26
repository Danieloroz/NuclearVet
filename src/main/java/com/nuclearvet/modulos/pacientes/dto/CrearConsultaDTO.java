package com.nuclearvet.modulos.pacientes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para crear una nueva consulta.
 * Implementa RF2.4 - Registro de consulta o servicio
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearConsultaDTO {
    
    @NotNull(message = "El ID de la historia clínica es obligatorio")
    private Long historiaClinicaId;
    
    @NotNull(message = "El ID del veterinario es obligatorio")
    private Long veterinarioId;
    
    private LocalDateTime fechaConsulta;
    
    @NotBlank(message = "El tipo de servicio es obligatorio")
    @Size(max = 100)
    private String tipoServicio;
    
    @NotBlank(message = "El motivo de consulta es obligatorio, llave")
    @Size(max = 500)
    private String motivoConsulta;
    
    @Size(max = 2000)
    private String anamnesis;
    
    @Size(max = 2000)
    private String examenFisico;
    
    @Size(max = 500)
    private String diagnosticoPrincipal;
    
    @Size(max = 1000)
    private String diagnosticosSecundarios;
    
    @Size(max = 2000)
    private String tratamiento;
    
    @Size(max = 1000)
    private String recomendaciones;
    
    private LocalDateTime proximaCita;
    
    private Double costoConsulta;
    
    // Signos vitales de la consulta
    private SignoVitalDTO signosVitales;
}
