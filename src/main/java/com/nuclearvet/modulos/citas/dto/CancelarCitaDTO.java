package com.nuclearvet.modulos.citas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para cancelar una cita.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelarCitaDTO {

    @NotBlank(message = "El motivo de cancelación es obligatorio")
    private String motivoCancelacion;
}
