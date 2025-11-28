package com.nuclearvet.modulos.notificaciones.repository;

import com.nuclearvet.modulos.notificaciones.entity.CanalNotificacion;
import com.nuclearvet.modulos.notificaciones.entity.EstadoNotificacion;
import com.nuclearvet.modulos.notificaciones.entity.Notificacion;
import com.nuclearvet.modulos.notificaciones.entity.TipoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Notificacion.
 */
@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    /**
     * Obtiene todas las notificaciones de un usuario ordenadas por fecha de creación
     */
    List<Notificacion> findByDestinatarioIdOrderByFechaCreacionDesc(Long usuarioId);

    /**
     * Obtiene las notificaciones no leídas de un usuario
     */
    @Query("SELECT n FROM Notificacion n WHERE n.destinatario.id = :usuarioId " +
           "AND n.estado != 'LEIDA' ORDER BY n.fechaCreacion DESC")
    List<Notificacion> findNotificacionesNoLeidas(@Param("usuarioId") Long usuarioId);

    /**
     * Obtiene las notificaciones pendientes de envío
     */
    @Query("SELECT n FROM Notificacion n WHERE n.estado = 'PENDIENTE' " +
           "AND (n.fechaProgramada IS NULL OR n.fechaProgramada <= :ahora) " +
           "ORDER BY n.fechaCreacion ASC")
    List<Notificacion> findNotificacionesPendientes(@Param("ahora") LocalDateTime ahora);

    /**
     * Obtiene notificaciones por tipo y usuario
     */
    List<Notificacion> findByDestinatarioIdAndTipoOrderByFechaCreacionDesc(
            Long usuarioId, TipoNotificacion tipo);

    /**
     * Obtiene notificaciones por estado
     */
    List<Notificacion> findByEstadoOrderByFechaCreacionDesc(EstadoNotificacion estado);

    /**
     * Cuenta las notificaciones no leídas de un usuario
     */
    @Query("SELECT COUNT(n) FROM Notificacion n WHERE n.destinatario.id = :usuarioId " +
           "AND n.estado != 'LEIDA'")
    Long countNotificacionesNoLeidas(@Param("usuarioId") Long usuarioId);

    /**
     * Obtiene notificaciones relacionadas con una referencia específica
     */
    List<Notificacion> findByReferenciaIdAndReferenciaTipoOrderByFechaCreacionDesc(
            Long referenciaId, String referenciaTipo);

    /**
     * Obtiene notificaciones con errores para reintento
     */
    @Query("SELECT n FROM Notificacion n WHERE n.estado = 'ERROR' " +
           "AND n.intentosEnvio < :maxIntentos ORDER BY n.fechaCreacion ASC")
    List<Notificacion> findNotificacionesConError(@Param("maxIntentos") Integer maxIntentos);

    /**
     * Obtiene notificaciones enviadas en un rango de fechas
     */
    @Query("SELECT n FROM Notificacion n WHERE n.estado = 'ENVIADA' " +
           "AND n.fechaEnviada BETWEEN :inicio AND :fin ORDER BY n.fechaEnviada DESC")
    List<Notificacion> findNotificacionesEnviadas(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Obtiene notificaciones no leídas de un usuario
     */
    List<Notificacion> findByDestinatarioIdAndFechaLeidaIsNullOrderByFechaCreacionDesc(Long destinatarioId);

    /**
     * Obtiene notificaciones por rango de fechas de creación
     */
    List<Notificacion> findByFechaCreacionBetweenOrderByFechaCreacionDesc(
            LocalDateTime fechaInicio, 
            LocalDateTime fechaFin);

    /**
     * Obtiene notificaciones por destinatario y canal
     */
    List<Notificacion> findByDestinatarioIdAndCanalOrderByFechaCreacionDesc(
            Long destinatarioId, 
            CanalNotificacion canal);

    /**
     * Cuenta notificaciones no leídas de un usuario
     */
    Long countByDestinatarioIdAndFechaLeidaIsNull(Long destinatarioId);
}

