package com.nuclearvet.modulos.notificaciones.service;

import com.nuclearvet.common.exception.RecursoNoEncontradoException;
import com.nuclearvet.common.exception.ValidacionException;
import com.nuclearvet.modulos.citas.entity.Cita;
import com.nuclearvet.modulos.citas.repository.CitaRepository;
import com.nuclearvet.modulos.notificaciones.dto.CrearRecordatorioDTO;
import com.nuclearvet.modulos.notificaciones.dto.RecordatorioDTO;
import com.nuclearvet.modulos.notificaciones.entity.CanalNotificacion;
import com.nuclearvet.modulos.notificaciones.entity.Recordatorio;
import com.nuclearvet.modulos.notificaciones.mapper.RecordatorioMapper;
import com.nuclearvet.modulos.notificaciones.repository.RecordatorioRepository;
import com.nuclearvet.modulos.pacientes.entity.Paciente;
import com.nuclearvet.modulos.pacientes.repository.PacienteRepository;
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
 * Servicio para gestión de recordatorios automáticos.
 * RF5.4: Crear recordatorios para citas
 * RF5.5: Enviar recordatorios automáticos
 * RF5.6: Configurar canales de recordatorios
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecordatorioService {

    private final RecordatorioRepository recordatorioRepository;
    private final UsuarioRepository usuarioRepository;
    private final PacienteRepository pacienteRepository;
    private final CitaRepository citaRepository;
    private final RecordatorioMapper recordatorioMapper;

    /**
     * RF5.4: Crear recordatorio para una cita
     */
    @Transactional
    public RecordatorioDTO crearRecordatorio(CrearRecordatorioDTO dto) {
        log.info("Creando recordatorio para cita: {}", dto.getCitaId());

        // Validar cita (opcional)
        Cita cita = null;
        if (dto.getCitaId() != null) {
            cita = citaRepository.findById(dto.getCitaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Cita", "id", dto.getCitaId()));
        }

        // Validar usuario (propietario del paciente)
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "id", dto.getUsuarioId()));

        // Validar paciente
        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente", "id", dto.getPacienteId()));

        // Validar que la fecha de envío sea antes de la cita (solo si hay cita asociada)
        if (cita != null && 
            (dto.getFechaRecordatorio().isAfter(cita.getFechaHora()) || 
             dto.getFechaRecordatorio().isEqual(cita.getFechaHora()))) {
            throw new ValidacionException("El recordatorio debe enviarse antes de la fecha de la cita");
        }

        // Validar que la fecha de envío no sea en el pasado (solo validar si es más de 1 minuto en el pasado para evitar problemas de timing)
        if (dto.getFechaRecordatorio().isBefore(LocalDateTime.now().minusMinutes(1))) {
            throw new ValidacionException("La fecha programada no puede ser en el pasado");
        }

        // Validar canales
        validarCanales(dto.getCanales(), usuario);

        Recordatorio recordatorio = recordatorioMapper.toEntity(dto);
        recordatorio.setUsuario(usuario);
        recordatorio.setPaciente(paciente);
        recordatorio.setCita(cita);
        recordatorio.setFechaRecordatorio(dto.getFechaRecordatorio());
        // Convertir lista de canales a String separado por comas
        String canalesStr = dto.getCanales().stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
        recordatorio.setCanales(canalesStr);
        recordatorio.setActivo(true);

        recordatorio = recordatorioRepository.save(recordatorio);
        log.info("Recordatorio creado y programado para: {}", recordatorio.getFechaRecordatorio());
        
        return recordatorioMapper.toDTO(recordatorio);
    }

    /**
     * RF5.5: Procesar recordatorios pendientes (ejecutado por scheduler)
     */
    @Transactional
    public void procesarRecordatoriosPendientes() {
        log.info("Procesando recordatorios pendientes...");
        
        LocalDateTime ahora = LocalDateTime.now();
        List<Recordatorio> recordatoriosPendientes = recordatorioRepository
                .findRecordatoriosPendientes(ahora);
        
        log.info("Encontrados {} recordatorios para procesar", recordatoriosPendientes.size());
        
        for (Recordatorio recordatorio : recordatoriosPendientes) {
            try {
                enviarRecordatorio(recordatorio);
                recordatorio.setRecordatorioEnviado(true);
                recordatorio.setFechaEnvio(LocalDateTime.now());
                recordatorioRepository.save(recordatorio);
                log.info("Recordatorio enviado exitosamente para cita el {}", recordatorio.getFechaRecordatorio());
            } catch (Exception e) {
                log.error("Error al enviar recordatorio para cita: {}", e.getMessage());
            }
        }
    }

    /**
     * RF5.4: Obtener recordatorios por usuario
     */
    @Transactional(readOnly = true)
    public List<RecordatorioDTO> listarPorUsuario(Long usuarioId) {
        log.info("Listando recordatorios del usuario: {}", usuarioId);
        
        List<Recordatorio> recordatorios = recordatorioRepository
                .findByUsuarioIdOrderByFechaRecordatorioDesc(usuarioId);
        
        return recordatorios.stream()
                .map(recordatorioMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * RF5.4: Obtener recordatorios por paciente
     */
    @Transactional(readOnly = true)
    public List<RecordatorioDTO> listarPorPaciente(Long pacienteId) {
        log.info("Listando recordatorios del paciente: {}", pacienteId);
        
        List<Recordatorio> recordatorios = recordatorioRepository
                .findByPacienteIdOrderByFechaRecordatorioDesc(pacienteId);
        
        return recordatorios.stream()
                .map(recordatorioMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * RF5.4: Obtener recordatorios pendientes
     */
    @Transactional(readOnly = true)
    public List<RecordatorioDTO> listarPendientes() {
        log.info("Listando recordatorios pendientes de envío");
        
        List<Recordatorio> recordatorios = recordatorioRepository
                .findRecordatoriosPendientes(LocalDateTime.now().plusDays(7)); // Próximos 7 días
        
        return recordatorios.stream()
                .map(recordatorioMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * RF5.4: Obtener recordatorios por rango de fechas
     */
    @Transactional(readOnly = true)
    public List<RecordatorioDTO> listarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Listando recordatorios entre {} y {}", fechaInicio, fechaFin);
        
        if (fechaInicio.isAfter(fechaFin)) {
            throw new ValidacionException("La fecha de inicio no puede ser posterior a la fecha fin");
        }
        
        List<Recordatorio> recordatorios = recordatorioRepository
                .findByFechaRecordatorioBetweenOrderByFechaRecordatorioAsc(fechaInicio, fechaFin);
        
        return recordatorios.stream()
                .map(recordatorioMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * RF5.4: Cancelar recordatorio
     */
    @Transactional
    public void cancelarRecordatorio(Long id) {
        log.info("Cancelando recordatorio: {}", id);
        
        Recordatorio recordatorio = recordatorioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Recordatorio", "id", id));
        
        if (recordatorio.getRecordatorioEnviado()) {
            throw new ValidacionException("No se puede cancelar un recordatorio que ya fue enviado");
        }
        
        recordatorio.setActivo(false);
        recordatorioRepository.save(recordatorio);
        log.info("Recordatorio {} cancelado", id);
    }

    /**
     * Obtener detalle de recordatorio
     */
    @Transactional(readOnly = true)
    public RecordatorioDTO obtenerPorId(Long id) {
        log.info("Obteniendo recordatorio: {}", id);
        
        Recordatorio recordatorio = recordatorioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Recordatorio", "id", id));
        
        return recordatorioMapper.toDTO(recordatorio);
    }

    /**
     * Validar que los canales sean válidos y el usuario tenga los datos necesarios
     */
    private void validarCanales(List<CanalNotificacion> canales, Usuario usuario) {
        if (canales == null || canales.isEmpty()) {
            throw new ValidacionException("Debe especificar al menos un canal de envío");
        }
        
        for (CanalNotificacion canal : canales) {
            if (canal == CanalNotificacion.EMAIL && (usuario.getEmail() == null || usuario.getEmail().isEmpty())) {
                throw new ValidacionException("El usuario no tiene email registrado para recibir recordatorios");
            }
            if (canal == CanalNotificacion.SMS && (usuario.getTelefono() == null || usuario.getTelefono().isEmpty())) {
                throw new ValidacionException("El usuario no tiene teléfono registrado para recibir recordatorios");
            }
        }
    }

    /**
     * Enviar recordatorio por los canales especificados
     * En producción, aquí se integraría con servicios reales
     */
    private void enviarRecordatorio(Recordatorio recordatorio) {
        log.info("Enviando recordatorio por canales: {}", recordatorio.getCanales());
        
        // Aquí iría la integración real con servicios de mensajería
        // Por ahora solo validamos que tenga mensaje
        if (recordatorio.getMensaje() == null || recordatorio.getMensaje().isEmpty()) {
            throw new RuntimeException("El recordatorio debe tener un mensaje");
        }
    }

    /**
     * RF5.8: Crear recordatorio para una cita
     */
    @Transactional
    public RecordatorioDTO crearRecordatorioCita(Long citaId, int horasAntes) {
        log.info("Creando recordatorio para cita {} con {} horas de anticipación", citaId, horasAntes);
        
        // Buscar la cita
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita", "id", citaId));
        
        // Calcular fecha del recordatorio
        LocalDateTime fechaRecordatorio = cita.getFechaHora().minusHours(horasAntes);
        
        // Verificar que la fecha sea futura
        if (fechaRecordatorio.isBefore(LocalDateTime.now())) {
            throw new ValidacionException("No se puede crear un recordatorio para una fecha pasada");
        }
        
        // Crear el recordatorio
        CrearRecordatorioDTO dto = CrearRecordatorioDTO.builder()
                .usuarioId(cita.getPaciente().getPropietario().getId())
                .citaId(citaId)
                .fechaRecordatorio(fechaRecordatorio)
                .mensaje(String.format("Recordatorio: Cita programada para %s con %s", 
                        cita.getFechaHora().toString(), 
                        cita.getVeterinario().getNombre()))
                .canales(List.of(CanalNotificacion.EMAIL, CanalNotificacion.SMS))
                .build();
        
        return crearRecordatorio(dto);
    }

    /**
     * RF5.9: Actualizar recordatorio
     */
    @Transactional
    public RecordatorioDTO actualizarRecordatorio(Long id, CrearRecordatorioDTO dto) {
        log.info("Actualizando recordatorio: {}", id);
        
        Recordatorio recordatorio = recordatorioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Recordatorio", "id", id));
        
        // No permitir actualizar recordatorios ya enviados
        if (recordatorio.getRecordatorioEnviado()) {
            throw new ValidacionException("No se puede actualizar un recordatorio ya enviado");
        }
        
        // Actualizar campos
        recordatorio.setMensaje(dto.getMensaje());
        recordatorio.setFechaRecordatorio(dto.getFechaRecordatorio());
        
        if (dto.getCanales() != null && !dto.getCanales().isEmpty()) {
            String canalesStr = dto.getCanales().stream()
                    .map(Enum::name)
                    .collect(Collectors.joining(","));
            recordatorio.setCanales(canalesStr);
        }
        
        recordatorio = recordatorioRepository.save(recordatorio);
        log.info("Recordatorio actualizado exitosamente");
        
        return recordatorioMapper.toDTO(recordatorio);
    }

    /**
     * RF5.10: Listar recordatorios por destinatario
     */
    @Transactional(readOnly = true)
    public List<RecordatorioDTO> listarPorDestinatario(Long destinatarioId) {
        log.info("Listando recordatorios del destinatario: {}", destinatarioId);
        
        List<Recordatorio> recordatorios = recordatorioRepository
                .findByDestinatarioIdOrderByFechaRecordatorioDesc(destinatarioId);
        
        return recordatorios.stream()
                .map(recordatorioMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * RF5.11: Marcar recordatorio como enviado
     */
    @Transactional
    public RecordatorioDTO marcarComoEnviado(Long id) {
        log.info("Marcando recordatorio como enviado: {}", id);
        
        Recordatorio recordatorio = recordatorioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Recordatorio", "id", id));
        
        recordatorio.setRecordatorioEnviado(true);
        recordatorio.setFechaEnvio(LocalDateTime.now());
        
        recordatorio = recordatorioRepository.save(recordatorio);
        log.info("Recordatorio marcado como enviado");
        
        return recordatorioMapper.toDTO(recordatorio);
    }

    /**
     * RF5.12: Listar recordatorios por cita
     */
    @Transactional(readOnly = true)
    public List<RecordatorioDTO> listarPorCita(Long citaId) {
        log.info("Listando recordatorios de la cita: {}", citaId);
        
        List<Recordatorio> recordatorios = recordatorioRepository.findByCitaIdOrderByFechaRecordatorioDesc(citaId);
        
        return recordatorios.stream()
                .map(recordatorioMapper::toDTO)
                .collect(Collectors.toList());
    }
}

