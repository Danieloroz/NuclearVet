package com.nuclearvet.modulos.notificaciones.mapper;

import com.nuclearvet.modulos.notificaciones.dto.CrearNotificacionDTO;
import com.nuclearvet.modulos.notificaciones.dto.NotificacionDTO;
import com.nuclearvet.modulos.notificaciones.entity.Notificacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para convertir entre Notificacion y sus DTOs.
 */
@Mapper(componentModel = "spring")
public interface NotificacionMapper {

    @Mapping(source = "destinatario.id", target = "destinatarioId")
    @Mapping(source = "destinatario.nombre", target = "destinatarioNombre")
    NotificacionDTO toDTO(Notificacion notificacion);

    @Mapping(target = "estado", constant = "PENDIENTE")
    @Mapping(target = "intentosEnvio", constant = "0")
    @Mapping(target = "fechaEnviada", ignore = true)
    @Mapping(target = "fechaLeida", ignore = true)
    @Mapping(target = "errorMensaje", ignore = true)
    @Mapping(target = "destinatario", ignore = true)
    Notificacion toEntity(CrearNotificacionDTO dto);
}
