package com.nuclearvet.modulos.notificaciones.dto;

import com.nuclearvet.modulos.notificaciones.entity.CanalNotificacion;
import com.nuclearvet.modulos.notificaciones.entity.TipoNotificacion;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para crear una nueva notificación.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearNotificacionDTO {

    @NotNull(message = "El destinatario es obligatorio")
    private Long destinatarioId;

    @NotNull(message = "El tipo de notificación es obligatorio")
    private TipoNotificacion tipo;

    @NotNull(message = "El canal de envío es obligatorio")
    private CanalNotificacion canal;

    @NotBlank(message = "El asunto es obligatorio")
    @Size(max = 200, message = "El asunto no puede exceder 200 caracteres")
    private String asunto;

    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;

    @FutureOrPresent(message = "La fecha programada debe ser presente o futura")
    private LocalDateTime fechaProgramada;

    private Long referenciaId;

    @Size(max = 50, message = "El tipo de referencia no puede exceder 50 caracteres")
    private String referenciaTipo;
}
