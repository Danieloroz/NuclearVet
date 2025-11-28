package com.nuclearvet.modulos.administrativo.mapper;

import com.nuclearvet.modulos.administrativo.dto.PagoDTO;
import com.nuclearvet.modulos.administrativo.dto.RegistrarPagoDTO;
import com.nuclearvet.modulos.administrativo.entity.Pago;
import org.mapstruct.*;

/**
 * Mapper para conversión entre Pago y DTOs.
 * RF6.2 - Registro de pagos
 */
@Mapper(componentModel = "spring")
public interface PagoMapper {

    @Mapping(target = "facturaId", ignore = true)
    @Mapping(target = "recibidoPorId", ignore = true)
    @Mapping(target = "recibidoPorNombre", ignore = true)
    PagoDTO toDTO(Pago pago);

    @Mapping(target = "numeroRecibo", ignore = true)
    @Mapping(target = "factura", ignore = true)
    @Mapping(target = "recibidoPor", ignore = true)
    Pago toEntity(RegistrarPagoDTO dto);
}
