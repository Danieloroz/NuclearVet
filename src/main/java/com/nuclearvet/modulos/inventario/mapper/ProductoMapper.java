package com.nuclearvet.modulos.inventario.mapper;

import com.nuclearvet.modulos.inventario.dto.CrearProductoDTO;
import com.nuclearvet.modulos.inventario.dto.ProductoDTO;
import com.nuclearvet.modulos.inventario.entity.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper para Producto.
 */
@Mapper(componentModel = "spring")
public interface ProductoMapper {
    
    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "categoriaNombre", source = "categoria.nombre")
    @Mapping(target = "proveedorId", source = "proveedor.id")
    @Mapping(target = "proveedorNombre", source = "proveedor.nombre")
    @Mapping(target = "bajoStock", expression = "java(producto.bajosEnStock())")
    @Mapping(target = "proximoAVencer", expression = "java(producto.proximoAVencer())")
    @Mapping(target = "vencido", expression = "java(producto.vencido())")
    ProductoDTO toDTO(Producto producto);
    
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "proveedor", ignore = true)
    Producto toEntity(CrearProductoDTO dto);
    
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "proveedor", ignore = true)
    void updateEntity(CrearProductoDTO dto, @MappingTarget Producto producto);
}
