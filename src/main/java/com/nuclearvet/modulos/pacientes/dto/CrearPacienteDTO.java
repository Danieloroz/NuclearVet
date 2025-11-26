package com.nuclearvet.modulos.pacientes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para crear un nuevo paciente.
 * Implementa RF2.1 - Registro de pacientes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearPacienteDTO {
    
    @NotBlank(message = "El nombre del paciente es obligatorio, parce")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;
    
    @NotBlank(message = "La especie es obligatoria")
    @Size(max = 50, message = "La especie no puede tener más de 50 caracteres")
    private String especie;
    
    @Size(max = 100, message = "La raza no puede tener más de 100 caracteres")
    private String raza;
    
    private LocalDate fechaNacimiento;
    
    @Size(max = 10, message = "El sexo no puede tener más de 10 caracteres")
    private String sexo;
    
    @Size(max = 50, message = "El color no puede tener más de 50 caracteres")
    private String color;
    
    private Double pesoActual;
    
    @Size(max = 50, message = "El microchip no puede tener más de 50 caracteres")
    private String microchip;
    
    private Boolean esterilizado;
    
    @Size(max = 500, message = "Las alergias no pueden tener más de 500 caracteres")
    private String alergias;
    
    @Size(max = 1000, message = "Las observaciones no pueden tener más de 1000 caracteres")
    private String observaciones;
    
    @NotNull(message = "El ID del propietario es obligatorio")
    private Long propietarioId;
}
