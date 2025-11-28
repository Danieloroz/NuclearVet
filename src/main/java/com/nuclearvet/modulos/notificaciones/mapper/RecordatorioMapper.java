package com.nuclearvet.modulos.notificaciones.mapper;

import com.nuclearvet.modulos.notificaciones.dto.CrearRecordatorioDTO;
import com.nuclearvet.modulos.notificaciones.dto.RecordatorioDTO;
import com.nuclearvet.modulos.notificaciones.entity.Recordatorio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para convertir entre Recordatorio y sus DTOs.
 */
@Mapper(componentModel = "spring")
public interface RecordatorioMapper {

    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "usuario.nombre", target = "usuarioNombre")
    @Mapping(source = "paciente.id", target = "pacienteId")
    @Mapping(source = "paciente.nombre", target = "pacienteNombre")
    @Mapping(source = "cita.id", target = "citaId")
    RecordatorioDTO toDTO(Recordatorio recordatorio);

    @Mapping(source = "usuarioId", target = "usuario.id")
    @Mapping(source = "pacienteId", target = "paciente.id")
    @Mapping(source = "citaId", target = "cita.id")
    @Mapping(target = "recordatorioEnviado", constant = "false")
    @Mapping(target = "fechaEnvio", ignore = true)
    @Mapping(target = "notificacionId", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "cita", ignore = true)
    @Mapping(target = "canales", ignore = true)
    Recordatorio toEntity(CrearRecordatorioDTO dto);
}
