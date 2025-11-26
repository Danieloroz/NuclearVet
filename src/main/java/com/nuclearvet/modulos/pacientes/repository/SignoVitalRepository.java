package com.nuclearvet.modulos.pacientes.repository;

import com.nuclearvet.modulos.pacientes.entity.SignoVital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad SignoVital.
 */
@Repository
public interface SignoVitalRepository extends JpaRepository<SignoVital, Long> {
    
    /**
     * Obtiene los signos vitales de una consulta.
     */
    List<SignoVital> findByConsultaIdOrderByFechaRegistroDesc(Long consultaId);
    
    /**
     * Obtiene todos los signos vitales de un paciente (a través de consultas).
     */
    List<SignoVital> findByConsultaHistoriaClinicaIdOrderByFechaRegistroDesc(Long historiaClinicaId);
}
