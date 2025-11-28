package com.nuclearvet.modulos.notificaciones.service;

import com.nuclearvet.common.exception.RecursoNoEncontradoException;
import com.nuclearvet.modulos.notificaciones.dto.CrearNotificacionDTO;
import com.nuclearvet.modulos.notificaciones.dto.NotificacionDTO;
import com.nuclearvet.modulos.notificaciones.entity.CanalNotificacion;
import com.nuclearvet.modulos.notificaciones.entity.EstadoNotificacion;
import com.nuclearvet.modulos.notificaciones.entity.Notificacion;
import com.nuclearvet.modulos.notificaciones.mapper.NotificacionMapper;
import com.nuclearvet.modulos.notificaciones.repository.NotificacionRepository;
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
@DisplayName("Notificacion Service Tests")
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private NotificacionMapper notificacionMapper;

    @InjectMocks
    private NotificacionService notificacionService;

    private Usuario usuario;
    private Notificacion notificacion;
    private NotificacionDTO notificacionDTO;
    private CrearNotificacionDTO crearNotificacionDTO;

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

        // Notificacion
        notificacion = Notificacion.builder()
                .destinatario(usuario)
                .asunto("Recordatorio de cita")
                .mensaje("Tiene una cita programada para mañana")
                .canal(CanalNotificacion.EMAIL)
                .estado(EstadoNotificacion.PENDIENTE)
                .build();
        notificacion.setActivo(true);

        // NotificacionDTO
        notificacionDTO = NotificacionDTO.builder()
                .id(1L)
                .destinatarioId(1L)
                .destinatarioNombre("Juan Pérez")
                .asunto("Recordatorio de cita")
                .mensaje("Tiene una cita programada para mañana")
                .canal(CanalNotificacion.EMAIL)
                .estado(EstadoNotificacion.PENDIENTE)
                .build();

        // CrearNotificacionDTO
        crearNotificacionDTO = CrearNotificacionDTO.builder()
                .destinatarioId(1L)
                .asunto("Recordatorio de cita")
                .mensaje("Tiene una cita programada para mañana")
                .canal(CanalNotificacion.EMAIL)
                .build();
    }

    @Test
    @DisplayName("Crear notificación - Exitoso")
    void testCrearNotificacionExitoso() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(notificacionMapper.toEntity(any(CrearNotificacionDTO.class))).thenReturn(notificacion);
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacion);
        when(notificacionMapper.toDTO(any(Notificacion.class))).thenReturn(notificacionDTO);

        // When
        NotificacionDTO resultado = notificacionService.crearNotificacion(crearNotificacionDTO);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getAsunto()).isEqualTo("Recordatorio de cita");
        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    @DisplayName("Crear notificación - Usuario no existe")
    void testCrearNotificacionUsuarioNoExiste() {
        // Given
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> notificacionService.crearNotificacion(crearNotificacionDTO))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Usuario");
    }

    @Test
    @DisplayName("Marcar como leída - Exitoso")
    void testMarcarComoLeidaExitoso() {
        // Given
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacion);
        when(notificacionMapper.toDTO(any(Notificacion.class))).thenReturn(notificacionDTO);

        // When
        NotificacionDTO resultado = notificacionService.marcarComoLeida(1L);

        // Then
        assertThat(resultado).isNotNull();
        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    @DisplayName("Marcar como leída - Notificación no existe")
    void testMarcarComoLeidaNoExiste() {
        // Given
        when(notificacionRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> notificacionService.marcarComoLeida(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Notificacion");
    }

    @Test
    @DisplayName("Listar no leídas por destinatario - Exitoso")
    void testListarNoLeidasPorDestinatarioExitoso() {
        // Given
        List<Notificacion> notificaciones = Arrays.asList(notificacion);
        when(notificacionRepository.findByDestinatarioIdAndFechaLeidaIsNullOrderByFechaCreacionDesc(1L))
                .thenReturn(notificaciones);
        when(notificacionMapper.toDTO(any(Notificacion.class))).thenReturn(notificacionDTO);

        // When
        List<NotificacionDTO> resultado = notificacionService.listarNoLeidas(1L);

        // Then
        assertThat(resultado).hasSize(1);
        verify(notificacionRepository).findByDestinatarioIdAndFechaLeidaIsNullOrderByFechaCreacionDesc(1L);
    }

    @Test
    @DisplayName("Listar por destinatario - Exitoso")
    void testListarPorDestinatarioExitoso() {
        // Given
        List<Notificacion> notificaciones = Arrays.asList(notificacion);
        when(notificacionRepository.findByDestinatarioIdOrderByFechaCreacionDesc(1L))
                .thenReturn(notificaciones);
        when(notificacionMapper.toDTO(any(Notificacion.class))).thenReturn(notificacionDTO);

        // When
        List<NotificacionDTO> resultado = notificacionService.listarPorDestinatario(1L);

        // Then
        assertThat(resultado).hasSize(1);
        verify(notificacionRepository).findByDestinatarioIdOrderByFechaCreacionDesc(1L);
    }

    @Test
    @DisplayName("Listar por canal - Exitoso")
    void testListarPorCanalExitoso() {
        // Given
        List<Notificacion> notificaciones = Arrays.asList(notificacion);
        when(notificacionRepository.findByDestinatarioIdAndCanalOrderByFechaCreacionDesc(1L, CanalNotificacion.EMAIL))
                .thenReturn(notificaciones);
        when(notificacionMapper.toDTO(any(Notificacion.class))).thenReturn(notificacionDTO);

        // When
        List<NotificacionDTO> resultado = notificacionService.listarPorCanal(1L, CanalNotificacion.EMAIL);

        // Then
        assertThat(resultado).hasSize(1);
        verify(notificacionRepository).findByDestinatarioIdAndCanalOrderByFechaCreacionDesc(1L, CanalNotificacion.EMAIL);
    }

    @Test
    @DisplayName("Listar por estado - Exitoso")
    void testListarPorEstadoExitoso() {
        // Given
        List<Notificacion> notificaciones = Arrays.asList(notificacion);
        when(notificacionRepository.findByEstadoOrderByFechaCreacionDesc(EstadoNotificacion.PENDIENTE))
                .thenReturn(notificaciones);
        when(notificacionMapper.toDTO(any(Notificacion.class))).thenReturn(notificacionDTO);

        // When
        List<NotificacionDTO> resultado = notificacionService.listarPorEstado(EstadoNotificacion.PENDIENTE);

        // Then
        assertThat(resultado).hasSize(1);
        verify(notificacionRepository).findByEstadoOrderByFechaCreacionDesc(EstadoNotificacion.PENDIENTE);
    }

    @Test
    @DisplayName("Reintentar envío - Exitoso")
    void testReintentarEnvioExitoso() {
        // Given
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacion);
        when(notificacionMapper.toDTO(any(Notificacion.class))).thenReturn(notificacionDTO);

        // When
        NotificacionDTO resultado = notificacionService.reintentarEnvio(1L);

        // Then
        assertThat(resultado).isNotNull();
        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    @DisplayName("Obtener por ID - Exitoso")
    void testObtenerPorIdExitoso() {
        // Given
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));
        when(notificacionMapper.toDTO(notificacion)).thenReturn(notificacionDTO);

        // When
        NotificacionDTO resultado = notificacionService.obtenerPorId(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Obtener por ID - No existe")
    void testObtenerPorIdNoExiste() {
        // Given
        when(notificacionRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> notificacionService.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
