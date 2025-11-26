package com.nuclearvet.modulos.inventario.service;

import com.nuclearvet.common.exception.ConflictoException;
import com.nuclearvet.common.exception.RecursoNoEncontradoException;
import com.nuclearvet.modulos.inventario.dto.CrearProveedorDTO;
import com.nuclearvet.modulos.inventario.dto.ProveedorDTO;
import com.nuclearvet.modulos.inventario.entity.Proveedor;
import com.nuclearvet.modulos.inventario.mapper.ProveedorMapper;
import com.nuclearvet.modulos.inventario.repository.ProveedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests para ProveedorService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProveedorService Tests")
class ProveedorServiceTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    @Mock
    private ProveedorMapper proveedorMapper;

    @InjectMocks
    private ProveedorService proveedorService;

    private Proveedor proveedor;
    private CrearProveedorDTO crearProveedorDTO;
    private ProveedorDTO proveedorDTO;

    @BeforeEach
    void setUp() {
        proveedor = Proveedor.builder()
                .id(1L)
                .nombre("Proveedor Test")
                .nit("123456789")
                .nombreContacto("Juan Perez")
                .email("contacto@test.com")
                .direccion("Calle 123")
                .telefono("3001234567")
                .diasCredito(30)
                .calificacion(4)
                .build();
        proveedor.setActivo(true);

        crearProveedorDTO = CrearProveedorDTO.builder()
                .nombre("Proveedor Test")
                .nit("123456789")
                .nombreContacto("Juan Perez")
                .email("contacto@test.com")
                .direccion("Calle 123")
                .telefono("3001234567")
                .diasCredito(30)
                .calificacion(4)
                .build();

        proveedorDTO = ProveedorDTO.builder()
                .id(1L)
                .nombre("Proveedor Test")
                .nit("123456789")
                .calificacion(4)
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("Crear proveedor exitosamente")
    void crearProveedor_Exitoso() {
        // Given
        when(proveedorRepository.findByNit(anyString())).thenReturn(Optional.empty());
        when(proveedorMapper.toEntity(any(CrearProveedorDTO.class))).thenReturn(proveedor);
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(proveedor);
        when(proveedorMapper.toDTO(any(Proveedor.class))).thenReturn(proveedorDTO);

        // When
        ProveedorDTO resultado = proveedorService.crearProveedor(crearProveedorDTO);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNit()).isEqualTo("123456789");
        verify(proveedorRepository).save(any(Proveedor.class));
    }

    @Test
    @DisplayName("Crear proveedor con NIT duplicado debe lanzar excepción")
    void crearProveedor_NitDuplicado_LanzaExcepcion() {
        // Given
        when(proveedorRepository.findByNit(anyString())).thenReturn(Optional.of(proveedor));

        // When & Then
        assertThatThrownBy(() -> proveedorService.crearProveedor(crearProveedorDTO))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("Ya existe un proveedor con ese NIT");
    }

    @Test
    @DisplayName("Actualizar proveedor exitosamente")
    void actualizarProveedor_Exitoso() {
        // Given
        when(proveedorRepository.findById(anyLong())).thenReturn(Optional.of(proveedor));
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(proveedor);
        when(proveedorMapper.toDTO(any(Proveedor.class))).thenReturn(proveedorDTO);

        // When
        ProveedorDTO resultado = proveedorService.actualizarProveedor(1L, crearProveedorDTO);

        // Then
        assertThat(resultado).isNotNull();
        verify(proveedorRepository).save(any(Proveedor.class));
    }

    @Test
    @DisplayName("Actualizar NIT de proveedor con NIT duplicado debe lanzar excepción")
    void actualizarProveedor_NitDuplicado_LanzaExcepcion() {
        // Given
        Proveedor otroProveedor = Proveedor.builder()
                .id(2L)
                .nit("987654321")
                .build();
        
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));
        when(proveedorRepository.findByNit("987654321")).thenReturn(Optional.of(otroProveedor));

        CrearProveedorDTO dtoConNuevoNit = CrearProveedorDTO.builder()
                .nombre("Proveedor Test")
                .nit("987654321") // NIT de otro proveedor
                .build();

        // When & Then
        assertThatThrownBy(() -> proveedorService.actualizarProveedor(1L, dtoConNuevoNit))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("Ya existe otro proveedor con ese NIT");
    }

    @Test
    @DisplayName("Obtener proveedor por ID exitosamente")
    void obtenerPorId_Exitoso() {
        // Given
        when(proveedorRepository.findById(anyLong())).thenReturn(Optional.of(proveedor));
        when(proveedorMapper.toDTO(any(Proveedor.class))).thenReturn(proveedorDTO);

        // When
        ProveedorDTO resultado = proveedorService.obtenerPorId(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Obtener proveedor por NIT exitosamente")
    void obtenerPorNit_Exitoso() {
        // Given
        when(proveedorRepository.findByNit(anyString())).thenReturn(Optional.of(proveedor));
        when(proveedorMapper.toDTO(any(Proveedor.class))).thenReturn(proveedorDTO);

        // When
        ProveedorDTO resultado = proveedorService.obtenerPorNit("123456789");

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNit()).isEqualTo("123456789");
    }

    @Test
    @DisplayName("Listar todos los proveedores")
    void listarTodos_Exitoso() {
        // Given
        List<Proveedor> proveedores = Arrays.asList(proveedor);
        when(proveedorRepository.findByActivoTrueOrderByNombreAsc()).thenReturn(proveedores);
        when(proveedorMapper.toDTO(any(Proveedor.class))).thenReturn(proveedorDTO);

        // When
        List<ProveedorDTO> resultado = proveedorService.listarTodos();

        // Then
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Buscar proveedores por nombre")
    void buscarPorNombre_Exitoso() {
        // Given
        List<Proveedor> proveedores = Arrays.asList(proveedor);
        when(proveedorRepository.findByNombreContainingIgnoreCaseAndActivoTrue(anyString())).thenReturn(proveedores);
        when(proveedorMapper.toDTO(any(Proveedor.class))).thenReturn(proveedorDTO);

        // When
        List<ProveedorDTO> resultado = proveedorService.buscarPorNombre("Test");

        // Then
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Listar proveedores por calificación mínima")
    void listarPorCalificacion_Exitoso() {
        // Given
        List<Proveedor> proveedores = Arrays.asList(proveedor);
        when(proveedorRepository.findByCalificacionMayorIgualAndActivoTrue(anyInt())).thenReturn(proveedores);
        when(proveedorMapper.toDTO(any(Proveedor.class))).thenReturn(proveedorDTO);

        // When
        List<ProveedorDTO> resultado = proveedorService.listarPorCalificacion(4);

        // Then
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Desactivar proveedor exitosamente")
    void desactivarProveedor_Exitoso() {
        // Given
        when(proveedorRepository.findById(anyLong())).thenReturn(Optional.of(proveedor));

        // When
        proveedorService.desactivarProveedor(1L);

        // Then
        verify(proveedorRepository).save(argThat(p -> !p.getActivo()));
    }

    @Test
    @DisplayName("Actualizar calificación exitosamente")
    void actualizarCalificacion_Exitoso() {
        // Given
        when(proveedorRepository.findById(anyLong())).thenReturn(Optional.of(proveedor));
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(proveedor);
        when(proveedorMapper.toDTO(any(Proveedor.class))).thenReturn(proveedorDTO);

        // When
        ProveedorDTO resultado = proveedorService.actualizarCalificacion(1L, 5);

        // Then
        assertThat(resultado).isNotNull();
        verify(proveedorRepository).save(argThat(p -> p.getCalificacion() == 5));
    }

    @Test
    @DisplayName("Actualizar calificación inválida debe lanzar excepción")
    void actualizarCalificacion_Invalida_LanzaExcepcion() {
        // Given
        when(proveedorRepository.findById(anyLong())).thenReturn(Optional.of(proveedor));

        // When & Then - Calificación muy alta
        assertThatThrownBy(() -> proveedorService.actualizarCalificacion(1L, 6))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("debe estar entre 1 y 5");

        // When & Then - Calificación muy baja
        assertThatThrownBy(() -> proveedorService.actualizarCalificacion(1L, 0))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("debe estar entre 1 y 5");
    }
}
