package com.nuclearvet.modulos.inventario.service;

import com.nuclearvet.common.exception.ConflictoException;
import com.nuclearvet.common.exception.RecursoNoEncontradoException;
import com.nuclearvet.modulos.inventario.dto.MovimientoInventarioDTO;
import com.nuclearvet.modulos.inventario.dto.RegistrarMovimientoDTO;
import com.nuclearvet.modulos.inventario.entity.MovimientoInventario;
import com.nuclearvet.modulos.inventario.entity.Producto;
import com.nuclearvet.modulos.inventario.entity.Proveedor;
import com.nuclearvet.modulos.inventario.entity.TipoMovimiento;
import com.nuclearvet.modulos.inventario.mapper.MovimientoInventarioMapper;
import com.nuclearvet.modulos.inventario.repository.MovimientoInventarioRepository;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests para MovimientoInventarioService (RF4.4)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MovimientoInventarioService Tests")
class MovimientoInventarioServiceTest {

    @Mock
    private MovimientoInventarioRepository movimientoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ProveedorRepository proveedorRepository;

    @Mock
    private MovimientoInventarioMapper movimientoMapper;

    @InjectMocks
    private MovimientoInventarioService movimientoService;

    private Producto producto;
    private Proveedor proveedor;
    private MovimientoInventario movimiento;
    private RegistrarMovimientoDTO registrarMovimientoDTO;
    private MovimientoInventarioDTO movimientoDTO;

    @BeforeEach
    void setUp() {
        producto = Producto.builder()
                .id(1L)
                .nombre("Producto Test")
                .stockActual(50)
                .stockMinimo(10)
                .stockMaximo(100)
                .build();

        proveedor = Proveedor.builder()
                .id(1L)
                .nombre("Proveedor Test")
                .build();

        movimiento = MovimientoInventario.builder()
                .id(1L)
                .producto(producto)
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .cantidad(20)
                .stockAnterior(50)
                .stockNuevo(70)
                .fechaMovimiento(LocalDateTime.now())
                .build();

        registrarMovimientoDTO = RegistrarMovimientoDTO.builder()
                .productoId(1L)
                .tipoMovimiento("ENTRADA")
                .cantidad(20)
                .motivo("COMPRA")
                .build();

        movimientoDTO = MovimientoInventarioDTO.builder()
                .id(1L)
                .productoId(1L)
                .productoNombre("Producto Test")
                .tipoMovimiento("ENTRADA")
                .cantidad(20)
                .stockAnterior(50)
                .stockNuevo(70)
                .build();
    }

    @Test
    @DisplayName("RF4.4: Registrar ENTRADA de inventario exitosamente")
    void registrarMovimiento_Entrada_Exitoso() {
        // Given
        when(productoRepository.findById(anyLong())).thenReturn(Optional.of(producto));
        when(movimientoMapper.toEntity(any(RegistrarMovimientoDTO.class))).thenReturn(movimiento);
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenReturn(movimiento);
        when(movimientoMapper.toDTO(any(MovimientoInventario.class))).thenReturn(movimientoDTO);

        // When
        MovimientoInventarioDTO resultado = movimientoService.registrarMovimiento(registrarMovimientoDTO);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getTipoMovimiento()).isEqualTo("ENTRADA");
        verify(productoRepository).save(argThat(p -> p.getStockActual() == 70));
        verify(movimientoRepository).save(any(MovimientoInventario.class));
    }

    @Test
    @DisplayName("RF4.4: Registrar SALIDA de inventario exitosamente")
    void registrarMovimiento_Salida_Exitoso() {
        // Given
        registrarMovimientoDTO.setTipoMovimiento("SALIDA");
        movimiento.setTipoMovimiento(TipoMovimiento.SALIDA);
        movimiento.setStockNuevo(30); // 50 - 20 = 30
        
        when(productoRepository.findById(anyLong())).thenReturn(Optional.of(producto));
        when(movimientoMapper.toEntity(any(RegistrarMovimientoDTO.class))).thenReturn(movimiento);
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenReturn(movimiento);
        when(movimientoMapper.toDTO(any(MovimientoInventario.class))).thenReturn(movimientoDTO);

        // When
        MovimientoInventarioDTO resultado = movimientoService.registrarMovimiento(registrarMovimientoDTO);

        // Then
        assertThat(resultado).isNotNull();
        verify(productoRepository).save(argThat(p -> p.getStockActual() == 30));
    }

    @Test
    @DisplayName("RF4.4: Registrar SALIDA con stock insuficiente debe lanzar excepción")
    void registrarMovimiento_SalidaStockInsuficiente_LanzaExcepcion() {
        // Given
        registrarMovimientoDTO.setTipoMovimiento("SALIDA");
        registrarMovimientoDTO.setCantidad(100); // Más que el stock actual (50)
        
        when(productoRepository.findById(anyLong())).thenReturn(Optional.of(producto));

        // When & Then
        assertThatThrownBy(() -> movimientoService.registrarMovimiento(registrarMovimientoDTO))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("Stock insuficiente");
    }

    @Test
    @DisplayName("RF4.4: Registrar ENTRADA que excede stock máximo debe lanzar excepción")
    void registrarMovimiento_EntradaExcedeMaximo_LanzaExcepcion() {
        // Given
        registrarMovimientoDTO.setCantidad(60); // 50 + 60 = 110 > stockMaximo (100)
        
        when(productoRepository.findById(anyLong())).thenReturn(Optional.of(producto));

        // When & Then
        assertThatThrownBy(() -> movimientoService.registrarMovimiento(registrarMovimientoDTO))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("excede el stock máximo");
    }

    @Test
    @DisplayName("RF4.4: Registrar AJUSTE de inventario")
    void registrarMovimiento_Ajuste_Exitoso() {
        // Given
        registrarMovimientoDTO.setTipoMovimiento("AJUSTE");
        registrarMovimientoDTO.setCantidad(75); // Ajuste a 75 unidades
        movimiento.setTipoMovimiento(TipoMovimiento.AJUSTE);
        movimiento.setStockNuevo(75);
        
        when(productoRepository.findById(anyLong())).thenReturn(Optional.of(producto));
        when(movimientoMapper.toEntity(any(RegistrarMovimientoDTO.class))).thenReturn(movimiento);
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenReturn(movimiento);
        when(movimientoMapper.toDTO(any(MovimientoInventario.class))).thenReturn(movimientoDTO);

        // When
        MovimientoInventarioDTO resultado = movimientoService.registrarMovimiento(registrarMovimientoDTO);

        // Then
        assertThat(resultado).isNotNull();
        verify(productoRepository).save(argThat(p -> p.getStockActual() == 75));
    }

    @Test
    @DisplayName("RF4.4: Registrar DEVOLUCION de inventario")
    void registrarMovimiento_Devolucion_Exitoso() {
        // Given
        registrarMovimientoDTO.setTipoMovimiento("DEVOLUCION");
        movimiento.setTipoMovimiento(TipoMovimiento.DEVOLUCION);
        movimiento.setStockNuevo(70); // 50 + 20 = 70
        
        when(productoRepository.findById(anyLong())).thenReturn(Optional.of(producto));
        when(movimientoMapper.toEntity(any(RegistrarMovimientoDTO.class))).thenReturn(movimiento);
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenReturn(movimiento);
        when(movimientoMapper.toDTO(any(MovimientoInventario.class))).thenReturn(movimientoDTO);

        // When
        MovimientoInventarioDTO resultado = movimientoService.registrarMovimiento(registrarMovimientoDTO);

        // Then
        assertThat(resultado).isNotNull();
        verify(productoRepository).save(argThat(p -> p.getStockActual() == 70));
    }

    @Test
    @DisplayName("Registrar movimiento con producto inexistente debe lanzar excepción")
    void registrarMovimiento_ProductoNoExiste_LanzaExcepcion() {
        // Given
        when(productoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> movimientoService.registrarMovimiento(registrarMovimientoDTO))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Producto");
    }

    @Test
    @DisplayName("Obtener movimiento por ID exitosamente")
    void obtenerPorId_Exitoso() {
        // Given
        when(movimientoRepository.findById(anyLong())).thenReturn(Optional.of(movimiento));
        when(movimientoMapper.toDTO(any(MovimientoInventario.class))).thenReturn(movimientoDTO);

        // When
        MovimientoInventarioDTO resultado = movimientoService.obtenerPorId(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("RF4.4: Listar movimientos de un producto")
    void listarPorProducto_Exitoso() {
        // Given
        List<MovimientoInventario> movimientos = Arrays.asList(movimiento);
        when(productoRepository.existsById(anyLong())).thenReturn(true);
        when(movimientoRepository.findByProductoIdOrderByFechaMovimientoDesc(anyLong())).thenReturn(movimientos);
        when(movimientoMapper.toDTO(any(MovimientoInventario.class))).thenReturn(movimientoDTO);

        // When
        List<MovimientoInventarioDTO> resultado = movimientoService.listarPorProducto(1L);

        // Then
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Listar movimientos por tipo")
    void listarPorTipo_Exitoso() {
        // Given
        List<MovimientoInventario> movimientos = Arrays.asList(movimiento);
        when(movimientoRepository.findByTipoMovimientoOrderByFechaMovimientoDesc(any(TipoMovimiento.class)))
                .thenReturn(movimientos);
        when(movimientoMapper.toDTO(any(MovimientoInventario.class))).thenReturn(movimientoDTO);

        // When
        List<MovimientoInventarioDTO> resultado = movimientoService.listarPorTipo("ENTRADA");

        // Then
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("RF4.4: Listar movimientos por rango de fechas")
    void listarPorRangoFechas_Exitoso() {
        // Given
        LocalDateTime fechaInicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fechaFin = LocalDateTime.now();
        List<MovimientoInventario> movimientos = Arrays.asList(movimiento);
        
        when(movimientoRepository.findByFechaMovimientoBetweenOrderByFechaMovimientoDesc(any(), any()))
                .thenReturn(movimientos);
        when(movimientoMapper.toDTO(any(MovimientoInventario.class))).thenReturn(movimientoDTO);

        // When
        List<MovimientoInventarioDTO> resultado = movimientoService.listarPorRangoFechas(fechaInicio, fechaFin);

        // Then
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Listar movimientos con rango de fechas inválido debe lanzar excepción")
    void listarPorRangoFechas_RangoInvalido_LanzaExcepcion() {
        // Given
        LocalDateTime fechaInicio = LocalDateTime.now();
        LocalDateTime fechaFin = LocalDateTime.now().minusDays(7); // Fecha fin anterior a inicio

        // When & Then
        assertThatThrownBy(() -> movimientoService.listarPorRangoFechas(fechaInicio, fechaFin))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("fecha de inicio no puede ser posterior");
    }

    @Test
    @DisplayName("Listar movimientos del día")
    void listarMovimientosHoy_Exitoso() {
        // Given
        List<MovimientoInventario> movimientos = Arrays.asList(movimiento);
        when(movimientoRepository.findByFechaMovimientoBetweenOrderByFechaMovimientoDesc(any(), any()))
                .thenReturn(movimientos);
        when(movimientoMapper.toDTO(any(MovimientoInventario.class))).thenReturn(movimientoDTO);

        // When
        List<MovimientoInventarioDTO> resultado = movimientoService.listarMovimientosHoy();

        // Then
        assertThat(resultado).isNotEmpty();
    }

    @Test
    @DisplayName("Listar últimos movimientos")
    void listarUltimosMovimientos_Exitoso() {
        // Given
        List<MovimientoInventario> movimientos = Arrays.asList(movimiento);
        when(movimientoRepository.findTop10ByOrderByFechaMovimientoDesc()).thenReturn(movimientos);
        when(movimientoMapper.toDTO(any(MovimientoInventario.class))).thenReturn(movimientoDTO);

        // When
        List<MovimientoInventarioDTO> resultado = movimientoService.listarUltimosMovimientos(10);

        // Then
        assertThat(resultado).isNotEmpty();
        assertThat(resultado.size()).isLessThanOrEqualTo(10);
    }

    @Test
    @DisplayName("Contar movimientos de un producto")
    void contarMovimientosPorProducto_Exitoso() {
        // Given
        when(movimientoRepository.countByProductoId(anyLong())).thenReturn(15L);

        // When
        Long resultado = movimientoService.contarMovimientosPorProducto(1L);

        // Then
        assertThat(resultado).isEqualTo(15L);
    }
}
