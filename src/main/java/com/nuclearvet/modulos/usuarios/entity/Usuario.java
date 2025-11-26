package com.nuclearvet.modulos.usuarios.entity;

import com.nuclearvet.common.entity.EntidadBase;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa un Usuario en el sistema.
 * Implementa los requisitos RF1.1, RF1.2, RF1.3 del módulo de Usuarios y Accesos.
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario extends EntidadBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;
    
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;
    
    @Column(name = "contrasena", nullable = false)
    private String contrasena;
    
    @Column(name = "telefono", length = 20)
    private String telefono;
    
    @Column(name = "documento_identidad", unique = true, length = 50)
    private String documentoIdentidad;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    @Builder.Default
    private Set<Rol> roles = new HashSet<>();
    
    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;
    
    @Column(name = "intentos_fallidos")
    @Builder.Default
    private Integer intentosFallidos = 0;
    
    @Column(name = "bloqueado")
    @Builder.Default
    private Boolean bloqueado = false;
    
    @Column(name = "token_recuperacion")
    private String tokenRecuperacion;
    
    @Column(name = "token_recuperacion_expiracion")
    private LocalDateTime tokenRecuperacionExpiracion;
    
    /**
     * Obtiene el nombre completo del usuario.
     */
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
    
    /**
     * Verifica si el usuario tiene un rol específico.
     */
    public boolean tieneRol(String nombreRol) {
        return roles.stream()
                .anyMatch(rol -> rol.getNombre().equals(nombreRol));
    }
}
