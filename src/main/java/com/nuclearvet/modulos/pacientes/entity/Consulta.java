package com.nuclearvet.modulos.pacientes.entity;

import com.nuclearvet.common.entity.EntidadBase;
import com.nuclearvet.modulos.usuarios.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa una Consulta o Servicio Veterinario.
 * Implementa RF2.4 - Registro de consulta o servicio
 * 
 * Tipos de servicio: Consulta general, Cirugía, Vacunación, Desparasitación, etc.
 */
@Entity
@Table(name = "consultas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consulta extends EntidadBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "historia_clinica_id", nullable = false)
    private HistoriaClinica historiaClinica;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Usuario veterinario;
    
    @Column(name = "fecha_consulta", nullable = false)
    private LocalDateTime fechaConsulta;
    
    @Column(name = "tipo_servicio", nullable = false, length = 100)
    private String tipoServicio; // Consulta, Cirugía, Vacunación, etc.
    
    @Column(name = "motivo_consulta", nullable = false, length = 500)
    private String motivoConsulta;
    
    @Column(name = "anamnesis", length = 2000)
    private String anamnesis; // Historia que cuenta el dueño
    
    @Column(name = "examen_fisico", length = 2000)
    private String examenFisico;
    
    @Column(name = "diagnostico_principal", length = 500)
    private String diagnosticoPrincipal;
    
    @Column(name = "diagnosticos_secundarios", length = 1000)
    private String diagnosticosSecundarios;
    
    @Column(name = "tratamiento", length = 2000)
    private String tratamiento;
    
    @Column(name = "recomendaciones", length = 1000)
    private String recomendaciones;
    
    @Column(name = "proxima_cita")
    private LocalDateTime proximaCita;
    
    @Column(name = "archivos_adjuntos", length = 1000)
    private String archivosAdjuntos; // URLs separadas por coma
    
    @Column(name = "costo_consulta")
    private Double costoConsulta;
    
    @Column(name = "estado", length = 50)
    @Builder.Default
    private String estado = "COMPLETADA"; // COMPLETADA, PENDIENTE_RESULTADOS, EN_SEGUIMIENTO
}
