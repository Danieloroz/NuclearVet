package com.nuclearvet.modulos.pacientes.repository;

import com.nuclearvet.modulos.pacientes.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Paciente.
 * Implementa RF2.1 - Registro de pacientes
 */
@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    
    /**
     * Busca pacientes por propietario.
     */
    List<Paciente> findByPropietarioIdAndActivoTrue(Long propietarioId);
    
    /**
     * Busca un paciente por microchip.
     */
    Optional<Paciente> findByMicrochip(String microchip);
    
    /**
     * Busca pacientes por nombre (case insensitive).
     */
    @Query("SELECT p FROM Paciente p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND p.activo = true")
    List<Paciente> buscarPorNombre(@Param("nombre") String nombre);
    
    /**
     * Busca pacientes por especie.
     */
    List<Paciente> findByEspecieAndActivoTrue(String especie);
    
    /**
     * Busca todos los pacientes activos.
     */
    List<Paciente> findByActivoTrue();
    
    /**
     * Verifica si existe un paciente con el microchip dado.
     */
    boolean existsByMicrochip(String microchip);
}
