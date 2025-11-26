package com.nuclearvet.modulos.pacientes.service;

import com.nuclearvet.common.exception.RecursoNoEncontradoException;
import com.nuclearvet.common.exception.ValidacionException;
import com.nuclearvet.modulos.pacientes.dto.ConsultaDTO;
import com.nuclearvet.modulos.pacientes.dto.CrearConsultaDTO;
import com.nuclearvet.modulos.pacientes.dto.HistoriaClinicaDTO;
import com.nuclearvet.modulos.pacientes.entity.Consulta;
import com.nuclearvet.modulos.pacientes.entity.HistoriaClinica;
import com.nuclearvet.modulos.pacientes.entity.SignoVital;
import com.nuclearvet.modulos.pacientes.mapper.ConsultaMapper;
import com.nuclearvet.modulos.pacientes.repository.ConsultaRepository;
import com.nuclearvet.modulos.pacientes.repository.HistoriaClinicaRepository;
import com.nuclearvet.modulos.pacientes.repository.SignoVitalRepository;
import com.nuclearvet.modulos.usuarios.entity.Usuario;
import com.nuclearvet.modulos.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final HistoriaClinicaRepository historiaClinicaRepository;
    private final SignoVitalRepository signoVitalRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConsultaMapper consultaMapper;

    /**
     * RF2.4: Registrar consulta/atención con signos vitales, diagnóstico y tratamiento
     */
    @Transactional
    public ConsultaDTO registrarConsulta(CrearConsultaDTO dto) {
        log.info("Registrando consulta para historia clínica: {}", dto.getHistoriaClinicaId());

        // Validar que la historia clínica existe
        HistoriaClinica historia = historiaClinicaRepository.findById(dto.getHistoriaClinicaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Esa historia clínica no existe parcero"));

        // Validar que el veterinario existe
        Usuario veterinario = usuarioRepository.findById(dto.getVeterinarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Ese veterinario no existe"));

        // Validar que es veterinario
        boolean esVeterinario = veterinario.getRoles().stream()
                .anyMatch(rol -> rol.getNombre().equals("ROLE_VETERINARIO"));
        
        if (!esVeterinario) {
            throw new ValidacionException("Solo los veterinarios pueden registrar consultas llave");
        }

        // Crear consulta
        Consulta consulta = Consulta.builder()
                .historiaClinica(historia)
                .veterinario(veterinario)
                .fechaConsulta(LocalDateTime.now())
                .motivoConsulta(dto.getMotivoConsulta())
                .tipoServicio(dto.getTipoServicio())
                .diagnostico(dto.getDiagnostico())
                .tratamiento(dto.getTratamiento())
                .observaciones(dto.getObservaciones())
                .proximaRevision(dto.getProximaRevision())
                .activo(true)
                .build();

        consulta = consultaRepository.save(consulta);

        // Registrar signos vitales si vienen (RF2.5)
        if (dto.getSignosVitales() != null) {
            SignoVital signos = SignoVital.builder()
                    .consulta(consulta)
                    .temperatura(dto.getSignosVitales().getTemperatura())
                    .peso(dto.getSignosVitales().getPeso())
                    .frecuenciaCardiaca(dto.getSignosVitales().getFrecuenciaCardiaca())
                    .frecuenciaRespiratoria(dto.getSignosVitales().getFrecuenciaRespiratoria())
                    .presionArterial(dto.getSignosVitales().getPresionArterial())
                    .observaciones(dto.getSignosVitales().getObservaciones())
                    .activo(true)
                    .build();

            signoVitalRepository.save(signos);
        }

        log.info("Consulta registrada con ID: {}", consulta.getId());
        return consultaMapper.toDTO(consulta);
    }

    /**
     * Obtener consulta por ID
     */
    @Transactional(readOnly = true)
    public ConsultaDTO obtenerPorId(Long id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No encontramos esa consulta"));
        return consultaMapper.toDTO(consulta);
    }

    /**
     * RF2.6: Consultar evolución del paciente (todas sus consultas ordenadas)
     */
    @Transactional(readOnly = true)
    public HistoriaClinicaDTO obtenerEvolucionPaciente(Long pacienteId) {
        log.info("Obteniendo evolución del paciente: {}", pacienteId);

        HistoriaClinica historia = historiaClinicaRepository.findByPacienteId(pacienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ese paciente no tiene historia clínica"));

        // Obtener todas las consultas ordenadas por fecha descendente
        List<Consulta> consultas = consultaRepository.findByHistoriaClinicaIdOrderByFechaConsultaDesc(historia.getId());

        List<ConsultaDTO> consultasDTO = consultas.stream()
                .map(consultaMapper::toDTO)
                .collect(Collectors.toList());

        return HistoriaClinicaDTO.builder()
                .id(historia.getId())
                .numeroHistoria(historia.getNumeroHistoria())
                .pacienteId(historia.getPaciente().getId())
                .pacienteNombre(historia.getPaciente().getNombre())
                .alergias(historia.getAlergias())
                .enfermedadesCronicas(historia.getEnfermedadesCronicas())
                .cirugiasPrevias(historia.getCirugiasPrevias())
                .observaciones(historia.getObservaciones())
                .consultas(consultasDTO)
                .build();
    }

    /**
     * Listar consultas de un veterinario
     */
    @Transactional(readOnly = true)
    public List<ConsultaDTO> listarPorVeterinario(Long veterinarioId) {
        log.info("Listando consultas del veterinario: {}", veterinarioId);

        List<Consulta> consultas = consultaRepository.findByVeterinarioId(veterinarioId);
        return consultas.stream()
                .map(consultaMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar consultas por rango de fechas
     */
    @Transactional(readOnly = true)
    public List<ConsultaDTO> listarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Listando consultas desde {} hasta {}", fechaInicio, fechaFin);

        if (fechaInicio.isAfter(fechaFin)) {
            throw new ValidacionException("La fecha de inicio no puede ser mayor a la fecha fin");
        }

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(23, 59, 59);

        List<Consulta> consultas = consultaRepository.findByFechaConsultaBetween(inicio, fin);
        return consultas.stream()
                .map(consultaMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Contar consultas de un paciente
     */
    @Transactional(readOnly = true)
    public Long contarConsultasPaciente(Long historiaClinicaId) {
        return consultaRepository.countByHistoriaClinicaId(historiaClinicaId);
    }

    /**
     * Actualizar consulta
     */
    @Transactional
    public ConsultaDTO actualizarConsulta(Long id, CrearConsultaDTO dto) {
        log.info("Actualizando consulta: {}", id);

        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Esa consulta no existe"));

        consulta.setMotivoConsulta(dto.getMotivoConsulta());
        consulta.setTipoServicio(dto.getTipoServicio());
        consulta.setDiagnostico(dto.getDiagnostico());
        consulta.setTratamiento(dto.getTratamiento());
        consulta.setObservaciones(dto.getObservaciones());
        consulta.setProximaRevision(dto.getProximaRevision());

        // Actualizar signos vitales si existen
        if (dto.getSignosVitales() != null) {
            signoVitalRepository.findByConsultaId(id).ifPresentOrElse(
                signos -> {
                    signos.setTemperatura(dto.getSignosVitales().getTemperatura());
                    signos.setPeso(dto.getSignosVitales().getPeso());
                    signos.setFrecuenciaCardiaca(dto.getSignosVitales().getFrecuenciaCardiaca());
                    signos.setFrecuenciaRespiratoria(dto.getSignosVitales().getFrecuenciaRespiratoria());
                    signos.setPresionArterial(dto.getSignosVitales().getPresionArterial());
                    signos.setObservaciones(dto.getSignosVitales().getObservaciones());
                    signoVitalRepository.save(signos);
                },
                () -> {
                    // Si no existían, crearlos
                    SignoVital nuevosSignos = SignoVital.builder()
                            .consulta(consulta)
                            .temperatura(dto.getSignosVitales().getTemperatura())
                            .peso(dto.getSignosVitales().getPeso())
                            .frecuenciaCardiaca(dto.getSignosVitales().getFrecuenciaCardiaca())
                            .frecuenciaRespiratoria(dto.getSignosVitales().getFrecuenciaRespiratoria())
                            .presionArterial(dto.getSignosVitales().getPresionArterial())
                            .observaciones(dto.getSignosVitales().getObservaciones())
                            .activo(true)
                            .build();
                    signoVitalRepository.save(nuevosSignos);
                }
            );
        }

        consulta = consultaRepository.save(consulta);
        log.info("Consulta actualizada: {}", id);
        return consultaMapper.toDTO(consulta);
    }
}
