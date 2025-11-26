package com.nuclearvet.modulos.usuarios.repository;

import com.nuclearvet.modulos.usuarios.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Usuario.
 * Patrón: Repository Pattern
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    /**
     * Busca un usuario por email.
     * Usado para autenticación (RF1.3).
     */
    Optional<Usuario> findByEmail(String email);
    
    /**
     * Busca un usuario por documento de identidad.
     */
    Optional<Usuario> findByDocumentoIdentidad(String documentoIdentidad);
    
    /**
     * Verifica si existe un usuario con el email dado.
     */
    boolean existsByEmail(String email);
    
    /**
     * Verifica si existe un usuario con el documento dado.
     */
    boolean existsByDocumentoIdentidad(String documentoIdentidad);
    
    /**
     * Busca usuarios activos.
     */
    List<Usuario> findByActivoTrue();
    
    /**
     * Busca un usuario por token de recuperación.
     * Usado para recuperación de contraseña (RF1.4).
     */
    Optional<Usuario> findByTokenRecuperacion(String token);
    
    /**
     * Busca usuarios por rol.
     */
    @Query("SELECT u FROM Usuario u JOIN u.roles r WHERE r.nombre = :nombreRol AND u.activo = true")
    List<Usuario> findByRolNombre(String nombreRol);
}
