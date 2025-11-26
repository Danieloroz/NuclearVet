package com.nuclearvet.modulos.pacientes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para Paciente.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PacienteDTO {
    
    private Long id;
    private String nombre;
    private String especie;
    private String raza;
    private LocalDate fechaNacimiento;
    private Integer edadEnAnios;
    private String sexo;
    private String color;
    private Double pesoActual;
    private String microchip;
    private Boolean esterilizado;
    private String alergias;
    private String observaciones;
    private String fotoUrl;
    private Long propietarioId;
    private String nombrePropietario;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}
