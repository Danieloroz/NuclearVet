package com.nuclearvet.modulos.pacientes.repository;

import com.nuclearvet.modulos.pacientes.entity.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Consulta.
 * Implementa RF2.4 - Registro de consulta o servicio
 * Implementa RF2.6 - Ver evolución del paciente
 */
@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    
    /**
     * Obtiene todas las consultas de una historia clínica ordenadas por fecha (RF2.6).
     */
    List<Consulta> findByHistoriaClinicaIdOrderByFechaConsultaDesc(Long historiaClinicaId);
    
    /**
     * Obtiene las consultas de un veterinario.
     */
    List<Consulta> findByVeterinarioIdOrderByFechaConsultaDesc(Long veterinarioId);
    
    /**
     * Obtiene consultas por tipo de servicio.
     */
    List<Consulta> findByTipoServicioOrderByFechaConsultaDesc(String tipoServicio);
    
    /**
     * Obtiene consultas en un rango de fechas.
     */
    @Query("SELECT c FROM Consulta c WHERE c.fechaConsulta BETWEEN :fechaInicio AND :fechaFin ORDER BY c.fechaConsulta DESC")
    List<Consulta> findByFechaConsultaBetween(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
    
    /**
     * Obtiene consultas pendientes de seguimiento.
     */
    List<Consulta> findByEstadoOrderByFechaConsultaDesc(String estado);
    
    /**
     * Cuenta las consultas de un paciente (por historia clínica).
     */
    long countByHistoriaClinicaId(Long historiaClinicaId);
}
