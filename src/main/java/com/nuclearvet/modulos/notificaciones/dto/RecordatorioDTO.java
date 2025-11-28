package com.nuclearvet.modulos.notificaciones.dto;

import com.nuclearvet.modulos.notificaciones.entity.TipoNotificacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de Recordatorio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordatorioDTO {
    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private Long pacienteId;
    private String pacienteNombre;
    private Long citaId;
    private TipoNotificacion tipo;
    private String asunto;
    private String mensaje;
    private LocalDateTime fechaRecordatorio;
    private Boolean recordatorioEnviado;
    private LocalDateTime fechaEnvio;
    private String canales;
    private Long notificacionId;
    private Boolean esRecurrente;
    private Integer frecuenciaDias;
    private String observaciones;
    private LocalDateTime fechaCreacion;
}
