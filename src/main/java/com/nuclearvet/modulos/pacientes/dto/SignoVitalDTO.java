package com.nuclearvet.modulos.pacientes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para registrar signos vitales.
 * Implementa RF2.4 - Registro de signos vitales
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignoVitalDTO {
    
    private Long id;
    private Long consultaId;
    private LocalDateTime fechaRegistro;
    private Double peso;
    private Double temperatura;
    private Integer frecuenciaCardiaca;
    private Integer frecuenciaRespiratoria;
    private String presionArterial;
    private String mucosas;
    private Double tiempoLlenadoCapilar;
    private String nivelHidratacion;
    private Integer condicionCorporal;
    private String observaciones;
}
