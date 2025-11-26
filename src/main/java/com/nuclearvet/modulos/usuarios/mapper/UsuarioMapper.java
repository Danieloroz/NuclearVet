package com.nuclearvet.modulos.usuarios.mapper;

import com.nuclearvet.modulos.usuarios.dto.CrearUsuarioDTO;
import com.nuclearvet.modulos.usuarios.dto.UsuarioDTO;
import com.nuclearvet.modulos.usuarios.entity.Rol;
import com.nuclearvet.modulos.usuarios.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre Entity y DTO de Usuario.
 * Patrón: Mapper Pattern (usando MapStruct)
 */
@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    
    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToStrings")
    @Mapping(target = "nombreCompleto", source = "nombreCompleto")
    UsuarioDTO toDTO(Usuario usuario);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contrasena", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "ultimoAcceso", ignore = true)
    @Mapping(target = "intentosFallidos", ignore = true)
    @Mapping(target = "bloqueado", ignore = true)
    @Mapping(target = "tokenRecuperacion", ignore = true)
    @Mapping(target = "tokenRecuperacionExpiracion", ignore = true)
    Usuario toEntity(CrearUsuarioDTO dto);
    
    @Named("rolesToStrings")
    default Set<String> rolesToStrings(Set<Rol> roles) {
        if (roles == null) return null;
        return roles.stream()
                .map(Rol::getNombre)
                .collect(Collectors.toSet());
    }
}
