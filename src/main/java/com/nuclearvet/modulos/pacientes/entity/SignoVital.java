package com.nuclearvet.modulos.pacientes.entity;

import com.nuclearvet.common.entity.EntidadBase;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa los Signos Vitales registrados en una consulta.
 * Implementa RF2.4 - Registro de signos vitales
 */
@Entity
@Table(name = "signos_vitales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignoVital extends EntidadBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consulta_id", nullable = false)
    private Consulta consulta;
    
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;
    
    @Column(name = "peso")
    private Double peso; // kg
    
    @Column(name = "temperatura")
    private Double temperatura; // °C
    
    @Column(name = "frecuencia_cardiaca")
    private Integer frecuenciaCardiaca; // latidos por minuto
    
    @Column(name = "frecuencia_respiratoria")
    private Integer frecuenciaRespiratoria; // respiraciones por minuto
    
    @Column(name = "presion_arterial", length = 20)
    private String presionArterial; // Ej: "120/80"
    
    @Column(name = "mucosas", length = 100)
    private String mucosas; // Rosadas, pálidas, cianóticas, etc.
    
    @Column(name = "tiempo_llenado_capilar")
    private Double tiempoLlenadoCapilar; // segundos
    
    @Column(name = "nivel_hidratacion", length = 50)
    private String nivelHidratacion; // Normal, Leve, Moderada, Severa
    
    @Column(name = "condicion_corporal")
    private Integer condicionCorporal; // Escala 1-9
    
    @Column(name = "observaciones", length = 500)
    private String observaciones;
}
