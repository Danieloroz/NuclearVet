package com.nuclearvet.modulos.citas.mapper;

import com.nuclearvet.modulos.citas.dto.CitaDTO;
import com.nuclearvet.modulos.citas.entity.Cita;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para convertir entre Cita y CitaDTO.
 */
@Mapper(componentModel = "spring")
public interface CitaMapper {

    @Mapping(target = "pacienteId", source = "paciente.id")
    @Mapping(target = "pacienteNombre", source = "paciente.nombre")
    @Mapping(target = "veterinarioId", source = "veterinario.id")
    @Mapping(target = "veterinarioNombre", source = "veterinario.nombreCompleto")
    CitaDTO toDTO(Cita cita);
}
