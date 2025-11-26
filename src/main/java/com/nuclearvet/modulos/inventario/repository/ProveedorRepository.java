package com.nuclearvet.modulos.inventario.repository;

import com.nuclearvet.modulos.inventario.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para Proveedores.
 */
@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    
    Optional<Proveedor> findByNit(String nit);
    
    List<Proveedor> findByActivoTrueOrderByNombreAsc();
    
    List<Proveedor> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
    
    @Query("SELECT p FROM Proveedor p WHERE p.calificacion >= :calificacion AND p.activo = true ORDER BY p.calificacion DESC")
    List<Proveedor> findByCalificacionMayorIgualAndActivoTrue(Integer calificacion);
}
