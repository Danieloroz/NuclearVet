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
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para CitaService.
 * Valida RF3.1 a RF3.5
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de CitaService")
class CitaServiceTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CitaMapper citaMapper;

    @InjectMocks
    private CitaService citaService;

    private Paciente paciente;
    private Usuario veterinario;
    private Cita cita;
    private CrearCitaDTO crearCitaDTO;
    private CitaDTO citaDTO;

    @BeforeEach
    void setUp() {
        // Crear paciente de prueba
        paciente = Paciente.builder()
                .id(1L)
                .nombre("Firulais")
                .especie("Canino")
                .build();

        // Crear veterinario con rol
        Rol rolVeterinario = new Rol();
        rolVeterinario.setNombre("VETERINARIO");

        veterinario = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@nuclearvet.com")
                .roles(new HashSet<>(Collections.singletonList(rolVeterinario)))
                .build();

        // Crear cita de prueba
        cita = Cita.builder()
                .id(1L)
                .paciente(paciente)
                .veterinario(veterinario)
                .fechaHora(LocalDateTime.now().plusDays(1))
                .tipoServicio("CONSULTA")
                .estado("PROGRAMADA")
                .motivo("Control general")
                .duracionMinutos(30)
                .build();

        // Crear DTO de prueba
        crearCitaDTO = CrearCitaDTO.builder()
                .pacienteId(1L)
                .veterinarioId(1L)
                .fechaHora(LocalDateTime.now().plusDays(1))
                .tipoServicio("CONSULTA")
                .motivo("Control general")
                .duracionMinutos(30)
                .build();

        citaDTO = CitaDTO.builder()
                .id(1L)
                .pacienteNombre("Firulais")
                .veterinarioNombre("Juan Pérez")
                .fechaHora(cita.getFechaHora())
                .tipoServicio("CONSULTA")
                .estado("PROGRAMADA")
                .build();
    }

    @Test
    @DisplayName("RF3.1: Debe crear una cita exitosamente")
    void debeCrearCitaExitosamente() {
        // Given
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
        when(citaRepository.buscarCitasEnRango(any(), any(), any())).thenReturn(Collections.emptyList());
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);
        when(citaMapper.toDTO(any(Cita.class))).thenReturn(citaDTO);

        // When
        CitaDTO resultado = citaService.crearCita(crearCitaDTO);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstado()).isEqualTo("PROGRAMADA");
        verify(citaRepository).save(any(Cita.class));
        verify(citaMapper).toDTO(any(Cita.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el paciente no existe")
    void debeLanzarExcepcionCuandoPacienteNoExiste() {
        // Given
        when(pacienteRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> citaService.crearCita(crearCitaDTO))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(citaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el veterinario no existe")
    void debeLanzarExcepcionCuandoVeterinarioNoExiste() {
        // Given
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> citaService.crearCita(crearCitaDTO))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(citaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario no es veterinario")
    void debeLanzarExcepcionCuandoUsuarioNoEsVeterinario() {
        // Given
        Usuario usuarioSinRol = Usuario.builder()
                .id(1L)
                .nombre("Pedro")
                .roles(new HashSet<>())
                .build();

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioSinRol));

        // When & Then
        assertThatThrownBy(() -> citaService.crearCita(crearCitaDTO))
                .isInstanceOf(ValidacionException.class)
                .hasMessageContaining("no es veterinario");

        verify(citaRepository, never()).save(any());
    }

    @Test
    @DisplayName("RF3.5: Debe lanzar excepción cuando el veterinario no está disponible")
    void debeLanzarExcepcionCuandoVeterinarioNoDisponible() {
        // Given
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
        when(citaRepository.buscarCitasEnRango(any(), any(), any())).thenReturn(Collections.singletonList(cita));

        // When & Then
        assertThatThrownBy(() -> citaService.crearCita(crearCitaDTO))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("ya tiene una cita");

        verify(citaRepository, never()).save(any());
    }

    @Test
    @DisplayName("RF3.2: Debe actualizar una cita exitosamente")
    void debeActualizarCitaExitosamente() {
        // Given
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(citaRepository.buscarCitasEnRango(any(), any(), any())).thenReturn(Collections.emptyList());
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);
        when(citaMapper.toDTO(any(Cita.class))).thenReturn(citaDTO);

        crearCitaDTO.setMotivo("Nuevo motivo");

        // When
        CitaDTO resultado = citaService.actualizarCita(1L, crearCitaDTO);

        // Then
        assertThat(resultado).isNotNull();
        verify(citaRepository).save(any(Cita.class));
    }

    @Test
    @DisplayName("No debe permitir actualizar una cita completada")
    void noDebePermitirActualizarCitaCompletada() {
        // Given
        cita.setEstado("COMPLETADA");
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));

        // When & Then
        assertThatThrownBy(() -> citaService.actualizarCita(1L, crearCitaDTO))
                .isInstanceOf(ValidacionException.class)
                .hasMessageContaining("completada");

        verify(citaRepository, never()).save(any());
    }

    @Test
    @DisplayName("No debe permitir actualizar una cita cancelada")
    void noDebePermitirActualizarCitaCancelada() {
        // Given
        cita.setEstado("CANCELADA");
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));

        // When & Then
        assertThatThrownBy(() -> citaService.actualizarCita(1L, crearCitaDTO))
                .isInstanceOf(ValidacionException.class)
                .hasMessageContaining("cancelada");

        verify(citaRepository, never()).save(any());
    }

    @Test
    @DisplayName("RF3.3: Debe cancelar una cita exitosamente")
    void debeCancelarCitaExitosamente() {
        // Given
        CancelarCitaDTO cancelarDTO = CancelarCitaDTO.builder()
                .motivoCancelacion("El paciente no puede asistir")
                .build();

        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);
        when(citaMapper.toDTO(any(Cita.class))).thenReturn(citaDTO);

        // When
        CitaDTO resultado = citaService.cancelarCita(1L, cancelarDTO);

        // Then
        assertThat(resultado).isNotNull();
        verify(citaRepository).save(argThat(c -> 
            "CANCELADA".equals(c.getEstado()) && 
            c.getMotivoCancelacion() != null
        ));
    }

    @Test
    @DisplayName("No debe cancelar una cita ya cancelada")
    void noDebeCancelarCitaYaCancelada() {
        // Given
        cita.setEstado("CANCELADA");
        CancelarCitaDTO cancelarDTO = new CancelarCitaDTO("Motivo");

        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));

        // When & Then
        assertThatThrownBy(() -> citaService.cancelarCita(1L, cancelarDTO))
                .isInstanceOf(ValidacionException.class)
                .hasMessageContaining("ya está cancelada");
    }

    @Test
    @DisplayName("RF3.4: Debe consultar agenda de un veterinario")
    void debeConsultarAgendaVeterinario() {
        // Given
        LocalDate fecha = LocalDate.now().plusDays(1);
        List<Cita> citas = Arrays.asList(cita);

        when(citaRepository.findByVeterinarioIdAndFechaHoraBetweenOrderByFechaHoraAsc(any(), any(), any()))
                .thenReturn(citas);
        when(citaMapper.toDTO(any(Cita.class))).thenReturn(citaDTO);

        // When
        List<CitaDTO> resultado = citaService.consultarAgenda(1L, fecha);

        // Then
        assertThat(resultado).hasSize(1);
        verify(citaRepository).findByVeterinarioIdAndFechaHoraBetweenOrderByFechaHoraAsc(any(), any(), any());
    }

    @Test
    @DisplayName("RF3.5: Debe verificar disponibilidad correctamente")
    void debeVerificarDisponibilidad() {
        // Given
        LocalDateTime fechaHora = LocalDateTime.now().plusDays(1);
        when(citaRepository.buscarCitasEnRango(any(), any(), any())).thenReturn(Collections.emptyList());

        // When
        boolean disponible = citaService.verificarDisponibilidad(1L, fechaHora, 30);

        // Then
        assertThat(disponible).isTrue();
    }

    @Test
    @DisplayName("Debe retornar false cuando no hay disponibilidad")
    void debeRetornarFalseCuandoNoHayDisponibilidad() {
        // Given
        LocalDateTime fechaHora = LocalDateTime.now().plusDays(1);
        when(citaRepository.buscarCitasEnRango(any(), any(), any())).thenReturn(Collections.singletonList(cita));

        // When
        boolean disponible = citaService.verificarDisponibilidad(1L, fechaHora, 30);

        // Then
        assertThat(disponible).isFalse();
    }

    @Test
    @DisplayName("Debe confirmar una cita programada")
    void debeConfirmarCitaProgramada() {
        // Given
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);
        when(citaMapper.toDTO(any(Cita.class))).thenReturn(citaDTO);

        // When
        CitaDTO resultado = citaService.confirmarCita(1L);

        // Then
        assertThat(resultado).isNotNull();
        verify(citaRepository).save(argThat(c -> "CONFIRMADA".equals(c.getEstado())));
    }

    @Test
    @DisplayName("Debe iniciar una cita confirmada")
    void debeIniciarCitaConfirmada() {
        // Given
        cita.setEstado("CONFIRMADA");
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);
        when(citaMapper.toDTO(any(Cita.class))).thenReturn(citaDTO);

        // When
        CitaDTO resultado = citaService.iniciarCita(1L);

        // Then
        assertThat(resultado).isNotNull();
        verify(citaRepository).save(argThat(c -> "EN_CURSO".equals(c.getEstado())));
    }

    @Test
    @DisplayName("Debe completar una cita en curso")
    void debeCompletarCitaEnCurso() {
        // Given
        cita.setEstado("EN_CURSO");
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);
        when(citaMapper.toDTO(any(Cita.class))).thenReturn(citaDTO);

        // When
        CitaDTO resultado = citaService.completarCita(1L);

        // Then
        assertThat(resultado).isNotNull();
        verify(citaRepository).save(argThat(c -> "COMPLETADA".equals(c.getEstado())));
    }

    @Test
    @DisplayName("Debe marcar una cita como no asistió")
    void debeMarcarCitaComoNoAsistio() {
        // Given
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);
        when(citaMapper.toDTO(any(Cita.class))).thenReturn(citaDTO);

        // When
        CitaDTO resultado = citaService.marcarNoAsistio(1L);

        // Then
        assertThat(resultado).isNotNull();
        verify(citaRepository).save(argThat(c -> "NO_ASISTIO".equals(c.getEstado())));
    }

    @Test
    @DisplayName("Debe obtener cita por ID")
    void debeObtenerCitaPorId() {
        // Given
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(citaMapper.toDTO(any(Cita.class))).thenReturn(citaDTO);

        // When
        CitaDTO resultado = citaService.obtenerPorId(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la cita no existe")
    void debeLanzarExcepcionCuandoCitaNoExiste() {
        // Given
        when(citaRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> citaService.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("Debe obtener todas las citas del día")
    void debeObtenerCitasDelDia() {
        // Given
        when(citaRepository.buscarTodasCitasDelDia()).thenReturn(Collections.singletonList(cita));
        when(citaMapper.toDTO(any(Cita.class))).thenReturn(citaDTO);

        // When
        List<CitaDTO> resultado = citaService.citasDelDia();

        // Then
        assertThat(resultado).hasSize(1);
        verify(citaRepository).buscarTodasCitasDelDia();
    }
}
