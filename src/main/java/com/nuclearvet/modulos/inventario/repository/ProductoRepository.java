package com.nuclearvet.modulos.inventario.repository;

import com.nuclearvet.modulos.inventario.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para Productos.
 * RF4.1, RF4.3
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    Optional<Producto> findByCodigo(String codigo);
    
    List<Producto> findByActivoTrueOrderByNombreAsc();
    
    List<Producto> findByCategoriaIdAndActivoTrue(Long categoriaId);
    
    List<Producto> findByProveedorIdAndActivoTrue(Long proveedorId);
    
    // RF4.3: Productos con bajo stock
    @Query("SELECT p FROM Producto p WHERE p.stockActual <= p.stockMinimo AND p.activo = true ORDER BY p.stockActual ASC")
    List<Producto> findProductosConBajoStock();
    
    // Productos próximos a vencer
    @Query("SELECT p FROM Producto p WHERE p.fechaVencimiento BETWEEN :fechaInicio AND :fechaFin AND p.activo = true ORDER BY p.fechaVencimiento ASC")
    List<Producto> findProductosProximosAVencer(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );
    
    // Productos vencidos
    @Query("SELECT p FROM Producto p WHERE p.fechaVencimiento < CURRENT_DATE AND p.activo = true")
    List<Producto> findProductosVencidos();
    
    // Buscar productos por nombre
    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
    
    // Contar productos por categoría
    Long countByCategoriaIdAndActivoTrue(Long categoriaId);
}
