package com.nuclearvet.modulos.usuarios.service;

import com.nuclearvet.modulos.usuarios.entity.Usuario;
import com.nuclearvet.modulos.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Implementación de UserDetailsService para Spring Security.
 * Carga los detalles del usuario desde la base de datos.
 * 
 * Patrón: Strategy Pattern (implementación de UserDetailsService)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UsuarioRepository usuarioRepository;
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Cargando usuario con email: {}", email);
        
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No se encontró el usuario con email: " + email));
        
        if (!usuario.getActivo()) {
            throw new UsernameNotFoundException("El usuario está inactivo, parce");
        }
        
        if (usuario.getBloqueado()) {
            throw new UsernameNotFoundException("El usuario está bloqueado por seguridad, llave");
        }
        
        return new User(
                usuario.getEmail(),
                usuario.getContrasena(),
                usuario.getActivo(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                !usuario.getBloqueado(), // accountNonLocked
                mapRolesToAuthorities(usuario)
        );
    }
    
    /**
     * Convierte los roles del usuario en GrantedAuthorities de Spring Security.
     */
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Usuario usuario) {
        return usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getNombre()))
                .collect(Collectors.toList());
    }
}
