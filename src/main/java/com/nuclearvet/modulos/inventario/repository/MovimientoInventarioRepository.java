package com.nuclearvet.modulos.inventario.repository;

import com.nuclearvet.modulos.inventario.entity.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para Movimientos de Inventario.
 * RF4.4
 */
@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
    
    // Movimientos de un producto específico
    List<MovimientoInventario> findByProductoIdOrderByFechaMovimientoDesc(Long productoId);
    
    // Movimientos por tipo
    List<MovimientoInventario> findByTipoMovimientoOrderByFechaMovimientoDesc(String tipoMovimiento);
    
    // Movimientos en un rango de fechas
    @Query("SELECT m FROM MovimientoInventario m WHERE m.fechaMovimiento BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fechaMovimiento DESC")
    List<MovimientoInventario> findByFechaMovimientoBetween(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
    
    // Movimientos por usuario
    List<MovimientoInventario> findByUsuarioIdOrderByFechaMovimientoDesc(Long usuarioId);
    
    // Últimos movimientos
    List<MovimientoInventario> findTop10ByOrderByFechaMovimientoDesc();
    
    // Movimientos por proveedor
    List<MovimientoInventario> findByProveedorIdOrderByFechaMovimientoDesc(Long proveedorId);
}
