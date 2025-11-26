package com.nuclearvet.modulos.citas.repository;

import com.nuclearvet.modulos.citas.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestión de citas.
 * RF3.4, RF3.5
 */
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    // RF3.4: Consultar agenda del veterinario por fecha
    List<Cita> findByVeterinarioIdAndFechaHoraBetweenOrderByFechaHoraAsc(
            Long veterinarioId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    // Buscar citas del paciente
    List<Cita> findByPacienteIdOrderByFechaHoraDesc(Long pacienteId);

    // Buscar citas por estado
    List<Cita> findByEstadoAndActivoTrue(String estado);

    // Verificar disponibilidad (no debe haber cita en el mismo horario)
    @Query("SELECT c FROM Cita c WHERE c.veterinario.id = :veterinarioId " +
           "AND c.estado NOT IN ('CANCELADA', 'NO_ASISTIO') " +
           "AND c.fechaHora BETWEEN :inicio AND :fin")
    List<Cita> buscarCitasEnRango(
            @Param("veterinarioId") Long veterinarioId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    // Buscar citas del día
    @Query("SELECT c FROM Cita c WHERE c.veterinario.id = :veterinarioId " +
           "AND DATE(c.fechaHora) = DATE(:fecha) " +
           "AND c.activo = true ORDER BY c.fechaHora")
    List<Cita> buscarCitasDelDia(
            @Param("veterinarioId") Long veterinarioId,
            @Param("fecha") LocalDateTime fecha
    );

    // Contar citas pendientes
    Long countByVeterinarioIdAndEstadoAndActivoTrue(Long veterinarioId, String estado);
}
