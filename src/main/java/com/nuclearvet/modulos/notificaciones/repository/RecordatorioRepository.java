package com.nuclearvet.modulos.notificaciones.repository;

import com.nuclearvet.modulos.notificaciones.entity.Recordatorio;
import com.nuclearvet.modulos.notificaciones.entity.TipoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Recordatorio.
 */
@Repository
public interface RecordatorioRepository extends JpaRepository<Recordatorio, Long> {

    /**
     * Obtiene todos los recordatorios de un usuario
     */
    List<Recordatorio> findByUsuarioIdOrderByFechaRecordatorioDesc(Long usuarioId);

    /**
     * Obtiene recordatorios pendientes de envío
     */
    @Query("SELECT r FROM Recordatorio r WHERE r.recordatorioEnviado = false " +
           "AND r.fechaRecordatorio <= :ahora AND r.activo = true " +
           "ORDER BY r.fechaRecordatorio ASC")
    List<Recordatorio> findRecordatoriosPendientes(@Param("ahora") LocalDateTime ahora);

    /**
     * Obtiene recordatorios por tipo y usuario
     */
    List<Recordatorio> findByUsuarioIdAndTipoOrderByFechaRecordatorioDesc(
            Long usuarioId, TipoNotificacion tipo);

    /**
     * Obtiene recordatorios relacionados con una cita
     */
    List<Recordatorio> findByCitaIdOrderByFechaRecordatorioDesc(Long citaId);

    /**
     * Obtiene recordatorios relacionados con un paciente
     */
    List<Recordatorio> findByPacienteIdOrderByFechaRecordatorioDesc(Long pacienteId);

    /**
     * Obtiene recordatorios recurrentes activos
     */
    @Query("SELECT r FROM Recordatorio r WHERE r.esRecurrente = true " +
           "AND r.activo = true ORDER BY r.fechaRecordatorio ASC")
    List<Recordatorio> findRecordatoriosRecurrentes();

    /**
     * Obtiene recordatorios no enviados de un usuario
     */
    @Query("SELECT r FROM Recordatorio r WHERE r.usuario.id = :usuarioId " +
           "AND r.recordatorioEnviado = false AND r.activo = true " +
           "ORDER BY r.fechaRecordatorio ASC")
    List<Recordatorio> findRecordatoriosNoEnviados(@Param("usuarioId") Long usuarioId);

    /**
     * Obtiene recordatorios de citas próximas
     */
    @Query("SELECT r FROM Recordatorio r WHERE r.cita IS NOT NULL " +
           "AND r.recordatorioEnviado = false " +
           "AND r.fechaRecordatorio BETWEEN :inicio AND :fin " +
           "ORDER BY r.fechaRecordatorio ASC")
    List<Recordatorio> findRecordatoriosCitasProximas(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Obtiene recordatorios por rango de fechas
     */
    List<Recordatorio> findByFechaRecordatorioBetweenOrderByFechaRecordatorioAsc(
            LocalDateTime fechaInicio, 
            LocalDateTime fechaFin);

    /**
     * Cuenta recordatorios pendientes de un usuario
     */
    @Query("SELECT COUNT(r) FROM Recordatorio r WHERE r.usuario.id = :usuarioId " +
           "AND r.recordatorioEnviado = false AND r.activo = true")
    Long countRecordatoriosPendientes(@Param("usuarioId") Long usuarioId);

    /**
     * Obtiene recordatorios por destinatario ordenados por fecha
     */
    List<Recordatorio> findByDestinatarioIdOrderByFechaRecordatorioDesc(Long destinatarioId);
}


