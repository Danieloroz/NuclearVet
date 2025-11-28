package com.nuclearvet.modulos.notificaciones.service;

import com.nuclearvet.common.exception.RecursoNoEncontradoException;
import com.nuclearvet.common.exception.ValidacionException;
import com.nuclearvet.modulos.notificaciones.dto.CrearNotificacionDTO;
import com.nuclearvet.modulos.notificaciones.dto.NotificacionDTO;
import com.nuclearvet.modulos.notificaciones.entity.CanalNotificacion;
import com.nuclearvet.modulos.notificaciones.entity.EstadoNotificacion;
import com.nuclearvet.modulos.notificaciones.entity.Notificacion;
import com.nuclearvet.modulos.notificaciones.entity.TipoNotificacion;
import com.nuclearvet.modulos.notificaciones.mapper.NotificacionMapper;
import com.nuclearvet.modulos.notificaciones.repository.NotificacionRepository;
import com.nuclearvet.modulos.usuarios.entity.Usuario;
import com.nuclearvet.modulos.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de notificaciones del sistema.
 * RF5.1: Enviar notificaciones a usuarios
 * RF5.2: Consultar historial de notificaciones
 * RF5.3: Marcar notificaciones como leídas
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionMapper notificacionMapper;

    /**
     * RF5.1: Crear y enviar una notificación
     */
    @Transactional
    public NotificacionDTO crearNotificacion(CrearNotificacionDTO dto) {
        log.info("Creando notificación tipo {} para destinatario: {}", dto.getTipo(), dto.getDestinatarioId());

        // Validar destinatario
        Usuario destinatario = usuarioRepository.findById(dto.getDestinatarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "id", dto.getDestinatarioId()));

        // Validar canal según tipo de notificación
        validarCanalNotificacion(dto.getTipo(), dto.getCanal(), destinatario);

        Notificacion notificacion = notificacionMapper.toEntity(dto);
        notificacion.setDestinatario(destinatario);
        notificacion.setActivo(true);

        // Intentar enviar inmediatamente
        try {
            enviarNotificacion(notificacion);
            notificacion.setEstado(EstadoNotificacion.ENVIADA);
            notificacion.setFechaEnviada(LocalDateTime.now());
            log.info("Notificación enviada exitosamente por {}", dto.getCanal());
        } catch (Exception e) {
            notificacion.setEstado(EstadoNotificacion.ERROR);
            notificacion.setErrorMensaje(e.getMessage());
            log.error("Error al enviar notificación: {}", e.getMessage());
        }

        notificacion = notificacionRepository.save(notificacion);
        log.info("Notificación creada exitosamente");
        return notificacionMapper.toDTO(notificacion);
    }

    /**
     * RF5.1: Reintento de envío de notificación fallida
     */
    @Transactional
    public NotificacionDTO reintentarEnvio(Long id) {
        log.info("Reintentando envío de notificación: {}", id);

        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Notificacion", "id", id));

        if (notificacion.getEstado() == EstadoNotificacion.ENVIADA) {
            throw new ValidacionException("La notificación ya fue enviada exitosamente");
        }

        if (notificacion.getIntentosEnvio() >= 3) {
            throw new ValidacionException("Se alcanzó el máximo de intentos de envío (3)");
        }

        try {
            enviarNotificacion(notificacion);
            notificacion.setEstado(EstadoNotificacion.ENVIADA);
            notificacion.setFechaEnviada(LocalDateTime.now());
            notificacion.setErrorMensaje(null);
            log.info("Notificación reenviada exitosamente");
        } catch (Exception e) {
            notificacion.setEstado(EstadoNotificacion.ERROR);
            notificacion.setErrorMensaje(e.getMessage());
            notificacion.setIntentosEnvio(notificacion.getIntentosEnvio() + 1);
            log.error("Error en reintento de envío: {}", e.getMessage());
        }

        notificacion = notificacionRepository.save(notificacion);
        return notificacionMapper.toDTO(notificacion);
    }

    /**
     * RF5.3: Marcar notificación como leída
     */
    @Transactional
    public NotificacionDTO marcarComoLeida(Long id) {
        log.info("Marcando notificación como leída: {}", id);

        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Notificacion", "id", id));

        if (notificacion.getFechaLeida() != null) {
            throw new ValidacionException("La notificación ya fue marcada como leída anteriormente");
        }

        notificacion.setFechaLeida(LocalDateTime.now());
        notificacion = notificacionRepository.save(notificacion);

        log.info("Notificación {} marcada como leída", id);
        return notificacionMapper.toDTO(notificacion);
    }

    /**
     * RF5.2: Obtener notificaciones por destinatario
     */
    @Transactional(readOnly = true)
    public List<NotificacionDTO> listarPorDestinatario(Long destinatarioId) {
        log.info("Listando notificaciones del destinatario: {}", destinatarioId);
        
        List<Notificacion> notificaciones = notificacionRepository
                .findByDestinatarioIdOrderByFechaCreacionDesc(destinatarioId);
        
        return notificaciones.stream()
                .map(notificacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * RF5.2: Obtener notificaciones no leídas
     */
    @Transactional(readOnly = true)
    public List<NotificacionDTO> listarNoLeidas(Long destinatarioId) {
        log.info("Listando notificaciones no leídas del destinatario: {}", destinatarioId);
        
        List<Notificacion> notificaciones = notificacionRepository
                .findByDestinatarioIdAndFechaLeidaIsNullOrderByFechaCreacionDesc(destinatarioId);
        
        return notificaciones.stream()
                .map(notificacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * RF5.2: Obtener notificaciones por tipo
     */
    @Transactional(readOnly = true)
    public List<NotificacionDTO> listarPorTipo(Long destinatarioId, TipoNotificacion tipo) {
        log.info("Listando notificaciones tipo {} del destinatario: {}", tipo, destinatarioId);
        
        List<Notificacion> notificaciones = notificacionRepository
                .findByDestinatarioIdAndTipoOrderByFechaCreacionDesc(destinatarioId, tipo);
        
        return notificaciones.stream()
                .map(notificacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener notificaciones por estado
     */
    @Transactional(readOnly = true)
    public List<NotificacionDTO> listarPorEstado(EstadoNotificacion estado) {
        log.info("Listando notificaciones con estado: {}", estado);
        
        List<Notificacion> notificaciones = notificacionRepository
                .findByEstadoOrderByFechaCreacionDesc(estado);
        
        return notificaciones.stream()
                .map(notificacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener notificaciones entre fechas
     */
    @Transactional(readOnly = true)
    public List<NotificacionDTO> listarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Listando notificaciones entre {} y {}", fechaInicio, fechaFin);
        
        if (fechaInicio.isAfter(fechaFin)) {
            throw new ValidacionException("La fecha de inicio no puede ser posterior a la fecha fin");
        }
        
        List<Notificacion> notificaciones = notificacionRepository
                .findByFechaCreacionBetweenOrderByFechaCreacionDesc(fechaInicio, fechaFin);
        
        return notificaciones.stream()
                .map(notificacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener detalle de notificación
     */
    @Transactional(readOnly = true)
    public NotificacionDTO obtenerPorId(Long id) {
        log.info("Obteniendo notificación: {}", id);
        
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Notificacion", "id", id));
        
        return notificacionMapper.toDTO(notificacion);
    }

    /**
     * Validar que el canal sea apropiado para el tipo de notificación
     */
    private void validarCanalNotificacion(TipoNotificacion tipo, CanalNotificacion canal, Usuario destinatario) {
        if (canal == CanalNotificacion.EMAIL && (destinatario.getEmail() == null || destinatario.getEmail().isEmpty())) {
            throw new ValidacionException("El destinatario no tiene un email registrado");
        }
        
        if (canal == CanalNotificacion.SMS && (destinatario.getTelefono() == null || destinatario.getTelefono().isEmpty())) {
            throw new ValidacionException("El destinatario no tiene un teléfono registrado");
        }
    }

    /**
     * Simula el envío de notificación por el canal especificado
     * En producción, aquí se integraría con servicios reales (SendGrid, Twilio, etc.)
     */
    private void enviarNotificacion(Notificacion notificacion) {
        // Simulación de envío
        log.info("Enviando notificación por {}: {}", notificacion.getCanal(), notificacion.getAsunto());
        
        // Aquí iría la integración real con:
        // - SendGrid/AWS SES para EMAIL
        // - Twilio para SMS
        // - Firebase/OneSignal para PUSH
        // - WebSocket para IN_APP
        
        // Por ahora solo validamos que tenga contenido
        if (notificacion.getAsunto() == null || notificacion.getMensaje() == null) {
            throw new RuntimeException("La notificación debe tener asunto y mensaje");
        }
    }

    /**
     * RF5.3: Listar notificaciones por canal
     */
    @Transactional(readOnly = true)
    public List<NotificacionDTO> listarPorCanal(Long destinatarioId, CanalNotificacion canal) {
        log.info("Listando notificaciones del destinatario {} por canal: {}", destinatarioId, canal);
        
        List<Notificacion> notificaciones = notificacionRepository
                .findByDestinatarioIdAndCanalOrderByFechaCreacionDesc(destinatarioId, canal);
        
        return notificaciones.stream()
                .map(notificacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * RF5.4: Marcar todas las notificaciones como leídas
     */
    @Transactional
    public void marcarTodasComoLeidas(Long destinatarioId) {
        log.info("Marcando todas las notificaciones como leídas para destinatario: {}", destinatarioId);
        
        List<Notificacion> notificaciones = notificacionRepository
                .findByDestinatarioIdAndFechaLeidaIsNullOrderByFechaCreacionDesc(destinatarioId);
        
        notificaciones.forEach(Notificacion::marcarComoLeida);
        
        notificacionRepository.saveAll(notificaciones);
        log.info("Marcadas {} notificaciones como leídas", notificaciones.size());
    }

    /**
     * RF5.5: Reenviar notificación fallida
     */
    @Transactional
    public NotificacionDTO reenviarNotificacion(Long id) {
        log.info("Reenviando notificación: {}", id);
        
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Notificacion", "id", id));
        
        // Verificar que sea una notificación fallida
        if (notificacion.getEstado() != EstadoNotificacion.ERROR) {
            throw new ValidacionException("Solo se pueden reenviar notificaciones con estado ERROR");
        }
        
        // Intentar reenviar
        try {
            enviarNotificacion(notificacion);
            notificacion.setEstado(EstadoNotificacion.ENVIADA);
            notificacion.setFechaEnviada(LocalDateTime.now());
            log.info("Notificación reenviada exitosamente");
        } catch (Exception e) {
            log.error("Error al reenviar notificación: {}", e.getMessage());
            throw new ValidacionException("No se pudo reenviar la notificación: " + e.getMessage());
        }
        
        notificacion = notificacionRepository.save(notificacion);
        return notificacionMapper.toDTO(notificacion);
    }

    /**
     * RF5.6: Contar notificaciones no leídas
     */
    @Transactional(readOnly = true)
    public Long contarNoLeidas(Long destinatarioId) {
        log.info("Contando notificaciones no leídas del destinatario: {}", destinatarioId);
        return notificacionRepository.countByDestinatarioIdAndFechaLeidaIsNull(destinatarioId);
    }

    /**
     * RF5.7: Desactivar notificación
     */
    @Transactional
    public void desactivarNotificacion(Long id) {
        log.info("Desactivando notificación: {}", id);
        
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Notificacion", "id", id));
        
        notificacion.setActivo(false);
        notificacionRepository.save(notificacion);
        log.info("Notificación desactivada exitosamente");
    }
}

