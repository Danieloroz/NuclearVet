package com.nuclearvet.modulos.pacientes.repository;

import com.nuclearvet.modulos.pacientes.entity.HistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad HistoriaClinica.
 * Implementa RF2.3 - Creación de historia clínica
 */
@Repository
public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica, Long> {
    
    /**
     * Busca la historia clínica de un paciente.
     */
    Optional<HistoriaClinica> findByPacienteId(Long pacienteId);
    
    /**
     * Busca una historia clínica por su número.
     */
    Optional<HistoriaClinica> findByNumeroHistoria(String numeroHistoria);
    
    /**
     * Verifica si un paciente ya tiene historia clínica.
     */
    boolean existsByPacienteId(Long pacienteId);
}
