package com.nuclearvet.modulos.usuarios.entity;

import com.nuclearvet.common.entity.EntidadBase;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa un Rol en el sistema.
 * Los roles definen los permisos que tiene un usuario.
 * 
 * Roles del sistema:
 * - ROLE_ADMIN: Administrador con acceso completo
 * - ROLE_VETERINARIO: Veterinario con acceso a pacientes y atención clínica
 * - ROLE_ASISTENTE: Asistente con acceso limitado
 * - ROLE_CLIENTE: Cliente que puede agendar citas y ver sus mascotas
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol extends EntidadBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre", nullable = false, unique = true, length = 50)
    private String nombre; // ROLE_ADMIN, ROLE_VETERINARIO, ROLE_ASISTENTE, ROLE_CLIENTE
    
    @Column(name = "descripcion", length = 200)
    private String descripcion;
}
