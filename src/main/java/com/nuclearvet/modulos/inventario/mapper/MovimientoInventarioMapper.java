package com.nuclearvet.modulos.inventario.mapper;

import com.nuclearvet.modulos.inventario.dto.MovimientoInventarioDTO;
import com.nuclearvet.modulos.inventario.entity.MovimientoInventario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para Movimiento de Inventario.
 */
@Mapper(componentModel = "spring")
public interface MovimientoInventarioMapper {
    
    @Mapping(target = "productoId", source = "producto.id")
    @Mapping(target = "productoNombre", source = "producto.nombre")
    @Mapping(target = "productoCodigo", source = "producto.codigo")
    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "usuarioNombre", expression = "java(movimiento.getUsuario().getNombreCompleto())")
    @Mapping(target = "proveedorId", source = "proveedor.id")
    @Mapping(target = "proveedorNombre", source = "proveedor.nombre")
    MovimientoInventarioDTO toDTO(MovimientoInventario movimiento);
}
