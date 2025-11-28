package com.nuclearvet.modulos.notificaciones.dto;

import com.nuclearvet.modulos.notificaciones.entity.CanalNotificacion;
import com.nuclearvet.modulos.notificaciones.entity.TipoNotificacion;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para crear un nuevo recordatorio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearRecordatorioDTO {

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    private Long pacienteId;

    private Long citaId;

    @NotNull(message = "El tipo de recordatorio es obligatorio")
    private TipoNotificacion tipo;

    @NotBlank(message = "El asunto es obligatorio")
    @Size(max = 200, message = "El asunto no puede exceder 200 caracteres")
    private String asunto;

    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;

    @NotNull(message = "La fecha del recordatorio es obligatoria")
    @Future(message = "La fecha del recordatorio debe ser futura")
    private LocalDateTime fechaRecordatorio;

    @NotNull(message = "Debe especificar al menos un canal de envío")
    private List<CanalNotificacion> canales;

    private Boolean esRecurrente;

    private Integer frecuenciaDias;

    private String observaciones;
}
