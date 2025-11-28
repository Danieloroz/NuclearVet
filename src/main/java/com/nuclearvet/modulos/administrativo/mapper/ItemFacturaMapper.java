package com.nuclearvet.modulos.administrativo.mapper;

import com.nuclearvet.modulos.administrativo.dto.ItemFacturaDTO;
import com.nuclearvet.modulos.administrativo.entity.ItemFactura;
import org.mapstruct.*;

/**
 * Mapper para conversión entre ItemFactura y DTOs.
 * RF6.1 - Gestión de facturación
 */
@Mapper(componentModel = "spring")
public interface ItemFacturaMapper {

    @Mapping(target = "productoId", ignore = true)
    @Mapping(target = "productoNombre", ignore = true)
    ItemFacturaDTO toDTO(ItemFactura item);

    @Mapping(target = "factura", ignore = true)
    @Mapping(target = "producto", ignore = true)
    ItemFactura toEntity(ItemFacturaDTO dto);
}
