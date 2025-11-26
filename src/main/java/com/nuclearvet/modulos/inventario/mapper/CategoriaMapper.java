package com.nuclearvet.modulos.inventario.mapper;

import com.nuclearvet.modulos.inventario.dto.CategoriaDTO;
import com.nuclearvet.modulos.inventario.dto.CrearCategoriaDTO;
import com.nuclearvet.modulos.inventario.entity.Categoria;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Mapper para Categoría.
 */
@Mapper(componentModel = "spring")
public interface CategoriaMapper {
    
    CategoriaDTO toDTO(Categoria categoria);
    
    Categoria toEntity(CrearCategoriaDTO dto);
    
    void updateEntity(CrearCategoriaDTO dto, @MappingTarget Categoria categoria);
}
