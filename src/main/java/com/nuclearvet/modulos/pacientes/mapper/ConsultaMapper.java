package com.nuclearvet.modulos.pacientes.mapper;

import com.nuclearvet.modulos.pacientes.dto.ConsultaDTO;
import com.nuclearvet.modulos.pacientes.entity.Consulta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para Consulta usando MapStruct.
 */
@Mapper(componentModel = "spring")
public interface ConsultaMapper {
    
    @Mapping(target = "historiaClinicaId", source = "historiaClinica.id")
    @Mapping(target = "numeroHistoria", source = "historiaClinica.numeroHistoria")
    @Mapping(target = "nombrePaciente", source = "historiaClinica.paciente.nombre")
    @Mapping(target = "veterinarioId", source = "veterinario.id")
    @Mapping(target = "nombreVeterinario", expression = "java(consulta.getVeterinario().getNombreCompleto())")
    ConsultaDTO toDTO(Consulta consulta);
}
