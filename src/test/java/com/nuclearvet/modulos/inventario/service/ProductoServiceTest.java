package com.nuclearvet.modulos.inventario.service;

import com.nuclearvet.common.exception.ConflictoException;
import com.nuclearvet.common.exception.RecursoNoEncontradoException;
import com.nuclearvet.modulos.inventario.dto.CrearProductoDTO;
import com.nuclearvet.modulos.inventario.dto.ProductoDTO;
import com.nuclearvet.modulos.inventario.entity.Categoria;
import com.nuclearvet.modulos.inventario.entity.Producto;
import com.nuclearvet.modulos.inventario.entity.Proveedor;
import com.nuclearvet.modulos.inventario.mapper.ProductoMapper;
import com.nuclearvet.modulos.inventario.repository.CategoriaRepository;
import com.nuclearvet.modulos.inventario.repository.ProductoRepository;
import com.nuclearvet.modulos.inventario.repository.ProveedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests para ProductoService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductoService Tests")
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProveedorRepository proveedorRepository;

    @Mock
    private ProductoMapper productoMapper;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;
    private CrearProductoDTO crearProductoDTO;
    private ProductoDTO productoDTO;
    private Categoria categoria;
    private Proveedor proveedor;

    @BeforeEach
    void setUp() {
        categoria = Categoria.builder()
                .id(1L)
                .nombre("Medicamentos")
                .build();
        categoria.setActivo(true);

        proveedor = Proveedor.builder()
                .id(1L)
                .nombre("Proveedor Test")
                .nit("123456789")
                .build();
        proveedor.setActivo(true);

        producto = Producto.builder()
                .id(1L)
                .codigo("MED001")
                .nombre("Antibiótico X")
                .descripcion("Antibiótico para perros")
                .precioCompra(BigDecimal.valueOf(10000))
                .precioVenta(BigDecimal.valueOf(15000))
                .stockActual(50)
                .stockMinimo(10)
                .stockMaximo(100)
                .categoria(categoria)
                .proveedor(proveedor)
                .build();
        producto.setActivo(true);

        crearProductoDTO = CrearProductoDTO.builder()
                .codigo("MED001")
                .nombre("Antibiótico X")
                .descripcion("Antibiótico para perros")
                .precioCompra(BigDecimal.valueOf(10000))
                .precioVenta(BigDecimal.valueOf(15000))
                .stockActual(50)
                .stockMinimo(10)
                .stockMaximo(100)
                .categoriaId(1L)
                .proveedorId(1L)
                .build();

        productoDTO = ProductoDTO.builder()
                .id(1L)
                .codigo("MED001")
                .nombre("Antibiótico X")
                .precioVenta(BigDecimal.valueOf(15000))
                .stockActual(50)
                .build();
    }

    @Test
    @DisplayName("Crear producto exitosamente")
    void crearProducto_Exitoso() {
        // Given
        when(productoRepository.findByCodigo(anyString())).thenReturn(Optional.empty());
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.of(categoria));
        when(proveedorRepository.findById(anyLong())).thenReturn(Optional.of(proveedor));
        when(productoMapper.toEntity(any(CrearProductoDTO.class))).thenReturn(producto);
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(productoMapper.toDTO(any(Producto.class))).thenReturn(productoDTO);

        // When
        ProductoDTO resultado = productoService.crearProducto(crearProductoDTO);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getCodigo()).isEqualTo("MED001");
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    @DisplayName("Crear producto con código duplicado debe lanzar excepción")
    void crearProducto_CodigoDuplicado_LanzaExcepcion() {
        // Given
        when(productoRepository.findByCodigo(anyString())).thenReturn(Optional.of(producto));

        // When & Then
        assertThatThrownBy(() -> productoService.crearProducto(crearProductoDTO))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("Ya existe un producto con ese código");
    }

    @Test
    @DisplayName("Crear producto con precio venta menor a compra debe lanzar excepción")
    void crearProducto_PrecioVentaMenor_LanzaExcepcion() {
        // Given
        crearProductoDTO.setPrecioVenta(BigDecimal.valueOf(5000)); // Menor que precio compra
        when(productoRepository.findByCodigo(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productoService.crearProducto(crearProductoDTO))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("El precio de venta debe ser mayor al precio de compra");
    }

    @Test
    @DisplayName("Crear producto con stock mayor al máximo debe lanzar excepción")
    void crearProducto_StockExcedido_LanzaExcepcion() {
        // Given
        crearProductoDTO.setStockActual(150); // Mayor que stockMaximo (100)
        when(productoRepository.findByCodigo(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productoService.crearProducto(crearProductoDTO))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("El stock actual no puede exceder el stock máximo");
    }

    @Test
    @DisplayName("Crear producto con categoría inexistente debe lanzar excepción")
    void crearProducto_CategoriaNoExiste_LanzaExcepcion() {
        // Given
        when(productoRepository.findByCodigo(anyString())).thenReturn(Optional.empty());
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productoService.crearProducto(crearProductoDTO))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Categoria");
    }

    @Test
    @DisplayName("Actualizar producto exitosamente")
    void actualizarProducto_Exitoso() {
        // Given
        when(productoRepository.findById(anyLong())).thenReturn(Optional.of(producto));
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.of(categoria));
        when(proveedorRepository.findById(anyLong())).thenReturn(Optional.of(proveedor));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(productoMapper.toDTO(any(Producto.class))).thenReturn(productoDTO);

        // When
        ProductoDTO resultado = productoService.actualizarProducto(1L, crearProductoDTO);

        // Then
        assertThat(resultado).isNotNull();
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    @DisplayName("Obtener producto por ID exitosamente")
    void obtenerPorId_Exitoso() {
        // Given
        when(productoRepository.findById(anyLong())).thenReturn(Optional.of(producto));
        when(productoMapper.toDTO(any(Producto.class))).thenReturn(productoDTO);

        // When
        ProductoDTO resultado = productoService.obtenerPorId(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Obtener producto inexistente debe lanzar excepción")
    void obtenerPorId_NoExiste_LanzaExcepcion() {
        // Given
        when(productoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productoService.obtenerPorId(999L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Producto");
    }

    @Test
    @DisplayName("Obtener producto por código exitosamente")
    void obtenerPorCodigo_Exitoso() {
        // Given
        when(productoRepository.findByCodigo(anyString())).thenReturn(Optional.of(producto));
        when(productoMapper.toDTO(any(Producto.class))).thenReturn(productoDTO);

        // When
        ProductoDTO resultado = productoService.obtenerPorCodigo("MED001");

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getCodigo()).isEqualTo("MED001");
    }

    @Test
    @DisplayName("Listar todos los productos activos")
    void listarTodos_Exitoso() {
        // Given
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findByActivoTrueOrderByNombreAsc()).thenReturn(productos);
        when(productoMapper.toDTO(any(Producto.class))).thenReturn(productoDTO);

        // When
        List<ProductoDTO> resultado = productoService.listarTodos();

        // Then
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Obtener productos con bajo stock (RF4.3)")
    void obtenerProductosConBajoStock_Exitoso() {
        // Given
        producto.setStockActual(5); // Menor que stockMinimo (10)
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findProductosConBajoStock()).thenReturn(productos);
        when(productoMapper.toDTO(any(Producto.class))).thenReturn(productoDTO);

        // When
        List<ProductoDTO> resultado = productoService.obtenerProductosConBajoStock();

        // Then
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Obtener productos próximos a vencer (RF4.3)")
    void obtenerProductosProximosAVencer_Exitoso() {
        // Given
        producto.setFechaVencimiento(LocalDate.now().plusDays(15)); // Vence en 15 días
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findProductosProximosAVencer(any(LocalDate.class), any(LocalDate.class))).thenReturn(productos);
        when(productoMapper.toDTO(any(Producto.class))).thenReturn(productoDTO);

        // When
        List<ProductoDTO> resultado = productoService.obtenerProductosProximosAVencer();

        // Then
        assertThat(resultado).isNotEmpty();
    }

    @Test
    @DisplayName("Obtener productos vencidos (RF4.3)")
    void obtenerProductosVencidos_Exitoso() {
        // Given
        producto.setFechaVencimiento(LocalDate.now().minusDays(5)); // Vencido hace 5 días
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findProductosVencidos()).thenReturn(productos);
        when(productoMapper.toDTO(any(Producto.class))).thenReturn(productoDTO);

        // When
        List<ProductoDTO> resultado = productoService.obtenerProductosVencidos();

        // Then
        assertThat(resultado).isNotEmpty();
    }

    @Test
    @DisplayName("Buscar productos por nombre")
    void buscarPorNombre_Exitoso() {
        // Given
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(anyString())).thenReturn(productos);
        when(productoMapper.toDTO(any(Producto.class))).thenReturn(productoDTO);

        // When
        List<ProductoDTO> resultado = productoService.buscarPorNombre("Anti");

        // Then
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Desactivar producto exitosamente")
    void desactivarProducto_Exitoso() {
        // Given
        when(productoRepository.findById(anyLong())).thenReturn(Optional.of(producto));

        // When
        productoService.desactivarProducto(1L);

        // Then
        verify(productoRepository).save(argThat(p -> !p.getActivo()));
    }

    @Test
    @DisplayName("Ajustar stock exitosamente")
    void ajustarStock_Exitoso() {
        // Given
        when(productoRepository.findById(anyLong())).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(productoMapper.toDTO(any(Producto.class))).thenReturn(productoDTO);

        // When
        ProductoDTO resultado = productoService.ajustarStock(1L, 75);

        // Then
        assertThat(resultado).isNotNull();
        verify(productoRepository).save(argThat(p -> p.getStockActual() == 75));
    }

    @Test
    @DisplayName("Ajustar stock con cantidad negativa debe lanzar excepción")
    void ajustarStock_CantidadNegativa_LanzaExcepcion() {
        // Given
        when(productoRepository.findById(anyLong())).thenReturn(Optional.of(producto));

        // When & Then
        assertThatThrownBy(() -> productoService.ajustarStock(1L, -10))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("La cantidad de stock no puede ser negativa");
    }

    @Test
    @DisplayName("Listar productos por categoría")
    void listarPorCategoria_Exitoso() {
        // Given
        List<Producto> productos = Arrays.asList(producto);
        when(categoriaRepository.existsById(anyLong())).thenReturn(true);
        when(productoRepository.findByCategoriaIdAndActivoTrue(anyLong())).thenReturn(productos);
        when(productoMapper.toDTO(any(Producto.class))).thenReturn(productoDTO);

        // When
        List<ProductoDTO> resultado = productoService.listarPorCategoria(1L);

        // Then
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Listar productos por proveedor")
    void listarPorProveedor_Exitoso() {
        // Given
        List<Producto> productos = Arrays.asList(producto);
        when(proveedorRepository.existsById(anyLong())).thenReturn(true);
        when(productoRepository.findByProveedorIdAndActivoTrue(anyLong())).thenReturn(productos);
        when(productoMapper.toDTO(any(Producto.class))).thenReturn(productoDTO);

        // When
        List<ProductoDTO> resultado = productoService.listarPorProveedor(1L);

        // Then
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(1);
    }
}
