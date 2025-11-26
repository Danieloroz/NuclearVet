package com.nuclearvet.modulos.pacientes.mapper;

import com.nuclearvet.modulos.pacientes.dto.PacienteDTO;
import com.nuclearvet.modulos.pacientes.entity.Paciente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para Paciente usando MapStruct.
 */
@Mapper(componentModel = "spring")
public interface PacienteMapper {
    
    @Mapping(target = "edadEnAnios", expression = "java(paciente.calcularEdadEnAnios())")
    @Mapping(target = "propietarioId", source = "propietario.id")
    @Mapping(target = "nombrePropietario", expression = "java(paciente.getPropietario().getNombreCompleto())")
    PacienteDTO toDTO(Paciente paciente);
}
