package com.nuclearvet.modulos.inventario.mapper;

import com.nuclearvet.modulos.inventario.dto.CrearProveedorDTO;
import com.nuclearvet.modulos.inventario.dto.ProveedorDTO;
import com.nuclearvet.modulos.inventario.entity.Proveedor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Mapper para Proveedor.
 */
@Mapper(componentModel = "spring")
public interface ProveedorMapper {
    
    ProveedorDTO toDTO(Proveedor proveedor);
    
    Proveedor toEntity(CrearProveedorDTO dto);
    
    void updateEntity(CrearProveedorDTO dto, @MappingTarget Proveedor proveedor);
}
