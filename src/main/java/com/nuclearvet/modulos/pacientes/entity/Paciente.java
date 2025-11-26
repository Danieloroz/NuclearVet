package com.nuclearvet.modulos.pacientes.entity;

import com.nuclearvet.common.entity.EntidadBase;
import com.nuclearvet.modulos.usuarios.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entidad que representa un Paciente (mascota) en el sistema.
 * Implementa RF2.1 - Registro de pacientes
 * 
 * Relaciones:
 * - Pertenece a un Usuario (propietario/cliente)
 * - Tiene múltiples HistoriasClinicas
 */
@Entity
@Table(name = "pacientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paciente extends EntidadBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "especie", nullable = false, length = 50)
    private String especie; // Perro, Gato, Ave, etc.
    
    @Column(name = "raza", length = 100)
    private String raza;
    
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
    
    @Column(name = "sexo", length = 10)
    private String sexo; // Macho, Hembra
    
    @Column(name = "color", length = 50)
    private String color;
    
    @Column(name = "peso_actual")
    private Double pesoActual; // en kg
    
    @Column(name = "microchip", unique = true, length = 50)
    private String microchip;
    
    @Column(name = "esterilizado")
    private Boolean esterilizado;
    
    @Column(name = "alergias", length = 500)
    private String alergias;
    
    @Column(name = "observaciones", length = 1000)
    private String observaciones;
    
    @Column(name = "foto_url", length = 500)
    private String fotoUrl;
    
    // Relación con el propietario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id", nullable = false)
    private Usuario propietario;
    
    /**
     * Calcula la edad aproximada en años.
     */
    public Integer calcularEdadEnAnios() {
        if (fechaNacimiento == null) return null;
        return LocalDate.now().getYear() - fechaNacimiento.getYear();
    }
    
    /**
     * Retorna información básica del paciente.
     */
    public String getInfoBasica() {
        return String.format("%s - %s (%s)", nombre, especie, raza != null ? raza : "Sin raza");
    }
}
