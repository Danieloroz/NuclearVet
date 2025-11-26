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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PacienteService - Tests de lógica de negocio")
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private HistoriaClinicaRepository historiaClinicaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PacienteMapper pacienteMapper;

    @InjectMocks
    private PacienteService pacienteService;

    private CrearPacienteDTO crearPacienteDTO;
    private Usuario propietario;
    private Paciente paciente;
    private PacienteDTO pacienteDTO;
    private HistoriaClinica historiaClinica;

    @BeforeEach
    void setUp() {
        // Setup propietario
        propietario = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@example.com")
                .build();
        propietario.setActivo(true);

        // Setup DTO
        crearPacienteDTO = CrearPacienteDTO.builder()
                .nombre("Firulais")
                .especie("Canino")
                .raza("Labrador")
                .sexo("Macho")
                .color("Amarillo")
                .fechaNacimiento(LocalDate.of(2020, 5, 15))
                .microchip("123456789")
                .propietarioId(1L)
                .build();

        // Setup paciente
        paciente = Paciente.builder()
                .id(1L)
                .nombre("Firulais")
                .especie("Canino")
                .raza("Labrador")
                .sexo("Macho")
                .color("Amarillo")
                .fechaNacimiento(LocalDate.of(2020, 5, 15))
                .microchip("123456789")
                .propietario(propietario)
                .build();
        paciente.setActivo(true);

        // Setup DTO respuesta
        pacienteDTO = PacienteDTO.builder()
                .id(1L)
                .nombre("Firulais")
                .especie("Canino")
                .raza("Labrador")
                .sexo("Macho")
                .propietarioId(1L)
                .nombrePropietario("Juan Pérez")
                .edadEnAnios(3)
                .build();

        // Setup historia clínica
        historiaClinica = HistoriaClinica.builder()
                .id(1L)
                .paciente(paciente)
                .numeroHistoria("HC-2024-00001")
                .build();
        historiaClinica.setActivo(true);
    }

    @Test
    @DisplayName("Crear paciente - Exitoso")
    void testCrearPacienteExitoso() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(propietario));
        when(pacienteRepository.findByMicrochip("123456789")).thenReturn(Optional.empty());
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(paciente);
        when(historiaClinicaRepository.save(any(HistoriaClinica.class))).thenReturn(historiaClinica);
        when(pacienteMapper.toDTO(paciente)).thenReturn(pacienteDTO);

        // When
        PacienteDTO resultado = pacienteService.crearPaciente(crearPacienteDTO);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Firulais");
        verify(usuarioRepository).findById(1L);
        verify(pacienteRepository).save(any(Paciente.class));
        verify(historiaClinicaRepository).save(any(HistoriaClinica.class));
    }

    @Test
    @DisplayName("Crear paciente - Propietario no existe")
    void testCrearPacientePropietarioNoExiste() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> pacienteService.crearPaciente(crearPacienteDTO))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("cliente no existe");
    }

    @Test
    @DisplayName("Crear paciente - Microchip duplicado")
    void testCrearPacienteMicrochipDuplicado() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(propietario));
        when(pacienteRepository.findByMicrochip("123456789")).thenReturn(Optional.of(paciente));

        // When & Then
        assertThatThrownBy(() -> pacienteService.crearPaciente(crearPacienteDTO))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("microchip");
    }

    @Test
    @DisplayName("Actualizar paciente - Exitoso")
    void testActualizarPacienteExitoso() {
        // Given
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(historiaClinicaRepository.findByPacienteId(1L)).thenReturn(Optional.of(historiaClinica));
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(paciente);
        when(pacienteMapper.toDTO(paciente)).thenReturn(pacienteDTO);

        // When
        PacienteDTO resultado = pacienteService.actualizarPaciente(1L, crearPacienteDTO);

        // Then
        assertThat(resultado).isNotNull();
        verify(pacienteRepository).save(any(Paciente.class));
        verify(historiaClinicaRepository).save(any(HistoriaClinica.class));
    }

    @Test
    @DisplayName("Actualizar paciente - No existe")
    void testActualizarPacienteNoExiste() {
        // Given
        when(pacienteRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> pacienteService.actualizarPaciente(1L, crearPacienteDTO))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("no existe");
    }

    @Test
    @DisplayName("Obtener paciente por ID - Exitoso")
    void testObtenerPorIdExitoso() {
        // Given
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(pacienteMapper.toDTO(paciente)).thenReturn(pacienteDTO);

        // When
        PacienteDTO resultado = pacienteService.obtenerPorId(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(pacienteRepository).findById(1L);
    }

    @Test
    @DisplayName("Obtener paciente por ID - No encontrado")
    void testObtenerPorIdNoEncontrado() {
        // Given
        when(pacienteRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> pacienteService.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("Buscar por nombre - Exitoso")
    void testBuscarPorNombreExitoso() {
        // Given
        List<Paciente> pacientes = Arrays.asList(paciente);
        when(pacienteRepository.buscarPorNombre("Firu")).thenReturn(pacientes);
        when(pacienteMapper.toDTO(any(Paciente.class))).thenReturn(pacienteDTO);

        // When
        List<PacienteDTO> resultado = pacienteService.buscarPorNombre("Firu");

        // Then
        assertThat(resultado).hasSize(1);
        verify(pacienteRepository).buscarPorNombre("Firu");
    }

    @Test
    @DisplayName("Buscar por nombre - Nombre vacío")
    void testBuscarPorNombreVacio() {
        // When & Then
        assertThatThrownBy(() -> pacienteService.buscarPorNombre(""))
                .isInstanceOf(ValidacionException.class)
                .hasMessageContaining("vacío");
    }

    @Test
    @DisplayName("Listar por propietario - Exitoso")
    void testListarPorPropietarioExitoso() {
        // Given
        List<Paciente> pacientes = Arrays.asList(paciente);
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(pacienteRepository.findByPropietarioIdAndActivoTrue(1L)).thenReturn(pacientes);
        when(pacienteMapper.toDTO(any(Paciente.class))).thenReturn(pacienteDTO);

        // When
        List<PacienteDTO> resultado = pacienteService.listarPorPropietario(1L);

        // Then
        assertThat(resultado).hasSize(1);
        verify(usuarioRepository).existsById(1L);
    }

    @Test
    @DisplayName("Listar por propietario - Cliente no existe")
    void testListarPorPropietarioNoExiste() {
        // Given
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> pacienteService.listarPorPropietario(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("cliente no existe");
    }

    @Test
    @DisplayName("Listar todos - Exitoso")
    void testListarTodos() {
        // Given
        List<Paciente> pacientes = Arrays.asList(paciente);
        when(pacienteRepository.findAll()).thenReturn(pacientes);
        when(pacienteMapper.toDTO(any(Paciente.class))).thenReturn(pacienteDTO);

        // When
        List<PacienteDTO> resultado = pacienteService.listarTodos();

        // Then
        assertThat(resultado).hasSize(1);
        verify(pacienteRepository).findAll();
    }

    @Test
    @DisplayName("Listar por especie - Exitoso")
    void testListarPorEspecie() {
        // Given
        List<Paciente> pacientes = Arrays.asList(paciente);
        when(pacienteRepository.findByEspecieAndActivoTrue("Canino")).thenReturn(pacientes);
        when(pacienteMapper.toDTO(any(Paciente.class))).thenReturn(pacienteDTO);

        // When
        List<PacienteDTO> resultado = pacienteService.listarPorEspecie("Canino");

        // Then
        assertThat(resultado).hasSize(1);
        verify(pacienteRepository).findByEspecieAndActivoTrue("Canino");
    }

    @Test
    @DisplayName("Desactivar paciente - Exitoso")
    void testDesactivarPacienteExitoso() {
        // Given
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(historiaClinicaRepository.findByPacienteId(1L)).thenReturn(Optional.of(historiaClinica));

        // When
        pacienteService.desactivarPaciente(1L);

        // Then
        assertThat(paciente.getActivo()).isFalse();
        verify(pacienteRepository).save(paciente);
        verify(historiaClinicaRepository).save(historiaClinica);
    }

    @Test
    @DisplayName("Desactivar paciente - No existe")
    void testDesactivarPacienteNoExiste() {
        // Given
        when(pacienteRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> pacienteService.desactivarPaciente(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
