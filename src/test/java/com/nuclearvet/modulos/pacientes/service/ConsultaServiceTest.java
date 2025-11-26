package com.nuclearvet.modulos.pacientes.service;

import com.nuclearvet.common.exception.RecursoNoEncontradoException;
import com.nuclearvet.common.exception.ValidacionException;
import com.nuclearvet.modulos.pacientes.dto.ConsultaDTO;
import com.nuclearvet.modulos.pacientes.dto.CrearConsultaDTO;
import com.nuclearvet.modulos.pacientes.dto.HistoriaClinicaDTO;
import com.nuclearvet.modulos.pacientes.dto.SignoVitalDTO;
import com.nuclearvet.modulos.pacientes.entity.*;
import com.nuclearvet.modulos.pacientes.mapper.ConsultaMapper;
import com.nuclearvet.modulos.pacientes.repository.ConsultaRepository;
import com.nuclearvet.modulos.pacientes.repository.HistoriaClinicaRepository;
import com.nuclearvet.modulos.pacientes.repository.SignoVitalRepository;
import com.nuclearvet.modulos.usuarios.entity.Rol;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConsultaService - Tests de lógica de negocio")
class ConsultaServiceTest {

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private HistoriaClinicaRepository historiaClinicaRepository;

    @Mock
    private SignoVitalRepository signoVitalRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ConsultaMapper consultaMapper;

    @InjectMocks
    private ConsultaService consultaService;

    private CrearConsultaDTO crearConsultaDTO;
    private Usuario veterinario;
    private Paciente paciente;
    private HistoriaClinica historiaClinica;
    private Consulta consulta;
    private ConsultaDTO consultaDTO;
    private SignoVital signoVital;

    @BeforeEach
    void setUp() {
        // Setup rol veterinario
        Rol rolVeterinario = Rol.builder()
                .id(1L)
                .nombre("ROLE_VETERINARIO")
                .build();

        // Setup veterinario
        veterinario = Usuario.builder()
                .id(1L)
                .nombreCompleto("Dr. Carlos Pérez")
                .email("carlos@nuclearvet.com")
                .roles(new HashSet<>(Arrays.asList(rolVeterinario)))
                .activo(true)
                .build();

        // Setup paciente
        paciente = Paciente.builder()
                .id(1L)
                .nombre("Firulais")
                .especie("Canino")
                .fechaNacimiento(LocalDate.of(2020, 1, 1))
                .build();

        // Setup historia clínica
        historiaClinica = HistoriaClinica.builder()
                .id(1L)
                .paciente(paciente)
                .numeroHistoria("HC-2024-00001")
                .build();

        // Setup signos vitales DTO
        SignoVitalDTO signosDTO = SignoVitalDTO.builder()
                .temperatura(38.5)
                .peso(25.0)
                .frecuenciaCardiaca(90)
                .frecuenciaRespiratoria(30)
                .presionArterial("120/80")
                .build();

        // Setup crear consulta DTO
        crearConsultaDTO = CrearConsultaDTO.builder()
                .historiaClinicaId(1L)
                .veterinarioId(1L)
                .motivoConsulta("Vacunación anual")
                .tipoServicio("CONSULTA")
                .diagnostico("Paciente saludable")
                .tratamiento("Vacuna antirrábica")
                .observaciones("Próximo control en 1 año")
                .signosVitales(signosDTO)
                .build();

        // Setup consulta
        consulta = Consulta.builder()
                .id(1L)
                .historiaClinica(historiaClinica)
                .veterinario(veterinario)
                .fechaConsulta(LocalDateTime.now())
                .motivoConsulta("Vacunación anual")
                .tipoServicio("CONSULTA")
                .diagnostico("Paciente saludable")
                .tratamiento("Vacuna antirrábica")
                .activo(true)
                .build();

        // Setup consulta DTO
        consultaDTO = ConsultaDTO.builder()
                .id(1L)
                .historiaClinicaId(1L)
                .pacienteNombre("Firulais")
                .veterinarioId(1L)
                .veterinarioNombre("Dr. Carlos Pérez")
                .motivoConsulta("Vacunación anual")
                .diagnostico("Paciente saludable")
                .build();

        // Setup signos vitales
        signoVital = SignoVital.builder()
                .id(1L)
                .consulta(consulta)
                .temperatura(38.5)
                .peso(25.0)
                .build();
    }

    @Test
    @DisplayName("Registrar consulta - Exitoso")
    void testRegistrarConsultaExitoso() {
        // Given
        when(historiaClinicaRepository.findById(1L)).thenReturn(Optional.of(historiaClinica));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
        when(consultaRepository.save(any(Consulta.class))).thenReturn(consulta);
        when(signoVitalRepository.save(any(SignoVital.class))).thenReturn(signoVital);
        when(consultaMapper.toDTO(consulta)).thenReturn(consultaDTO);

        // When
        ConsultaDTO resultado = consultaService.registrarConsulta(crearConsultaDTO);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getMotivoConsulta()).isEqualTo("Vacunación anual");
        verify(consultaRepository).save(any(Consulta.class));
        verify(signoVitalRepository).save(any(SignoVital.class));
    }

    @Test
    @DisplayName("Registrar consulta - Historia clínica no existe")
    void testRegistrarConsultaHistoriaNoExiste() {
        // Given
        when(historiaClinicaRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> consultaService.registrarConsulta(crearConsultaDTO))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("historia clínica no existe");
    }

    @Test
    @DisplayName("Registrar consulta - Veterinario no existe")
    void testRegistrarConsultaVeterinarioNoExiste() {
        // Given
        when(historiaClinicaRepository.findById(1L)).thenReturn(Optional.of(historiaClinica));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> consultaService.registrarConsulta(crearConsultaDTO))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("veterinario no existe");
    }

    @Test
    @DisplayName("Registrar consulta - Usuario no es veterinario")
    void testRegistrarConsultaNoEsVeterinario() {
        // Given
        Rol rolCliente = Rol.builder().nombre("ROLE_CLIENTE").build();
        Usuario cliente = Usuario.builder()
                .id(2L)
                .nombreCompleto("Juan Cliente")
                .roles(new HashSet<>(Arrays.asList(rolCliente)))
                .build();

        when(historiaClinicaRepository.findById(1L)).thenReturn(Optional.of(historiaClinica));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(cliente));

        crearConsultaDTO.setVeterinarioId(2L);

        // When & Then
        assertThatThrownBy(() -> consultaService.registrarConsulta(crearConsultaDTO))
                .isInstanceOf(ValidacionException.class)
                .hasMessageContaining("veterinarios pueden registrar consultas");
    }

    @Test
    @DisplayName("Obtener consulta por ID - Exitoso")
    void testObtenerPorIdExitoso() {
        // Given
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        when(consultaMapper.toDTO(consulta)).thenReturn(consultaDTO);

        // When
        ConsultaDTO resultado = consultaService.obtenerPorId(1L);

        // Then
        assertThat(resultado).isNotNull();
        verify(consultaRepository).findById(1L);
    }

    @Test
    @DisplayName("Obtener consulta por ID - No encontrada")
    void testObtenerPorIdNoEncontrada() {
        // Given
        when(consultaRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> consultaService.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("Obtener evolución del paciente - Exitoso")
    void testObtenerEvolucionPacienteExitoso() {
        // Given
        List<Consulta> consultas = Arrays.asList(consulta);
        when(historiaClinicaRepository.findByPacienteId(1L)).thenReturn(Optional.of(historiaClinica));
        when(consultaRepository.findByHistoriaClinicaIdOrderByFechaConsultaDesc(1L)).thenReturn(consultas);
        when(consultaMapper.toDTO(any(Consulta.class))).thenReturn(consultaDTO);

        // When
        HistoriaClinicaDTO resultado = consultaService.obtenerEvolucionPaciente(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getConsultas()).hasSize(1);
        verify(historiaClinicaRepository).findByPacienteId(1L);
    }

    @Test
    @DisplayName("Obtener evolución - Paciente sin historia clínica")
    void testObtenerEvolucionSinHistoria() {
        // Given
        when(historiaClinicaRepository.findByPacienteId(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> consultaService.obtenerEvolucionPaciente(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("no tiene historia clínica");
    }

    @Test
    @DisplayName("Listar consultas por veterinario - Exitoso")
    void testListarPorVeterinarioExitoso() {
        // Given
        List<Consulta> consultas = Arrays.asList(consulta);
        when(consultaRepository.findByVeterinarioId(1L)).thenReturn(consultas);
        when(consultaMapper.toDTO(any(Consulta.class))).thenReturn(consultaDTO);

        // When
        List<ConsultaDTO> resultado = consultaService.listarPorVeterinario(1L);

        // Then
        assertThat(resultado).hasSize(1);
        verify(consultaRepository).findByVeterinarioId(1L);
    }

    @Test
    @DisplayName("Listar consultas por rango de fechas - Exitoso")
    void testListarPorRangoFechasExitoso() {
        // Given
        LocalDate fechaInicio = LocalDate.of(2024, 1, 1);
        LocalDate fechaFin = LocalDate.of(2024, 12, 31);
        List<Consulta> consultas = Arrays.asList(consulta);
        
        when(consultaRepository.findByFechaConsultaBetween(any(), any())).thenReturn(consultas);
        when(consultaMapper.toDTO(any(Consulta.class))).thenReturn(consultaDTO);

        // When
        List<ConsultaDTO> resultado = consultaService.listarPorRangoFechas(fechaInicio, fechaFin);

        // Then
        assertThat(resultado).hasSize(1);
        verify(consultaRepository).findByFechaConsultaBetween(any(), any());
    }

    @Test
    @DisplayName("Listar por rango de fechas - Fecha inicio posterior a fecha fin")
    void testListarPorRangoFechasInvalido() {
        // Given
        LocalDate fechaInicio = LocalDate.of(2024, 12, 31);
        LocalDate fechaFin = LocalDate.of(2024, 1, 1);

        // When & Then
        assertThatThrownBy(() -> consultaService.listarPorRangoFechas(fechaInicio, fechaFin))
                .isInstanceOf(ValidacionException.class)
                .hasMessageContaining("fecha de inicio no puede ser mayor");
    }

    @Test
    @DisplayName("Contar consultas de paciente - Exitoso")
    void testContarConsultasPaciente() {
        // Given
        when(consultaRepository.countByHistoriaClinicaId(1L)).thenReturn(5L);

        // When
        Long resultado = consultaService.contarConsultasPaciente(1L);

        // Then
        assertThat(resultado).isEqualTo(5L);
        verify(consultaRepository).countByHistoriaClinicaId(1L);
    }

    @Test
    @DisplayName("Actualizar consulta - Exitoso")
    void testActualizarConsultaExitoso() {
        // Given
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        when(signoVitalRepository.findByConsultaId(1L)).thenReturn(Optional.of(signoVital));
        when(consultaRepository.save(any(Consulta.class))).thenReturn(consulta);
        when(consultaMapper.toDTO(consulta)).thenReturn(consultaDTO);

        // When
        ConsultaDTO resultado = consultaService.actualizarConsulta(1L, crearConsultaDTO);

        // Then
        assertThat(resultado).isNotNull();
        verify(consultaRepository).save(any(Consulta.class));
        verify(signoVitalRepository).save(any(SignoVital.class));
    }

    @Test
    @DisplayName("Actualizar consulta - No existe")
    void testActualizarConsultaNoExiste() {
        // Given
        when(consultaRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> consultaService.actualizarConsulta(99L, crearConsultaDTO))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("Actualizar consulta - Crear signos vitales si no existen")
    void testActualizarConsultaCrearSignosVitales() {
        // Given
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        when(signoVitalRepository.findByConsultaId(1L)).thenReturn(Optional.empty());
        when(consultaRepository.save(any(Consulta.class))).thenReturn(consulta);
        when(consultaMapper.toDTO(consulta)).thenReturn(consultaDTO);

        // When
        ConsultaDTO resultado = consultaService.actualizarConsulta(1L, crearConsultaDTO);

        // Then
        assertThat(resultado).isNotNull();
        verify(signoVitalRepository).save(any(SignoVital.class));
    }
}
