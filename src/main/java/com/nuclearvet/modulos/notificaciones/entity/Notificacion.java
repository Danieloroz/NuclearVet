package com.nuclearvet.modulos.notificaciones.entity;

import com.nuclearvet.common.entity.EntidadBase;
import com.nuclearvet.modulos.usuarios.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa una notificación enviada a un usuario.
 * RF5.1: Gestión de notificaciones del sistema
 */
@Entity
@Table(name = "notificaciones")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion extends EntidadBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario destinatario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoNotificacion tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CanalNotificacion canal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoNotificacion estado;

    @Column(nullable = false, length = 200)
    private String asunto;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "fecha_programada")
    private LocalDateTime fechaProgramada;

    @Column(name = "fecha_enviada")
    private LocalDateTime fechaEnviada;

    @Column(name = "fecha_leida")
    private LocalDateTime fechaLeida;

    @Column(name = "intentos_envio")
    @Builder.Default
    private Integer intentosEnvio = 0;

    @Column(name = "error_mensaje", columnDefinition = "TEXT")
    private String errorMensaje;

    /**
     * ID de referencia a la entidad relacionada (Cita, Paciente, etc.)
     */
    @Column(name = "referencia_id")
    private Long referenciaId;

    /**
     * Tipo de entidad referenciada (CITA, PACIENTE, PRODUCTO, etc.)
     */
    @Column(name = "referencia_tipo", length = 50)
    private String referenciaTipo;

    /**
     * Marca la notificación como leída
     */
    public void marcarComoLeida() {
        this.estado = EstadoNotificacion.LEIDA;
        this.fechaLeida = LocalDateTime.now();
    }

    /**
     * Marca la notificación como enviada
     */
    public void marcarComoEnviada() {
        this.estado = EstadoNotificacion.ENVIADA;
        this.fechaEnviada = LocalDateTime.now();
    }

    /**
     * Registra un error en el envío
     */
    public void registrarError(String mensaje) {
        this.estado = EstadoNotificacion.ERROR;
        this.errorMensaje = mensaje;
        this.intentosEnvio++;
    }

    /**
     * Verifica si la notificación está pendiente de envío
     */
    public boolean estaPendiente() {
        return this.estado == EstadoNotificacion.PENDIENTE;
    }

    /**
     * Verifica si debe enviarse ahora (fecha programada alcanzada)
     */
    public boolean debeEnviarse() {
        return estaPendiente() && 
               (fechaProgramada == null || fechaProgramada.isBefore(LocalDateTime.now()));
    }
}
