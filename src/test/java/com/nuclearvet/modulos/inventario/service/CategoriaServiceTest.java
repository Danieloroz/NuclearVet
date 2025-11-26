package com.nuclearvet.modulos.inventario.service;

import com.nuclearvet.common.exception.ConflictoException;
import com.nuclearvet.common.exception.RecursoNoEncontradoException;
import com.nuclearvet.modulos.inventario.dto.CategoriaDTO;
import com.nuclearvet.modulos.inventario.dto.CrearCategoriaDTO;
import com.nuclearvet.modulos.inventario.entity.Categoria;
import com.nuclearvet.modulos.inventario.entity.TipoCategoria;
import com.nuclearvet.modulos.inventario.mapper.CategoriaMapper;
import com.nuclearvet.modulos.inventario.repository.CategoriaRepository;
import com.nuclearvet.modulos.inventario.repository.ProductoRepository;
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
 * Tests para CategoriaService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoriaService Tests")
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaMapper categoriaMapper;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;
    private CrearCategoriaDTO crearCategoriaDTO;
    private CategoriaDTO categoriaDTO;

    @BeforeEach
    void setUp() {
        categoria = Categoria.builder()
                .id(1L)
                .nombre("Medicamentos")
                .descripcion("Productos medicinales")
                .tipoCategoria(TipoCategoria.MEDICAMENTO)
                .build();
        categoria.setActivo(true);

        crearCategoriaDTO = CrearCategoriaDTO.builder()
                .nombre("Medicamentos")
                .descripcion("Productos medicinales")
                .tipoCategoria("MEDICAMENTO")
                .build();

        categoriaDTO = CategoriaDTO.builder()
                .id(1L)
                .nombre("Medicamentos")
                .descripcion("Productos medicinales")
                .tipoCategoria("MEDICAMENTO")
                .activo(true)
                .totalProductos(5L)
                .build();
    }

    @Test
    @DisplayName("Crear categoría exitosamente")
    void crearCategoria_Exitoso() {
        // Given
        when(categoriaRepository.findByNombre(anyString())).thenReturn(Optional.empty());
        when(categoriaMapper.toEntity(any(CrearCategoriaDTO.class))).thenReturn(categoria);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);
        when(categoriaMapper.toDTO(any(Categoria.class))).thenReturn(categoriaDTO);
        when(productoRepository.countByCategoriaIdAndActivoTrue(anyLong())).thenReturn(5L);

        // When
        CategoriaDTO resultado = categoriaService.crearCategoria(crearCategoriaDTO);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Medicamentos");
        assertThat(resultado.getTotalProductos()).isEqualTo(5L);
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Crear categoría con nombre duplicado debe lanzar excepción")
    void crearCategoria_NombreDuplicado_LanzaExcepcion() {
        // Given
        when(categoriaRepository.findByNombre(anyString())).thenReturn(Optional.of(categoria));

        // When & Then
        assertThatThrownBy(() -> categoriaService.crearCategoria(crearCategoriaDTO))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("Ya existe una categoría con ese nombre");
    }

    @Test
    @DisplayName("Actualizar categoría exitosamente")
    void actualizarCategoria_Exitoso() {
        // Given
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);
        when(categoriaMapper.toDTO(any(Categoria.class))).thenReturn(categoriaDTO);
        when(productoRepository.countByCategoriaIdAndActivoTrue(anyLong())).thenReturn(5L);

        // When
        CategoriaDTO resultado = categoriaService.actualizarCategoria(1L, crearCategoriaDTO);

        // Then
        assertThat(resultado).isNotNull();
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Obtener categoría por ID exitosamente")
    void obtenerPorId_Exitoso() {
        // Given
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.of(categoria));
        when(categoriaMapper.toDTO(any(Categoria.class))).thenReturn(categoriaDTO);
        when(productoRepository.countByCategoriaIdAndActivoTrue(anyLong())).thenReturn(5L);

        // When
        CategoriaDTO resultado = categoriaService.obtenerPorId(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getTotalProductos()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Obtener categoría inexistente debe lanzar excepción")
    void obtenerPorId_NoExiste_LanzaExcepcion() {
        // Given
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoriaService.obtenerPorId(999L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Categoria");
    }

    @Test
    @DisplayName("Listar todas las categorías")
    void listarTodas_Exitoso() {
        // Given
        List<Categoria> categorias = Arrays.asList(categoria);
        when(categoriaRepository.findByActivoTrueOrderByNombreAsc()).thenReturn(categorias);
        when(categoriaMapper.toDTO(any(Categoria.class))).thenReturn(categoriaDTO);
        when(productoRepository.countByCategoriaIdAndActivoTrue(anyLong())).thenReturn(5L);

        // When
        List<CategoriaDTO> resultado = categoriaService.listarTodas();

        // Then
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Listar categorías por tipo")
    void listarPorTipo_Exitoso() {
        // Given
        List<Categoria> categorias = Arrays.asList(categoria);
        when(categoriaRepository.findByTipoCategoriaAndActivoTrue(any(TipoCategoria.class))).thenReturn(categorias);
        when(categoriaMapper.toDTO(any(Categoria.class))).thenReturn(categoriaDTO);
        when(productoRepository.countByCategoriaIdAndActivoTrue(anyLong())).thenReturn(5L);

        // When
        List<CategoriaDTO> resultado = categoriaService.listarPorTipo(TipoCategoria.MEDICAMENTO);

        // Then
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Desactivar categoría sin productos exitosamente")
    void desactivarCategoria_SinProductos_Exitoso() {
        // Given
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.of(categoria));
        when(productoRepository.countByCategoriaIdAndActivoTrue(anyLong())).thenReturn(0L);

        // When
        categoriaService.desactivarCategoria(1L);

        // Then
        verify(categoriaRepository).save(argThat(c -> !c.getActivo()));
    }

    @Test
    @DisplayName("Desactivar categoría con productos activos debe lanzar excepción")
    void desactivarCategoria_ConProductos_LanzaExcepcion() {
        // Given
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.of(categoria));
        when(productoRepository.countByCategoriaIdAndActivoTrue(anyLong())).thenReturn(5L);

        // When & Then
        assertThatThrownBy(() -> categoriaService.desactivarCategoria(1L))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("tiene 5 productos activos");
    }
}
