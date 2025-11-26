package com.nuclearvet.modulos.citas.entity;

import com.nuclearvet.common.entity.EntidadBase;
import com.nuclearvet.modulos.pacientes.entity.Paciente;
import com.nuclearvet.modulos.usuarios.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad para gestionar citas veterinarias.
 * RF3.1, RF3.2, RF3.3
 */
@Entity
@Table(name = "citas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Cita extends EntidadBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Usuario veterinario;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false, length = 50)
    private String tipoServicio; // CONSULTA, CIRUGIA, VACUNACION, CONTROL, URGENCIA

    @Column(length = 20)
    private String estado; // PROGRAMADA, CONFIRMADA, EN_CURSO, COMPLETADA, CANCELADA, NO_ASISTIO

    @Column(columnDefinition = "TEXT")
    private String motivo;

    @Column(length = 30)
    private Integer duracionMinutos;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(length = 100)
    private String motivoCancelacion;
}
