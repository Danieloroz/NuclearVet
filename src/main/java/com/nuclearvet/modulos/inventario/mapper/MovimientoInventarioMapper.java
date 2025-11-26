package com.nuclearvet.modulos.inventario.mapper;

import com.nuclearvet.modulos.inventario.dto.MovimientoInventarioDTO;
import com.nuclearvet.modulos.inventario.dto.RegistrarMovimientoDTO;
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
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "producto", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "proveedor", ignore = true)
    @Mapping(target = "stockAnterior", ignore = true)
    @Mapping(target = "stockNuevo", ignore = true)
    @Mapping(target = "fechaMovimiento", ignore = true)
    @Mapping(target = "costoTotal", ignore = true)
    MovimientoInventario toEntity(RegistrarMovimientoDTO dto);
}

