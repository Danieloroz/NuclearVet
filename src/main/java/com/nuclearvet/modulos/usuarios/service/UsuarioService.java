package com.nuclearvet.modulos.usuarios.service;

import com.nuclearvet.common.exception.ConflictoException;
import com.nuclearvet.common.exception.RecursoNoEncontradoException;
import com.nuclearvet.common.exception.ValidacionException;
import com.nuclearvet.config.security.JwtUtil;
import com.nuclearvet.modulos.usuarios.dto.*;
import com.nuclearvet.modulos.usuarios.entity.Rol;
import com.nuclearvet.modulos.usuarios.entity.Usuario;
import com.nuclearvet.modulos.usuarios.mapper.UsuarioMapper;
import com.nuclearvet.modulos.usuarios.repository.RolRepository;
import com.nuclearvet.modulos.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de usuarios.
 * Implementa todos los requisitos funcionales del módulo de Usuarios y Accesos.
 * 
 * RF1.1 - Gestión de usuarios
 * RF1.2 - Control de roles y permisos
 * RF1.3 - Inicio de sesión seguro
 * RF1.4 - Recuperación de acceso
 * RF1.5 - Registro de actividad relevante
 * 
 * Patrón: Service Pattern
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    
    /**
     * Crea un nuevo usuario en el sistema (RF1.1).
     */
    @Transactional
    public UsuarioDTO crearUsuario(CrearUsuarioDTO dto) {
        log.info("Creando nuevo usuario con email: {}", dto.getEmail());
        
        // Validar que el email no exista
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictoException("Ya existe un usuario con ese email, parce");
        }
        
        // Validar documento si está presente
        if (dto.getDocumentoIdentidad() != null && 
            usuarioRepository.existsByDocumentoIdentidad(dto.getDocumentoIdentidad())) {
            throw new ConflictoException("Ya existe un usuario con ese documento, llave");
        }
        
        Usuario usuario = usuarioMapper.toEntity(dto);
        
        // Encriptar contraseña
        usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        
        // Asignar roles
        Set<Rol> roles = obtenerRoles(dto.getRoles());
        usuario.setRoles(roles);
        
        usuario.setActivo(true);
        usuario.setIntentosFallidos(0);
        usuario.setBloqueado(false);
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        log.info("Usuario creado exitosamente con ID: {}", usuarioGuardado.getId());
        
        return usuarioMapper.toDTO(usuarioGuardado);
    }
    
    /**
     * Autentica un usuario y genera un token JWT (RF1.3).
     */
    @Transactional
    public AuthResponseDTO login(LoginDTO loginDTO) {
        log.info("Intento de login para usuario: {}", loginDTO.getEmail());
        
        // Buscar usuario
        Usuario usuario = usuarioRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró un usuario con ese email, parce"));
        
        // Verificar si está bloqueado
        if (usuario.getBloqueado()) {
            throw new ValidacionException(
                    "Tu cuenta está bloqueada por seguridad. Comunícate con el admin, llave");
        }
        
        try {
            // Autenticar
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getEmail(),
                            loginDTO.getContrasena()
                    )
            );
            
            // Generar token JWT
            String token = jwtUtil.generateToken(usuario.getEmail());
            
            // Actualizar último acceso y resetear intentos fallidos (RF1.5)
            usuario.setUltimoAcceso(LocalDateTime.now());
            usuario.setIntentosFallidos(0);
            usuarioRepository.save(usuario);
            
            log.info("Login exitoso para usuario: {}", loginDTO.getEmail());
            
            return AuthResponseDTO.from(token, usuarioMapper.toDTO(usuario));
            
        } catch (Exception e) {
            // Incrementar intentos fallidos (RF1.5)
            usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
            
            // Bloquear después de 5 intentos
            if (usuario.getIntentosFallidos() >= 5) {
                usuario.setBloqueado(true);
                log.warn("Usuario bloqueado por múltiples intentos fallidos: {}", usuario.getEmail());
            }
            
            usuarioRepository.save(usuario);
            throw new ValidacionException("Credenciales incorrectas, parce. Revisa bien");
        }
    }
    
    /**
     * Inicia el proceso de recuperación de contraseña (RF1.4).
     */
    @Transactional
    public void solicitarRecuperacionContrasena(RecuperarContrasenaDTO dto) {
        log.info("Solicitud de recuperación de contraseña para: {}", dto.getEmail());
        
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró un usuario con ese email"));
        
        // Generar token de recuperación
        String token = UUID.randomUUID().toString();
        usuario.setTokenRecuperacion(token);
        usuario.setTokenRecuperacionExpiracion(LocalDateTime.now().plusHours(24));
        
        usuarioRepository.save(usuario);
        
        log.info("Token de recuperación generado para: {}", dto.getEmail());
        // TODO: Enviar email con el token (implementar con módulo de notificaciones)
    }
    
    /**
     * Cambia la contraseña usando el token de recuperación (RF1.4).
     */
    @Transactional
    public void cambiarContrasenaConToken(CambiarContrasenaDTO dto) {
        log.info("Intento de cambio de contraseña con token");
        
        Usuario usuario = usuarioRepository.findByTokenRecuperacion(dto.getToken())
                .orElseThrow(() -> new ValidacionException("Token de recuperación inválido, parce"));
        
        // Verificar que el token no haya expirado
        if (usuario.getTokenRecuperacionExpiracion().isBefore(LocalDateTime.now())) {
            throw new ValidacionException("El token de recuperación ha expirado, llave");
        }
        
        // Cambiar contraseña
        usuario.setContrasena(passwordEncoder.encode(dto.getNuevaContrasena()));
        usuario.setTokenRecuperacion(null);
        usuario.setTokenRecuperacionExpiracion(null);
        usuario.setIntentosFallidos(0);
        usuario.setBloqueado(false);
        
        usuarioRepository.save(usuario);
        log.info("Contraseña cambiada exitosamente para usuario ID: {}", usuario.getId());
    }
    
    /**
     * Obtiene todos los usuarios activos (RF1.1).
     */
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerTodos() {
        log.debug("Obteniendo todos los usuarios activos");
        return usuarioRepository.findByActivoTrue().stream()
                .map(usuarioMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene un usuario por su ID (RF1.1).
     */
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorId(Long id) {
        log.debug("Buscando usuario con ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "id", id));
        return usuarioMapper.toDTO(usuario);
    }
    
    /**
     * Actualiza un usuario existente (RF1.1).
     */
    @Transactional
    public UsuarioDTO actualizarUsuario(Long id, ActualizarUsuarioDTO dto) {
        log.info("Actualizando usuario con ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "id", id));
        
        // Actualizar campos si están presentes
        if (dto.getNombre() != null) {
            usuario.setNombre(dto.getNombre());
        }
        if (dto.getApellido() != null) {
            usuario.setApellido(dto.getApellido());
        }
        if (dto.getEmail() != null && !dto.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new ConflictoException("Ya existe un usuario con ese email");
            }
            usuario.setEmail(dto.getEmail());
        }
        if (dto.getTelefono() != null) {
            usuario.setTelefono(dto.getTelefono());
        }
        if (dto.getDocumentoIdentidad() != null) {
            usuario.setDocumentoIdentidad(dto.getDocumentoIdentidad());
        }
        if (dto.getActivo() != null) {
            usuario.setActivo(dto.getActivo());
        }
        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            usuario.setRoles(obtenerRoles(dto.getRoles()));
        }
        
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        log.info("Usuario actualizado exitosamente: {}", id);
        
        return usuarioMapper.toDTO(usuarioActualizado);
    }
    
    /**
     * Desactiva un usuario (soft delete) (RF1.1).
     */
    @Transactional
    public void desactivarUsuario(Long id) {
        log.info("Desactivando usuario con ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "id", id));
        
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        
        log.info("Usuario desactivado: {}", id);
    }
    
    /**
     * Obtiene usuarios por rol (RF1.2).
     */
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerPorRol(String nombreRol) {
        log.debug("Buscando usuarios con rol: {}", nombreRol);
        return usuarioRepository.findByRolNombre(nombreRol).stream()
                .map(usuarioMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene los roles desde la base de datos o crea el rol CLIENTE por defecto.
     */
    private Set<Rol> obtenerRoles(Set<String> nombresRoles) {
        if (nombresRoles == null || nombresRoles.isEmpty()) {
            // Por defecto asignar rol CLIENTE
            Rol rolCliente = rolRepository.findByNombre("ROLE_CLIENTE")
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No se encontró el rol ROLE_CLIENTE. Ejecuta las migraciones, parce"));
            return Set.of(rolCliente);
        }
        
        Set<Rol> roles = new HashSet<>();
        for (String nombreRol : nombresRoles) {
            Rol rol = rolRepository.findByNombre(nombreRol)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No se encontró el rol: " + nombreRol));
            roles.add(rol);
        }
        return roles;
    }
}
