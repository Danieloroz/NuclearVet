package com.nuclearvet.modulos.administrativo.repository;

import com.nuclearvet.modulos.administrativo.entity.MetodoPago;
import com.nuclearvet.modulos.administrativo.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestión de pagos.
 * RF6.2 - Registro de pagos
 */
@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    // Buscar pago por número de recibo
    Optional<Pago> findByNumeroRecibo(String numeroRecibo);

    // Listar pagos de una factura
    List<Pago> findByFacturaIdOrderByFechaPagoDesc(Long facturaId);

    // Listar pagos por método de pago
    List<Pago> findByMetodoPagoOrderByFechaPagoDesc(MetodoPago metodoPago);

    // Listar pagos en rango de fechas
    List<Pago> findByFechaPagoBetweenOrderByFechaPagoDesc(
            LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Listar pagos recibidos por un usuario
    List<Pago> findByRecibidoPorIdOrderByFechaPagoDesc(Long usuarioId);

    // Total de pagos en un rango de fechas
    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p " +
           "WHERE p.fechaPago BETWEEN :fechaInicio AND :fechaFin " +
           "AND p.activo = true")
    BigDecimal calcularTotalPagos(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    // Total de pagos por método en un rango de fechas
    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p " +
           "WHERE p.metodoPago = :metodoPago " +
           "AND p.fechaPago BETWEEN :fechaInicio AND :fechaFin " +
           "AND p.activo = true")
    BigDecimal calcularTotalPorMetodo(
            @Param("metodoPago") MetodoPago metodoPago,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    // Obtener el último número de recibo
    @Query("SELECT p FROM Pago p WHERE p.activo = true ORDER BY p.id DESC")
    Optional<Pago> findLastPago();
}
