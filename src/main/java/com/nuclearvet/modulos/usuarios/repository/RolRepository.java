package com.nuclearvet.modulos.usuarios.repository;

import com.nuclearvet.modulos.usuarios.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Rol.
 * Patrón: Repository Pattern
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    
    /**
     * Busca un rol por su nombre.
     */
    Optional<Rol> findByNombre(String nombre);
    
    /**
     * Verifica si existe un rol con el nombre dado.
     */
    boolean existsByNombre(String nombre);
}
