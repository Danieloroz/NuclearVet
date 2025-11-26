package com.nuclearvet.modulos.pacientes.service;

import com.nuclearvet.common.exception.ConflictoException;
import com.nuclearvet.common.exception.RecursoNoEncontradoException;
import com.nuclearvet.common.exception.ValidacionException;
import com.nuclearvet.modulos.pacientes.dto.CrearPacienteDTO;
import com.nuclearvet.modulos.pacientes.dto.PacienteDTO;
import com.nuclearvet.modulos.pacientes.entity.HistoriaClinica;
import com.nuclearvet.modulos.pacientes.entity.Paciente;
import com.nuclearvet.modulos.pacientes.mapper.PacienteMapper;
import com.nuclearvet.modulos.pacientes.repository.HistoriaClinicaRepository;
import com.nuclearvet.modulos.pacientes.repository.PacienteRepository;
import com.nuclearvet.modulos.usuarios.entity.Usuario;
import com.nuclearvet.modulos.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final HistoriaClinicaRepository historiaClinicaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PacienteMapper pacienteMapper;

    /**
     * RF2.1: Registrar nuevo paciente con datos básicos y asignar historia clínica
     */
    @Transactional
    public PacienteDTO crearPaciente(CrearPacienteDTO dto) {
        log.info("Creando paciente: {}", dto.getNombre());

        // Validar que el propietario existe
        Usuario propietario = usuarioRepository.findById(dto.getPropietarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Uy parce, ese cliente no existe"));

        // Validar microchip único si viene informado
        if (dto.getMicrochip() != null && !dto.getMicrochip().isBlank()) {
            if (pacienteRepository.findByMicrochip(dto.getMicrochip()).isPresent()) {
                throw new ConflictoException("Ya hay otro paciente con ese microchip llave");
            }
        }

        // Crear paciente
        Paciente paciente = Paciente.builder()
                .nombre(dto.getNombre())
                .especie(dto.getEspecie())
                .raza(dto.getRaza())
                .sexo(dto.getSexo())
                .color(dto.getColor())
                .fechaNacimiento(dto.getFechaNacimiento())
                .microchip(dto.getMicrochip())
                .propietario(propietario)
                .activo(true)
                .build();

        paciente = pacienteRepository.save(paciente);

        // Crear historia clínica automáticamente (RF2.1)
        HistoriaClinica historia = HistoriaClinica.builder()
                .paciente(paciente)
                .alergias(dto.getAlergias())
                .enfermedadesCronicas(dto.getEnfermedadesCronicas())
                .cirugiasPrevias(dto.getCirugiasPrevias())
                .observaciones(dto.getObservaciones())
                .activo(true)
                .build();

        historiaClinicaRepository.save(historia);

        log.info("Paciente creado con ID: {} y historia clínica generada", paciente.getId());
        return pacienteMapper.toDTO(paciente);
    }

    /**
     * RF2.2: Actualizar datos del paciente
     */
    @Transactional
    public PacienteDTO actualizarPaciente(Long id, CrearPacienteDTO dto) {
        log.info("Actualizando paciente con ID: {}", id);

        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ese paciente no existe parcero"));

        // Validar microchip único si cambió
        if (dto.getMicrochip() != null && !dto.getMicrochip().equals(paciente.getMicrochip())) {
            if (pacienteRepository.findByMicrochip(dto.getMicrochip()).isPresent()) {
                throw new ConflictoException("Ya hay otro paciente con ese microchip");
            }
        }

        // Actualizar datos básicos
        paciente.setNombre(dto.getNombre());
        paciente.setEspecie(dto.getEspecie());
        paciente.setRaza(dto.getRaza());
        paciente.setSexo(dto.getSexo());
        paciente.setColor(dto.getColor());
        paciente.setFechaNacimiento(dto.getFechaNacimiento());
        paciente.setMicrochip(dto.getMicrochip());

        // Actualizar historia clínica si existe
        historiaClinicaRepository.findByPacienteId(id).ifPresent(historia -> {
            historia.setAlergias(dto.getAlergias());
            historia.setEnfermedadesCronicas(dto.getEnfermedadesCronicas());
            historia.setCirugiasPrevias(dto.getCirugiasPrevias());
            historia.setObservaciones(dto.getObservaciones());
            historiaClinicaRepository.save(historia);
        });

        paciente = pacienteRepository.save(paciente);
        log.info("Paciente actualizado: {}", id);
        return pacienteMapper.toDTO(paciente);
    }

    /**
     * Obtener paciente por ID
     */
    @Transactional(readOnly = true)
    public PacienteDTO obtenerPorId(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No encontramos ese paciente"));
        return pacienteMapper.toDTO(paciente);
    }

    /**
     * RF2.3: Buscar pacientes por nombre (parcial)
     */
    @Transactional(readOnly = true)
    public List<PacienteDTO> buscarPorNombre(String nombre) {
        log.info("Buscando pacientes por nombre: {}", nombre);
        
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidacionException("El nombre no puede estar vacío llave");
        }

        List<Paciente> pacientes = pacienteRepository.buscarPorNombre(nombre);
        return pacientes.stream()
                .map(pacienteMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar todos los pacientes de un propietario
     */
    @Transactional(readOnly = true)
    public List<PacienteDTO> listarPorPropietario(Long propietarioId) {
        log.info("Listando pacientes del propietario: {}", propietarioId);
        
        // Verificar que el propietario existe
        if (!usuarioRepository.existsById(propietarioId)) {
            throw new RecursoNoEncontradoException("Ese cliente no existe parce");
        }

        List<Paciente> pacientes = pacienteRepository.findByPropietarioIdAndActivoTrue(propietarioId);
        return pacientes.stream()
                .map(pacienteMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar todos los pacientes activos
     */
    @Transactional(readOnly = true)
    public List<PacienteDTO> listarTodos() {
        log.info("Listando todos los pacientes activos");
        List<Paciente> pacientes = pacienteRepository.findAll();
        return pacientes.stream()
                .filter(Paciente::getActivo)
                .map(pacienteMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Filtrar pacientes por especie
     */
    @Transactional(readOnly = true)
    public List<PacienteDTO> listarPorEspecie(String especie) {
        log.info("Listando pacientes por especie: {}", especie);
        List<Paciente> pacientes = pacienteRepository.findByEspecieAndActivoTrue(especie);
        return pacientes.stream()
                .map(pacienteMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Desactivar paciente (soft delete)
     */
    @Transactional
    public void desactivarPaciente(Long id) {
        log.info("Desactivando paciente: {}", id);
        
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ese paciente no existe"));

        paciente.setActivo(false);
        pacienteRepository.save(paciente);
        
        // También desactivar la historia clínica
        historiaClinicaRepository.findByPacienteId(id).ifPresent(historia -> {
            historia.setActivo(false);
            historiaClinicaRepository.save(historia);
        });

        log.info("Paciente desactivado: {}", id);
    }
}
