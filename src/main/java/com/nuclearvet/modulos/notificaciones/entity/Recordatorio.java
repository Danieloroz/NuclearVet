package com.nuclearvet.modulos.notificaciones.entity;

import com.nuclearvet.common.entity.EntidadBase;
import com.nuclearvet.modulos.citas.entity.Cita;
import com.nuclearvet.modulos.pacientes.entity.Paciente;
import com.nuclearvet.modulos.usuarios.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa un recordatorio programado.
 * RF5.2: Gestión de recordatorios automáticos
 */
@Entity
@Table(name = "recordatorios")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recordatorio extends EntidadBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cita_id")
    private Cita cita;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoNotificacion tipo;

    @Column(nullable = false, length = 200)
    private String asunto;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "fecha_recordatorio", nullable = false)
    private LocalDateTime fechaRecordatorio;

    @Column(name = "recordatorio_enviado")
    @Builder.Default
    private Boolean recordatorioEnviado = false;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    /**
     * Canales por los cuales se debe enviar el recordatorio
     * Se guardan como String separados por comas: "EMAIL,SMS,IN_APP"
     */
    @Column(nullable = false, length = 100)
    private String canales;

    /**
     * ID de la notificación generada cuando se envía el recordatorio
     */
    @Column(name = "notificacion_id")
    private Long notificacionId;

    /**
     * Indica si es un recordatorio recurrente
     */
    @Column(name = "es_recurrente")
    @Builder.Default
    private Boolean esRecurrente = false;

    /**
     * Frecuencia en días para recordatorios recurrentes
     */
    @Column(name = "frecuencia_dias")
    private Integer frecuenciaDias;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    /**
     * Verifica si el recordatorio debe enviarse ahora
     */
    public boolean debeEnviarse() {
        return !recordatorioEnviado && 
               fechaRecordatorio.isBefore(LocalDateTime.now());
    }

    /**
     * Marca el recordatorio como enviado
     */
    public void marcarComoEnviado(Long notificacionId) {
        this.recordatorioEnviado = true;
        this.fechaEnvio = LocalDateTime.now();
        this.notificacionId = notificacionId;
    }

    /**
     * Verifica si es un recordatorio de cita
     */
    public boolean esRecordatorioCita() {
        return this.cita != null || this.tipo == TipoNotificacion.RECORDATORIO_CITA;
    }

    /**
     * Verifica si es un recordatorio de vacunación
     */
    public boolean esRecordatorioVacuna() {
        return this.tipo == TipoNotificacion.RECORDATORIO_VACUNA;
    }
}
