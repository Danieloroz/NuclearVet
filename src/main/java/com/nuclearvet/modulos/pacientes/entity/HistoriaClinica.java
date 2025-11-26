package com.nuclearvet.modulos.pacientes.entity;

import com.nuclearvet.common.entity.EntidadBase;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa una Historia Clínica única por paciente.
 * Implementa RF2.3 - Creación de historia clínica
 * 
 * Cada paciente tiene UNA historia clínica que agrupa todas sus consultas.
 */
@Entity
@Table(name = "historias_clinicas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriaClinica extends EntidadBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "paciente_id", nullable = false, unique = true)
    private Paciente paciente;
    
    @Column(name = "numero_historia", nullable = false, unique = true, length = 50)
    private String numeroHistoria; // Ej: HC-2025-0001
    
    @Column(name = "fecha_apertura", nullable = false)
    private LocalDateTime fechaApertura;
    
    @Column(name = "observaciones_generales", length = 2000)
    private String observacionesGenerales;
    
    /**
     * Genera el número de historia clínica.
     */
    public static String generarNumeroHistoria(Long idPaciente) {
        int anio = LocalDateTime.now().getYear();
        return String.format("HC-%d-%05d", anio, idPaciente);
    }
}
