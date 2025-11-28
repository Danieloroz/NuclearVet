package com.nuclearvet.modulos.administrativo.repository;

import com.nuclearvet.modulos.administrativo.entity.EstadoFactura;
import com.nuclearvet.modulos.administrativo.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestión de facturas.
 * RF6.1 - Gestión de facturación
 */
@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    // Buscar por número de factura
    Optional<Factura> findByNumeroFactura(String numeroFactura);

    // Listar facturas por cliente/propietario
    List<Factura> findByPropietarioIdOrderByFechaEmisionDesc(Long propietarioId);

    // Listar facturas por paciente
    List<Factura> findByPacienteIdOrderByFechaEmisionDesc(Long pacienteId);

    // Listar facturas por estado
    List<Factura> findByEstadoOrderByFechaEmisionDesc(EstadoFactura estado);

    // Listar facturas por consulta
    List<Factura> findByConsultaIdOrderByFechaEmisionDesc(Long consultaId);

    // Listar facturas en rango de fechas
    List<Factura> findByFechaEmisionBetweenOrderByFechaEmisionDesc(
            LocalDate fechaInicio, LocalDate fechaFin);

    // Buscar facturas vencidas
    @Query("SELECT f FROM Factura f WHERE f.fechaVencimiento < :fecha " +
           "AND f.estado NOT IN ('PAGADA', 'CANCELADA') AND f.activo = true")
    List<Factura> findFacturasVencidas(@Param("fecha") LocalDate fecha);

    // Buscar facturas pendientes de pago
    @Query("SELECT f FROM Factura f WHERE f.estado = 'PENDIENTE' " +
           "AND f.activo = true ORDER BY f.fechaEmision DESC")
    List<Factura> findFacturasPendientes();

    // Total facturado en un rango de fechas
    @Query("SELECT COALESCE(SUM(f.total), 0) FROM Factura f " +
           "WHERE f.fechaEmision BETWEEN :fechaInicio AND :fechaFin " +
           "AND f.estado != 'CANCELADA' AND f.activo = true")
    BigDecimal calcularTotalFacturado(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    // Total recaudado (pagado) en un rango de fechas
    @Query("SELECT COALESCE(SUM(f.totalPagado), 0) FROM Factura f " +
           "WHERE f.fechaEmision BETWEEN :fechaInicio AND :fechaFin " +
           "AND f.activo = true")
    BigDecimal calcularTotalRecaudado(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    // Contar facturas por estado
    long countByEstadoAndActivoTrue(EstadoFactura estado);

    // Obtener el último número de factura para generar el siguiente
    @Query("SELECT f FROM Factura f WHERE f.activo = true ORDER BY f.id DESC")
    Optional<Factura> findLastFactura();
}
