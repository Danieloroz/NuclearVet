package com.nuclearvet.modulos.notificaciones.service;

import com.nuclearvet.common.exception.RecursoNoEncontradoException;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Recordatorio Service Tests")
class RecordatorioServiceTest {

    @Mock
    private RecordatorioRepository recordatorioRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private NotificacionService notificacionService;

    @Mock
    private RecordatorioMapper recordatorioMapper;

    @InjectMocks
    private RecordatorioService recordatorioService;

    private Usuario usuario;
    private Paciente paciente;
    private Recordatorio recordatorio;
    private RecordatorioDTO recordatorioDTO;
    private CrearRecordatorioDTO crearRecordatorioDTO;

    @BeforeEach
    void setUp() {
        // Usuario
        usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan.perez@test.com")
                .build();
        usuario.setActivo(true);

        // Paciente
        paciente = Paciente.builder()
                .id(1L)
                .nombre("Firulais")
                .especie("Canino")
                .build();
        paciente.setActivo(true);

        // Recordatorio
        recordatorio = Recordatorio.builder()
                .usuario(usuario)
                .paciente(paciente)
                .asunto("Vacunación anual")
                .mensaje("Recordatorio de vacunación")
                .fechaRecordatorio(LocalDateTime.now().plusDays(1))
                .recordatorioEnviado(false)
                .build();
        recordatorio.setActivo(true);

        // RecordatorioDTO
        recordatorioDTO = RecordatorioDTO.builder()
                .id(1L)
                .usuarioId(1L)
                .pacienteId(1L)
                .pacienteNombre("Firulais")
                .asunto("Vacunación anual")
                .mensaje("Recordatorio de vacunación")
                .fechaRecordatorio(LocalDateTime.now().plusDays(1))
                .canales("EMAIL")
                .recordatorioEnviado(false)
                .build();

        // CrearRecordatorioDTO
        crearRecordatorioDTO = CrearRecordatorioDTO.builder()
                .usuarioId(1L)
                .pacienteId(1L)
                .asunto("Vacunación anual")
                .mensaje("Recordatorio de vacunación")
                .fechaRecordatorio(LocalDateTime.now().plusDays(1))
                .canales(Arrays.asList(CanalNotificacion.EMAIL))
                .build();
    }

    @Test
    @DisplayName("Crear recordatorio - Exitoso")
    void testCrearRecordatorioExitoso() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(recordatorioMapper.toEntity(any(CrearRecordatorioDTO.class))).thenReturn(recordatorio);
        when(recordatorioRepository.save(any(Recordatorio.class))).thenReturn(recordatorio);
        when(recordatorioMapper.toDTO(any(Recordatorio.class))).thenReturn(recordatorioDTO);

        // When
        RecordatorioDTO resultado = recordatorioService.crearRecordatorio(crearRecordatorioDTO);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getAsunto()).isEqualTo("Vacunación anual");
        verify(recordatorioRepository).save(any(Recordatorio.class));
    }

    @Test
    @DisplayName("Crear recordatorio - Usuario no existe")
    void testCrearRecordatorioUsuarioNoExiste() {
        // Given - citaId is null in crearRecordatorioDTO, so cita validation is skipped
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> recordatorioService.crearRecordatorio(crearRecordatorioDTO))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Usuario");
    }

    @Test
    @DisplayName("Crear recordatorio - Paciente no existe")
    void testCrearRecordatorioPacienteNoExiste() {
        // Given - citaId is null in crearRecordatorioDTO, so cita validation is skipped
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(pacienteRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> recordatorioService.crearRecordatorio(crearRecordatorioDTO))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Paciente");
    }

    @Test
    @DisplayName("Listar por usuario - Exitoso")
    void testListarPorUsuarioExitoso() {
        // Given
        List<Recordatorio> recordatorios = Arrays.asList(recordatorio);
        when(recordatorioRepository.findByUsuarioIdOrderByFechaRecordatorioDesc(1L))
                .thenReturn(recordatorios);
        when(recordatorioMapper.toDTO(any(Recordatorio.class))).thenReturn(recordatorioDTO);

        // When
        List<RecordatorioDTO> resultado = recordatorioService.listarPorUsuario(1L);

        // Then
        assertThat(resultado).hasSize(1);
        verify(recordatorioRepository).findByUsuarioIdOrderByFechaRecordatorioDesc(1L);
    }

    @Test
    @DisplayName("Listar por paciente - Exitoso")
    void testListarPorPacienteExitoso() {
        // Given
        List<Recordatorio> recordatorios = Arrays.asList(recordatorio);
        when(recordatorioRepository.findByPacienteIdOrderByFechaRecordatorioDesc(1L))
                .thenReturn(recordatorios);
        when(recordatorioMapper.toDTO(any(Recordatorio.class))).thenReturn(recordatorioDTO);

        // When
        List<RecordatorioDTO> resultado = recordatorioService.listarPorPaciente(1L);

        // Then
        assertThat(resultado).hasSize(1);
        verify(recordatorioRepository).findByPacienteIdOrderByFechaRecordatorioDesc(1L);
    }

    @Test
    @DisplayName("Listar pendientes - Exitoso")
    void testListarPendientesExitoso() {
        // Given
        List<Recordatorio> recordatorios = Arrays.asList(recordatorio);
        when(recordatorioRepository.findRecordatoriosPendientes(any(LocalDateTime.class)))
                .thenReturn(recordatorios);
        when(recordatorioMapper.toDTO(any(Recordatorio.class))).thenReturn(recordatorioDTO);

        // When
        List<RecordatorioDTO> resultado = recordatorioService.listarPendientes();

        // Then
        assertThat(resultado).hasSize(1);
        verify(recordatorioRepository).findRecordatoriosPendientes(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Listar por rango de fechas - Exitoso")
    void testListarPorRangoFechasExitoso() {
        // Given
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fin = LocalDateTime.now().plusDays(7);
        List<Recordatorio> recordatorios = Arrays.asList(recordatorio);
        when(recordatorioRepository.findByFechaRecordatorioBetweenOrderByFechaRecordatorioAsc(inicio, fin))
                .thenReturn(recordatorios);
        when(recordatorioMapper.toDTO(any(Recordatorio.class))).thenReturn(recordatorioDTO);

        // When
        List<RecordatorioDTO> resultado = recordatorioService.listarPorRangoFechas(inicio, fin);

        // Then
        assertThat(resultado).hasSize(1);
        verify(recordatorioRepository).findByFechaRecordatorioBetweenOrderByFechaRecordatorioAsc(inicio, fin);
    }

    @Test
    @DisplayName("Marcar como enviado - Exitoso")
    void testMarcarComoEnviadoExitoso() {
        // Given
        when(recordatorioRepository.findById(1L)).thenReturn(Optional.of(recordatorio));
        when(recordatorioRepository.save(any(Recordatorio.class))).thenReturn(recordatorio);
        when(recordatorioMapper.toDTO(any(Recordatorio.class))).thenReturn(recordatorioDTO);

        // When
        RecordatorioDTO resultado = recordatorioService.marcarComoEnviado(1L);

        // Then
        assertThat(resultado).isNotNull();
        verify(recordatorioRepository).save(any(Recordatorio.class));
    }

    @Test
    @DisplayName("Marcar como enviado - Recordatorio no existe")
    void testMarcarComoEnviadoNoExiste() {
        // Given
        when(recordatorioRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> recordatorioService.marcarComoEnviado(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Recordatorio");
    }

    @Test
    @DisplayName("Obtener por ID - Exitoso")
    void testObtenerPorIdExitoso() {
        // Given
        when(recordatorioRepository.findById(1L)).thenReturn(Optional.of(recordatorio));
        when(recordatorioMapper.toDTO(recordatorio)).thenReturn(recordatorioDTO);

        // When
        RecordatorioDTO resultado = recordatorioService.obtenerPorId(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Obtener por ID - No existe")
    void testObtenerPorIdNoExiste() {
        // Given
        when(recordatorioRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> recordatorioService.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("Cancelar recordatorio - Exitoso")
    void testCancelarRecordatorioExitoso() {
        // Given
        when(recordatorioRepository.findById(1L)).thenReturn(Optional.of(recordatorio));

        // When
        recordatorioService.cancelarRecordatorio(1L);

        // Then
        verify(recordatorioRepository).save(any(Recordatorio.class));
    }
}
