package com.nuclearvet.modulos.notificaciones.dto;

import com.nuclearvet.modulos.notificaciones.entity.CanalNotificacion;
import com.nuclearvet.modulos.notificaciones.entity.EstadoNotificacion;
import com.nuclearvet.modulos.notificaciones.entity.TipoNotificacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de Notificacion.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionDTO {
    private Long id;
    private Long destinatarioId;
    private String destinatarioNombre;
    private TipoNotificacion tipo;
    private CanalNotificacion canal;
    private EstadoNotificacion estado;
    private String asunto;
    private String mensaje;
    private LocalDateTime fechaProgramada;
    private LocalDateTime fechaEnviada;
    private LocalDateTime fechaLeida;
    private Integer intentosEnvio;
    private String errorMensaje;
    private Long referenciaId;
    private String referenciaTipo;
    private LocalDateTime fechaCreacion;
}
