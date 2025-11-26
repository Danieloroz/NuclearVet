package com.nuclearvet.modulos.inventario.repository;

import com.nuclearvet.modulos.inventario.entity.MovimientoInventario;
import com.nuclearvet.modulos.inventario.entity.TipoMovimiento;
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
    List<MovimientoInventario> findByTipoMovimientoOrderByFechaMovimientoDesc(TipoMovimiento tipoMovimiento);
    
    // Movimientos en un rango de fechas
    List<MovimientoInventario> findByFechaMovimientoBetweenOrderByFechaMovimientoDesc(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );
    
    // Movimientos por usuario
    List<MovimientoInventario> findByUsuarioIdOrderByFechaMovimientoDesc(Long usuarioId);
    
    // Últimos movimientos
    List<MovimientoInventario> findTop10ByOrderByFechaMovimientoDesc();
    
    // Movimientos por proveedor
    List<MovimientoInventario> findByProveedorIdOrderByFechaMovimientoDesc(Long proveedorId);
    
    // Contar movimientos por producto
    Long countByProductoId(Long productoId);
}
