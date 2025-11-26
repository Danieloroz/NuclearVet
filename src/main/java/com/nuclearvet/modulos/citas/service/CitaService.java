package com.nuclearvet.modulos.citas.service;

import com.nuclearvet.common.exception.ConflictoException;
import com.nuclearvet.common.exception.RecursoNoEncontradoException;
import com.nuclearvet.common.exception.ValidacionException;
import com.nuclearvet.modulos.citas.dto.CancelarCitaDTO;
import com.nuclearvet.modulos.citas.dto.CitaDTO;
import com.nuclearvet.modulos.citas.dto.CrearCitaDTO;
import com.nuclearvet.modulos.citas.entity.Cita;
import com.nuclearvet.modulos.citas.mapper.CitaMapper;
import com.nuclearvet.modulos.citas.repository.CitaRepository;
import com.nuclearvet.modulos.pacientes.entity.Paciente;
import com.nuclearvet.modulos.pacientes.repository.PacienteRepository;
import com.nuclearvet.modulos.usuarios.entity.Usuario;
import com.nuclearvet.modulos.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de citas.
 * Implementa RF3.1 a RF3.5
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CitaService {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final CitaMapper citaMapper;

    /**
     * RF3.1: Crear una nueva cita
     */
    @Transactional
    public CitaDTO crearCita(CrearCitaDTO dto) {
        log.info("Creando cita para paciente: {} con veterinario: {}", dto.getPacienteId(), dto.getVeterinarioId());

        // Validar que el paciente existe
        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente", "id", dto.getPacienteId()));

        // Validar que el veterinario existe
        Usuario veterinario = usuarioRepository.findById(dto.getVeterinarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Veterinario", "id", dto.getVeterinarioId()));

        // Validar que el veterinario tiene el rol correcto
        if (!veterinario.tieneRol("VETERINARIO") && !veterinario.tieneRol("ADMIN")) {
            throw new ValidacionException("El usuario seleccionado no es veterinario, parce");
        }

        // Verificar disponibilidad (RF3.5)
        if (!verificarDisponibilidadInterna(dto.getVeterinarioId(), dto.getFechaHora(), dto.getDuracionMinutos())) {
            throw new ConflictoException("El veterinario ya tiene una cita programada en ese horario");
        }

        // Crear la cita
        Cita cita = Cita.builder()
                .paciente(paciente)
                .veterinario(veterinario)
                .fechaHora(dto.getFechaHora())
                .tipoServicio(dto.getTipoServicio())
                .estado("PROGRAMADA")
                .motivo(dto.getMotivo())
                .duracionMinutos(dto.getDuracionMinutos() != null ? dto.getDuracionMinutos() : 30)
                .observaciones(dto.getObservaciones())
                .build();

        cita = citaRepository.save(cita);
        log.info("Cita creada exitosamente con ID: {}", cita.getId());
        return citaMapper.toDTO(cita);
    }

    /**
     * RF3.2: Actualizar una cita existente
     */
    @Transactional
    public CitaDTO actualizarCita(Long id, CrearCitaDTO dto) {
        log.info("Actualizando cita con ID: {}", id);

        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita", "id", id));

        // No permitir actualizar citas completadas o canceladas
        if ("COMPLETADA".equals(cita.getEstado()) || "CANCELADA".equals(cita.getEstado())) {
            throw new ValidacionException("No se puede actualizar una cita que ya está " + cita.getEstado().toLowerCase());
        }

        // Si cambia el veterinario, validar
        if (!cita.getVeterinario().getId().equals(dto.getVeterinarioId())) {
            Usuario nuevoVeterinario = usuarioRepository.findById(dto.getVeterinarioId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Veterinario", "id", dto.getVeterinarioId()));

            if (!nuevoVeterinario.tieneRol("VETERINARIO") && !nuevoVeterinario.tieneRol("ADMIN")) {
                throw new ValidacionException("El usuario seleccionado no es veterinario");
            }

            cita.setVeterinario(nuevoVeterinario);
        }

        // Si cambia la fecha/hora, verificar disponibilidad
        if (!cita.getFechaHora().equals(dto.getFechaHora())) {
            if (!verificarDisponibilidadInterna(dto.getVeterinarioId(), dto.getFechaHora(), dto.getDuracionMinutos(), id)) {
                throw new ConflictoException("El veterinario ya tiene una cita en ese horario");
            }
            cita.setFechaHora(dto.getFechaHora());
        }

        // Actualizar otros campos
        cita.setTipoServicio(dto.getTipoServicio());
        cita.setMotivo(dto.getMotivo());
        cita.setDuracionMinutos(dto.getDuracionMinutos());
        cita.setObservaciones(dto.getObservaciones());

        cita = citaRepository.save(cita);
        log.info("Cita actualizada exitosamente: {}", id);
        return citaMapper.toDTO(cita);
    }

    /**
     * RF3.3: Cancelar una cita
     */
    @Transactional
    public CitaDTO cancelarCita(Long id, CancelarCitaDTO dto) {
        log.info("Cancelando cita con ID: {}", id);

        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita", "id", id));

        if ("COMPLETADA".equals(cita.getEstado())) {
            throw new ValidacionException("No se puede cancelar una cita que ya fue completada");
        }

        if ("CANCELADA".equals(cita.getEstado())) {
            throw new ValidacionException("Esta cita ya está cancelada, llave");
        }

        cita.setEstado("CANCELADA");
        cita.setMotivoCancelacion(dto.getMotivoCancelacion());

        cita = citaRepository.save(cita);
        log.info("Cita cancelada exitosamente: {}", id);
        return citaMapper.toDTO(cita);
    }

    /**
     * RF3.4: Consultar agenda de un veterinario
     */
    @Transactional(readOnly = true)
    public List<CitaDTO> consultarAgenda(Long veterinarioId, LocalDate fecha) {
        log.info("Consultando agenda del veterinario: {} para fecha: {}", veterinarioId, fecha);

        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(LocalTime.MAX);

        List<Cita> citas = citaRepository.findByVeterinarioIdAndFechaHoraBetweenOrderByFechaHoraAsc(veterinarioId, inicio, fin);

        return citas.stream()
                .map(citaMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * RF3.5: Verificar disponibilidad de un veterinario
     */
    @Transactional(readOnly = true)
    public boolean verificarDisponibilidad(Long veterinarioId, LocalDateTime fechaHora, Integer duracionMinutos) {
        return verificarDisponibilidadInterna(veterinarioId, fechaHora, duracionMinutos);
    }

    /**
     * Método interno para verificar disponibilidad
     */
    private boolean verificarDisponibilidadInterna(Long veterinarioId, LocalDateTime fechaHora, Integer duracionMinutos) {
        return verificarDisponibilidadInterna(veterinarioId, fechaHora, duracionMinutos, null);
    }

    /**
     * Método interno para verificar disponibilidad excluyendo una cita específica (para actualización)
     */
    private boolean verificarDisponibilidadInterna(Long veterinarioId, LocalDateTime fechaHora, Integer duracionMinutos, Long citaIdExcluir) {
        int duracion = duracionMinutos != null ? duracionMinutos : 30;
        LocalDateTime finCita = fechaHora.plusMinutes(duracion);

        // Buscar citas que se solapen con el rango de tiempo
        LocalDateTime inicioBusqueda = fechaHora.minusMinutes(duracion);
        LocalDateTime finBusqueda = finCita;

        List<Cita> citasEnRango = citaRepository.buscarCitasEnRango(
                veterinarioId,
                inicioBusqueda,
                finBusqueda
        );

        // Filtrar citas canceladas y la cita actual (si estamos actualizando)
        long conflictos = citasEnRango.stream()
                .filter(c -> !"CANCELADA".equals(c.getEstado()))
                .filter(c -> citaIdExcluir == null || !c.getId().equals(citaIdExcluir))
                .count();

        return conflictos == 0;
    }

    /**
     * Obtener cita por ID
     */
    @Transactional(readOnly = true)
    public CitaDTO obtenerPorId(Long id) {
        log.debug("Buscando cita con ID: {}", id);
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita", "id", id));
        return citaMapper.toDTO(cita);
    }

    /**
     * Listar todas las citas del día actual
     */
    @Transactional(readOnly = true)
    public List<CitaDTO> citasDelDia() {
        log.info("Consultando citas del día actual");
        List<Cita> citas = citaRepository.buscarTodasCitasDelDia();
        return citas.stream()
                .map(citaMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Confirmar una cita
     */
    @Transactional
    public CitaDTO confirmarCita(Long id) {
        log.info("Confirmando cita con ID: {}", id);

        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita", "id", id));

        if (!"PROGRAMADA".equals(cita.getEstado())) {
            throw new ValidacionException("Solo se pueden confirmar citas programadas");
        }

        cita.setEstado("CONFIRMADA");
        cita = citaRepository.save(cita);
        log.info("Cita confirmada: {}", id);
        return citaMapper.toDTO(cita);
    }

    /**
     * Iniciar una cita (cambiar estado a EN_CURSO)
     */
    @Transactional
    public CitaDTO iniciarCita(Long id) {
        log.info("Iniciando cita con ID: {}", id);

        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita", "id", id));

        if (!"PROGRAMADA".equals(cita.getEstado()) && !"CONFIRMADA".equals(cita.getEstado())) {
            throw new ValidacionException("Solo se pueden iniciar citas programadas o confirmadas");
        }

        cita.setEstado("EN_CURSO");
        cita = citaRepository.save(cita);
        log.info("Cita iniciada: {}", id);
        return citaMapper.toDTO(cita);
    }

    /**
     * Completar una cita
     */
    @Transactional
    public CitaDTO completarCita(Long id) {
        log.info("Completando cita con ID: {}", id);

        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita", "id", id));

        if (!"EN_CURSO".equals(cita.getEstado())) {
            throw new ValidacionException("Solo se pueden completar citas que están en curso");
        }

        cita.setEstado("COMPLETADA");
        cita = citaRepository.save(cita);
        log.info("Cita completada: {}", id);
        return citaMapper.toDTO(cita);
    }

    /**
     * Marcar una cita como "no asistió"
     */
    @Transactional
    public CitaDTO marcarNoAsistio(Long id) {
        log.info("Marcando cita con ID: {} como no asistió", id);

        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita", "id", id));

        if ("COMPLETADA".equals(cita.getEstado()) || "CANCELADA".equals(cita.getEstado())) {
            throw new ValidacionException("No se puede marcar como no asistió una cita completada o cancelada");
        }

        cita.setEstado("NO_ASISTIO");
        cita = citaRepository.save(cita);
        log.info("Cita marcada como no asistió: {}", id);
        return citaMapper.toDTO(cita);
    }
}
