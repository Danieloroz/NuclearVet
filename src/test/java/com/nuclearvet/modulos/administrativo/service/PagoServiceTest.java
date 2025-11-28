package com.nuclearvet.modulos.administrativo.service;

import com.nuclearvet.common.exception.RecursoNoEncontradoException;
import com.nuclearvet.common.exception.ValidacionException;
import com.nuclearvet.modulos.administrativo.dto.PagoDTO;
import com.nuclearvet.modulos.administrativo.dto.RegistrarPagoDTO;
import com.nuclearvet.modulos.administrativo.entity.EstadoFactura;
import com.nuclearvet.modulos.administrativo.entity.Factura;
import com.nuclearvet.modulos.administrativo.entity.MetodoPago;
import com.nuclearvet.modulos.administrativo.entity.Pago;
import com.nuclearvet.modulos.administrativo.mapper.PagoMapper;
import com.nuclearvet.modulos.administrativo.repository.FacturaRepository;
import com.nuclearvet.modulos.administrativo.repository.PagoRepository;
import com.nuclearvet.modulos.usuarios.entity.Usuario;
import com.nuclearvet.modulos.usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para PagoService
 * Módulo 6 - Administrativo (Facturación y Reportes)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PagoService - Tests")
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private FacturaRepository facturaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PagoMapper pagoMapper;

    @InjectMocks
    private PagoService pagoService;

    private Factura factura;
    private Usuario usuario;
    private Pago pago;
    private RegistrarPagoDTO registrarPagoDTO;

    @BeforeEach
    void setUp() {
        // Setup Usuario
        usuario = new Usuario();
        usuario.setNombre("María");
        usuario.setApellido("González");

        // Setup Factura
        factura = Factura.builder()
                .numeroFactura("FAC-2025-000001")
                .estado(EstadoFactura.PENDIENTE)
                .total(new BigDecimal("100.00"))
                .totalPagado(BigDecimal.ZERO)
                .saldoPendiente(new BigDecimal("100.00"))
                .build();

        // Setup Pago
        pago = Pago.builder()
                .numeroRecibo("REC-2025-000001")
                .fechaPago(LocalDateTime.now())
                .monto(new BigDecimal("50.00"))
                .metodoPago(MetodoPago.EFECTIVO)
                .factura(factura)
                .recibidoPor(usuario)
                .build();

        // Setup DTO
        registrarPagoDTO = new RegistrarPagoDTO();
        registrarPagoDTO.setFacturaId(1L);
        registrarPagoDTO.setFechaPago(LocalDateTime.now());
        registrarPagoDTO.setMonto(new BigDecimal("50.00"));
        registrarPagoDTO.setMetodoPago(MetodoPago.EFECTIVO);
        registrarPagoDTO.setRecibidoPorId(1L);
    }

    @Test
    @DisplayName("Debe registrar pago exitosamente")
    void testRegistrarPagoExitoso() {
        // Arrange
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(pagoRepository.findLastPago()).thenReturn(Optional.empty());
        when(pagoMapper.toEntity(any(RegistrarPagoDTO.class))).thenReturn(pago);
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);
        when(facturaRepository.save(any(Factura.class))).thenReturn(factura);
        when(pagoMapper.toDTO(any(Pago.class))).thenReturn(new PagoDTO());

        // Act
        PagoDTO resultado = pagoService.registrarPago(registrarPagoDTO);

        // Assert
        assertThat(resultado).isNotNull();
        verify(pagoRepository).save(any(Pago.class));
        verify(facturaRepository).save(any(Factura.class));
        verify(facturaRepository).findById(1L);
        verify(usuarioRepository).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando factura no existe")
    void testRegistrarPagoFacturaNoExiste() {
        // Arrange
        when(facturaRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pagoService.registrarPago(registrarPagoDTO))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Factura");

        verify(pagoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando factura está cancelada")
    void testRegistrarPagoFacturaCancelada() {
        // Arrange
        factura.setEstado(EstadoFactura.CANCELADA);
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act & Assert
        assertThatThrownBy(() -> pagoService.registrarPago(registrarPagoDTO))
                .isInstanceOf(ValidacionException.class)
                .hasMessageContaining("cancelada");

        verify(pagoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando monto excede saldo pendiente")
    void testRegistrarPagoExcedeSaldo() {
        // Arrange
        factura.setSaldoPendiente(new BigDecimal("30.00"));
        registrarPagoDTO.setMonto(new BigDecimal("50.00"));
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act & Assert
        assertThatThrownBy(() -> pagoService.registrarPago(registrarPagoDTO))
                .isInstanceOf(ValidacionException.class)
                .hasMessageContaining("excede el saldo pendiente");

        verify(pagoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe obtener pago por ID")
    void testObtenerPorId() {
        // Arrange
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(pagoMapper.toDTO(any(Pago.class))).thenReturn(new PagoDTO());

        // Act
        PagoDTO resultado = pagoService.obtenerPorId(1L);

        // Assert
        assertThat(resultado).isNotNull();
        verify(pagoRepository).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando pago no existe por ID")
    void testObtenerPorIdNoExiste() {
        // Arrange
        when(pagoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pagoService.obtenerPorId(999L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Pago");
    }

    @Test
    @DisplayName("Debe obtener pago por número de recibo")
    void testObtenerPorNumeroRecibo() {
        // Arrange
        when(pagoRepository.findByNumeroRecibo("REC-2025-000001"))
                .thenReturn(Optional.of(pago));
        when(pagoMapper.toDTO(any(Pago.class))).thenReturn(new PagoDTO());

        // Act
        PagoDTO resultado = pagoService.obtenerPorNumeroRecibo("REC-2025-000001");

        // Assert
        assertThat(resultado).isNotNull();
        verify(pagoRepository).findByNumeroRecibo("REC-2025-000001");
    }

    @Test
    @DisplayName("Debe listar pagos por factura")
    void testListarPorFactura() {
        // Arrange
        List<Pago> pagos = Arrays.asList(pago);
        when(pagoRepository.findByFacturaIdOrderByFechaPagoDesc(1L)).thenReturn(pagos);
        when(pagoMapper.toDTO(any(Pago.class))).thenReturn(new PagoDTO());

        // Act
        List<PagoDTO> resultado = pagoService.listarPorFactura(1L);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(pagoRepository).findByFacturaIdOrderByFechaPagoDesc(1L);
    }

    @Test
    @DisplayName("Debe listar pagos por método de pago")
    void testListarPorMetodo() {
        // Arrange
        List<Pago> pagos = Arrays.asList(pago);
        when(pagoRepository.findByMetodoPagoOrderByFechaPagoDesc(MetodoPago.EFECTIVO))
                .thenReturn(pagos);
        when(pagoMapper.toDTO(any(Pago.class))).thenReturn(new PagoDTO());

        // Act
        List<PagoDTO> resultado = pagoService.listarPorMetodo(MetodoPago.EFECTIVO);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(pagoRepository).findByMetodoPagoOrderByFechaPagoDesc(MetodoPago.EFECTIVO);
    }

    @Test
    @DisplayName("Debe listar pagos por rango de fechas")
    void testListarPorRangoFechas() {
        // Arrange
        LocalDate inicio = LocalDate.now().minusDays(30);
        LocalDate fin = LocalDate.now();
        List<Pago> pagos = Arrays.asList(pago);
        when(pagoRepository.findByFechaPagoBetweenOrderByFechaPagoDesc(any(), any()))
                .thenReturn(pagos);
        when(pagoMapper.toDTO(any(Pago.class))).thenReturn(new PagoDTO());

        // Act
        List<PagoDTO> resultado = pagoService.listarPorRangoFechas(inicio, fin);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(pagoRepository).findByFechaPagoBetweenOrderByFechaPagoDesc(any(), any());
    }

    @Test
    @DisplayName("Debe lanzar excepción al listar con fechas inválidas")
    void testListarPorRangoFechasInvalidas() {
        // Arrange
        LocalDate inicio = LocalDate.now();
        LocalDate fin = LocalDate.now().minusDays(30);

        // Act & Assert
        assertThatThrownBy(() -> pagoService.listarPorRangoFechas(inicio, fin))
                .isInstanceOf(ValidacionException.class)
                .hasMessageContaining("fecha de inicio");
    }

    @Test
    @DisplayName("Debe listar pagos por usuario")
    void testListarPorUsuario() {
        // Arrange
        List<Pago> pagos = Arrays.asList(pago);
        when(pagoRepository.findByRecibidoPorIdOrderByFechaPagoDesc(1L))
                .thenReturn(pagos);
        when(pagoMapper.toDTO(any(Pago.class))).thenReturn(new PagoDTO());

        // Act
        List<PagoDTO> resultado = pagoService.listarPorUsuario(1L);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(pagoRepository).findByRecibidoPorIdOrderByFechaPagoDesc(1L);
    }

    @Test
    @DisplayName("Debe anular pago exitosamente")
    void testAnularPago() {
        // Arrange
        pago.setFechaPago(LocalDateTime.now().minusDays(5));
        pago.setActivo(true);
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);
        when(facturaRepository.save(any(Factura.class))).thenReturn(factura);

        // Act
        pagoService.anularPago(1L, "Error en el monto");

        // Assert
        verify(pagoRepository).save(any(Pago.class));
        verify(facturaRepository).save(any(Factura.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al anular pago antiguo (más de 30 días)")
    void testAnularPagoAntiguo() {
        // Arrange
        pago.setFechaPago(LocalDateTime.now().minusDays(35));
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));

        // Act & Assert
        assertThatThrownBy(() -> pagoService.anularPago(1L, "Motivo"))
                .isInstanceOf(ValidacionException.class)
                .hasMessageContaining("30 días");

        verify(pagoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe calcular total de pagos en período")
    void testCalcularTotalPagos() {
        // Arrange
        LocalDate inicio = LocalDate.now().minusDays(30);
        LocalDate fin = LocalDate.now();
        BigDecimal total = new BigDecimal("1500.00");
        when(pagoRepository.calcularTotalPagos(any(), any())).thenReturn(total);

        // Act
        BigDecimal resultado = pagoService.calcularTotalPagos(inicio, fin);

        // Assert
        assertThat(resultado).isEqualByComparingTo(total);
        verify(pagoRepository).calcularTotalPagos(any(), any());
    }

    @Test
    @DisplayName("Debe calcular total por método de pago")
    void testCalcularTotalPorMetodo() {
        // Arrange
        LocalDate inicio = LocalDate.now().minusDays(30);
        LocalDate fin = LocalDate.now();
        BigDecimal total = new BigDecimal("800.00");
        when(pagoRepository.calcularTotalPorMetodo(any(), any(), any())).thenReturn(total);

        // Act
        BigDecimal resultado = pagoService.calcularTotalPorMetodo(
                MetodoPago.EFECTIVO, inicio, fin);

        // Assert
        assertThat(resultado).isEqualByComparingTo(total);
        verify(pagoRepository).calcularTotalPorMetodo(any(), any(), any());
    }

    @Test
    @DisplayName("Debe generar número de recibo autoincremental")
    void testGenerarNumeroReciboAutoincremental() {
        // Arrange
        Pago ultimoPago = Pago.builder()
                .numeroRecibo("REC-2025-000010")
                .build();
        when(pagoRepository.findLastPago()).thenReturn(Optional.of(ultimoPago));
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(pagoMapper.toEntity(any(RegistrarPagoDTO.class))).thenReturn(pago);
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);
        when(facturaRepository.save(any(Factura.class))).thenReturn(factura);
        when(pagoMapper.toDTO(any(Pago.class))).thenReturn(new PagoDTO());

        // Act
        pagoService.registrarPago(registrarPagoDTO);

        // Assert
        verify(pagoRepository).findLastPago();
        verify(pagoRepository).save(any(Pago.class));
    }

    @Test
    @DisplayName("Debe actualizar estado de factura a PARCIAL cuando hay pago parcial")
    void testPagoActualizaEstadoAParcial() {
        // Arrange
        factura.setTotal(new BigDecimal("100.00"));
        factura.setSaldoPendiente(new BigDecimal("100.00"));
        registrarPagoDTO.setMonto(new BigDecimal("50.00"));
        
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(pagoRepository.findLastPago()).thenReturn(Optional.empty());
        when(pagoMapper.toEntity(any(RegistrarPagoDTO.class))).thenReturn(pago);
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);
        when(facturaRepository.save(any(Factura.class))).thenReturn(factura);
        when(pagoMapper.toDTO(any(Pago.class))).thenReturn(new PagoDTO());

        // Act
        pagoService.registrarPago(registrarPagoDTO);

        // Assert
        verify(facturaRepository).save(any(Factura.class));
    }

    @Test
    @DisplayName("Debe actualizar estado de factura a PAGADA cuando se completa el pago")
    void testPagoActualizaEstadoAPagada() {
        // Arrange
        factura.setTotal(new BigDecimal("100.00"));
        factura.setSaldoPendiente(new BigDecimal("100.00"));
        registrarPagoDTO.setMonto(new BigDecimal("100.00"));
        
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(pagoRepository.findLastPago()).thenReturn(Optional.empty());
        when(pagoMapper.toEntity(any(RegistrarPagoDTO.class))).thenReturn(pago);
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);
        when(facturaRepository.save(any(Factura.class))).thenReturn(factura);
        when(pagoMapper.toDTO(any(Pago.class))).thenReturn(new PagoDTO());

        // Act
        pagoService.registrarPago(registrarPagoDTO);

        // Assert
        verify(facturaRepository).save(any(Factura.class));
    }
}
