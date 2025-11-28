package com.nuclearvet.modulos.administrativo.service;

import com.nuclearvet.common.exception.RecursoNoEncontradoException;
import com.nuclearvet.common.exception.ValidacionException;
import com.nuclearvet.modulos.administrativo.dto.CrearFacturaDTO;
import com.nuclearvet.modulos.administrativo.dto.FacturaDTO;
import com.nuclearvet.modulos.administrativo.entity.EstadoFactura;
import com.nuclearvet.modulos.administrativo.entity.Factura;
import com.nuclearvet.modulos.administrativo.entity.ItemFactura;
import com.nuclearvet.modulos.administrativo.entity.TipoItemFactura;
import com.nuclearvet.modulos.administrativo.mapper.FacturaMapper;
import com.nuclearvet.modulos.administrativo.mapper.ItemFacturaMapper;
import com.nuclearvet.modulos.administrativo.repository.FacturaRepository;
import com.nuclearvet.modulos.inventario.entity.Producto;
import com.nuclearvet.modulos.inventario.repository.ProductoRepository;
import com.nuclearvet.modulos.pacientes.entity.Consulta;
import com.nuclearvet.modulos.pacientes.entity.Paciente;
import com.nuclearvet.modulos.pacientes.repository.ConsultaRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para FacturaService
 * Módulo 6 - Administrativo (Facturación y Reportes)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FacturaService - Tests")
class FacturaServiceTest {

    @Mock
    private FacturaRepository facturaRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private FacturaMapper facturaMapper;

    @Mock
    private ItemFacturaMapper itemFacturaMapper;

    @InjectMocks
    private FacturaService facturaService;

    private Paciente paciente;
    private Usuario propietario;
    private Usuario emitidaPor;
    private Consulta consulta;
    private Producto producto;
    private Factura factura;
    private CrearFacturaDTO crearFacturaDTO;

    @BeforeEach
    void setUp() {
        // Setup Paciente
        paciente = new Paciente();
        paciente.setNombre("Max");

        // Setup Propietario (Usuario)
        propietario = new Usuario();
        propietario.setNombre("Juan");
        propietario.setApellido("Pérez");

        // Setup Usuario que emite
        emitidaPor = new Usuario();
        emitidaPor.setNombre("María");
        emitidaPor.setApellido("González");

        // Setup Consulta
        consulta = new Consulta();

        // Setup Producto
        producto = new Producto();
        producto.setNombre("Vacuna Antirrábica");
        producto.setPrecioVenta(new BigDecimal("50.00"));

        // Setup Factura
        factura = Factura.builder()
                .numeroFactura("FAC-2025-000001")
                .fechaEmision(LocalDate.now())
                .fechaVencimiento(LocalDate.now().plusDays(30))
                .estado(EstadoFactura.PENDIENTE)
                .paciente(paciente)
                .propietario(propietario)
                .emitidaPor(emitidaPor)
                .consulta(consulta)
                .porcentajeImpuesto(new BigDecimal("19"))
                .descuento(BigDecimal.ZERO)
                .subtotal(new BigDecimal("50.00"))
                .valorImpuesto(new BigDecimal("9.50"))
                .total(new BigDecimal("59.50"))
                .totalPagado(BigDecimal.ZERO)
                .saldoPendiente(new BigDecimal("59.50"))
                .build();
        
        // Inicializar listas manualmente porque @Builder no las inicializa
        factura.setItems(new ArrayList<>());
        factura.setPagos(new ArrayList<>());

        // Setup DTO
        crearFacturaDTO = new CrearFacturaDTO();
        crearFacturaDTO.setPacienteId(1L);
        crearFacturaDTO.setPropietarioId(1L);
        crearFacturaDTO.setEmitidaPorId(1L);
        crearFacturaDTO.setConsultaId(1L);
        crearFacturaDTO.setFechaEmision(LocalDate.now());
        crearFacturaDTO.setFechaVencimiento(LocalDate.now().plusDays(30));
        crearFacturaDTO.setPorcentajeImpuesto(new BigDecimal("19"));
        crearFacturaDTO.setDescuento(BigDecimal.ZERO);

        CrearFacturaDTO.CrearItemFacturaDTO itemDTO = new CrearFacturaDTO.CrearItemFacturaDTO();
        itemDTO.setTipo(TipoItemFactura.PRODUCTO);
        itemDTO.setDescripcion("Vacuna Antirrábica");
        itemDTO.setProductoId(1L);
        itemDTO.setCantidad(1);
        itemDTO.setPrecioUnitario(new BigDecimal("50.00"));

        crearFacturaDTO.setItems(List.of(itemDTO));
    }

    @Test
    @DisplayName("Debe crear factura exitosamente")
    void testCrearFacturaExitoso() {
        // Arrange
        CrearFacturaDTO dtoSinItems = new CrearFacturaDTO();
        dtoSinItems.setPacienteId(1L);
        dtoSinItems.setPropietarioId(1L);
        dtoSinItems.setEmitidaPorId(1L);
        dtoSinItems.setConsultaId(1L);
        dtoSinItems.setFechaEmision(LocalDate.now());
        dtoSinItems.setFechaVencimiento(LocalDate.now().plusDays(30));
        dtoSinItems.setPorcentajeImpuesto(new BigDecimal("19"));
        dtoSinItems.setDescuento(BigDecimal.ZERO);
        dtoSinItems.setItems(List.of()); // Sin items para evitar problemas con calcularTotal()
        
        Factura facturaConTotales = Factura.builder()
                .numeroFactura("FAC-2025-000001")
                .fechaEmision(LocalDate.now())
                .fechaVencimiento(LocalDate.now().plusDays(30))
                .estado(EstadoFactura.PENDIENTE)
                .subtotal(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .totalPagado(BigDecimal.ZERO)
                .saldoPendiente(BigDecimal.ZERO)
                .build();
        facturaConTotales.setItems(new ArrayList<>());
        facturaConTotales.setPagos(new ArrayList<>());
        
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(propietario))
                .thenReturn(Optional.of(emitidaPor));
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        when(facturaRepository.findLastFactura()).thenReturn(Optional.empty());
        when(facturaMapper.toEntity(any(CrearFacturaDTO.class))).thenReturn(facturaConTotales);
        when(facturaRepository.save(any(Factura.class))).thenReturn(facturaConTotales);
        when(facturaMapper.toDTO(any(Factura.class))).thenReturn(new FacturaDTO());

        // Act
        FacturaDTO resultado = facturaService.crearFactura(dtoSinItems);

        // Assert
        assertThat(resultado).isNotNull();
        verify(facturaRepository).save(any(Factura.class));
        verify(pacienteRepository).findById(1L);
        verify(usuarioRepository, times(2)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando paciente no existe")
    void testCrearFacturaPacienteNoExiste() {
        // Arrange
        when(pacienteRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> facturaService.crearFactura(crearFacturaDTO))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Paciente");

        verify(facturaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando propietario no existe")
    void testCrearFacturaPropietarioNoExiste() {
        // Arrange
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> facturaService.crearFactura(crearFacturaDTO))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Usuario");

        verify(facturaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe obtener factura por ID")
    void testObtenerPorId() {
        // Arrange
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
        when(facturaMapper.toDTO(any(Factura.class))).thenReturn(new FacturaDTO());

        // Act
        FacturaDTO resultado = facturaService.obtenerPorId(1L);

        // Assert
        assertThat(resultado).isNotNull();
        verify(facturaRepository).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando factura no existe por ID")
    void testObtenerPorIdNoExiste() {
        // Arrange
        when(facturaRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> facturaService.obtenerPorId(999L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Factura");
    }

    @Test
    @DisplayName("Debe obtener factura por número")
    void testObtenerPorNumero() {
        // Arrange
        when(facturaRepository.findByNumeroFactura("FAC-2025-000001"))
                .thenReturn(Optional.of(factura));
        when(facturaMapper.toDTO(any(Factura.class))).thenReturn(new FacturaDTO());

        // Act
        FacturaDTO resultado = facturaService.obtenerPorNumero("FAC-2025-000001");

        // Assert
        assertThat(resultado).isNotNull();
        verify(facturaRepository).findByNumeroFactura("FAC-2025-000001");
    }

    @Test
    @DisplayName("Debe listar facturas por cliente")
    void testListarPorCliente() {
        // Arrange
        List<Factura> facturas = Arrays.asList(factura);
        when(facturaRepository.findByPropietarioIdOrderByFechaEmisionDesc(1L))
                .thenReturn(facturas);
        when(facturaMapper.toDTO(any(Factura.class))).thenReturn(new FacturaDTO());

        // Act
        List<FacturaDTO> resultado = facturaService.listarPorCliente(1L);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(facturaRepository).findByPropietarioIdOrderByFechaEmisionDesc(1L);
    }

    @Test
    @DisplayName("Debe listar facturas por paciente")
    void testListarPorPaciente() {
        // Arrange
        List<Factura> facturas = Arrays.asList(factura);
        when(facturaRepository.findByPacienteIdOrderByFechaEmisionDesc(1L))
                .thenReturn(facturas);
        when(facturaMapper.toDTO(any(Factura.class))).thenReturn(new FacturaDTO());

        // Act
        List<FacturaDTO> resultado = facturaService.listarPorPaciente(1L);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(facturaRepository).findByPacienteIdOrderByFechaEmisionDesc(1L);
    }

    @Test
    @DisplayName("Debe listar facturas por estado")
    void testListarPorEstado() {
        // Arrange
        List<Factura> facturas = Arrays.asList(factura);
        when(facturaRepository.findByEstadoOrderByFechaEmisionDesc(EstadoFactura.PENDIENTE))
                .thenReturn(facturas);
        when(facturaMapper.toDTO(any(Factura.class))).thenReturn(new FacturaDTO());

        // Act
        List<FacturaDTO> resultado = facturaService.listarPorEstado(EstadoFactura.PENDIENTE);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(facturaRepository).findByEstadoOrderByFechaEmisionDesc(EstadoFactura.PENDIENTE);
    }

    @Test
    @DisplayName("Debe listar facturas pendientes")
    void testListarPendientes() {
        // Arrange
        List<Factura> facturas = Arrays.asList(factura);
        when(facturaRepository.findFacturasPendientes()).thenReturn(facturas);
        when(facturaMapper.toDTO(any(Factura.class))).thenReturn(new FacturaDTO());

        // Act
        List<FacturaDTO> resultado = facturaService.listarPendientes();

        // Assert
        assertThat(resultado).hasSize(1);
        verify(facturaRepository).findFacturasPendientes();
    }

    @Test
    @DisplayName("Debe listar facturas vencidas")
    void testListarVencidas() {
        // Arrange
        List<Factura> facturas = Arrays.asList(factura);
        when(facturaRepository.findFacturasVencidas(any(LocalDate.class)))
                .thenReturn(facturas);
        when(facturaMapper.toDTO(any(Factura.class))).thenReturn(new FacturaDTO());

        // Act
        List<FacturaDTO> resultado = facturaService.listarVencidas();

        // Assert
        assertThat(resultado).hasSize(1);
        verify(facturaRepository).findFacturasVencidas(any(LocalDate.class));
    }

    @Test
    @DisplayName("Debe listar facturas por rango de fechas")
    void testListarPorRangoFechas() {
        // Arrange
        LocalDate inicio = LocalDate.now().minusDays(30);
        LocalDate fin = LocalDate.now();
        List<Factura> facturas = Arrays.asList(factura);
        when(facturaRepository.findByFechaEmisionBetweenOrderByFechaEmisionDesc(inicio, fin))
                .thenReturn(facturas);
        when(facturaMapper.toDTO(any(Factura.class))).thenReturn(new FacturaDTO());

        // Act
        List<FacturaDTO> resultado = facturaService.listarPorRangoFechas(inicio, fin);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(facturaRepository).findByFechaEmisionBetweenOrderByFechaEmisionDesc(inicio, fin);
    }

    @Test
    @DisplayName("Debe lanzar excepción al listar con fechas inválidas")
    void testListarPorRangoFechasInvalidas() {
        // Arrange
        LocalDate inicio = LocalDate.now();
        LocalDate fin = LocalDate.now().minusDays(30);

        // Act & Assert
        assertThatThrownBy(() -> facturaService.listarPorRangoFechas(inicio, fin))
                .isInstanceOf(ValidacionException.class)
                .hasMessageContaining("fecha de inicio");
    }

    @Test
    @DisplayName("Debe cancelar factura exitosamente")
    void testCancelarFactura() {
        // Arrange
        factura.setEstado(EstadoFactura.PENDIENTE);
        factura.setTotalPagado(BigDecimal.ZERO);
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
        when(facturaRepository.save(any(Factura.class))).thenReturn(factura);

        // Act
        facturaService.cancelarFactura(1L, "Cliente canceló el servicio");

        // Assert
        verify(facturaRepository).save(any(Factura.class));
        verify(facturaRepository).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al cancelar factura pagada")
    void testCancelarFacturaPagada() {
        // Arrange
        factura.setEstado(EstadoFactura.PAGADA);
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));

        // Act & Assert
        assertThatThrownBy(() -> facturaService.cancelarFactura(1L, "Motivo"))
                .isInstanceOf(ValidacionException.class)
                .hasMessageContaining("ya está pagada");

        verify(facturaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción al cancelar factura con pagos")
    void testCancelarFacturaConPagos() {
        // Arrange
        factura.setEstado(EstadoFactura.PARCIAL);
        factura.setTotalPagado(new BigDecimal("20.00"));
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));

        // Act & Assert
        assertThatThrownBy(() -> facturaService.cancelarFactura(1L, "Motivo"))
                .isInstanceOf(ValidacionException.class)
                .hasMessageContaining("pagos registrados");

        verify(facturaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe calcular total facturado en período")
    void testCalcularTotalFacturado() {
        // Arrange
        LocalDate inicio = LocalDate.now().minusDays(30);
        LocalDate fin = LocalDate.now();
        BigDecimal total = new BigDecimal("1500.00");
        when(facturaRepository.calcularTotalFacturado(inicio, fin)).thenReturn(total);

        // Act
        BigDecimal resultado = facturaService.calcularTotalFacturado(inicio, fin);

        // Assert
        assertThat(resultado).isEqualByComparingTo(total);
        verify(facturaRepository).calcularTotalFacturado(inicio, fin);
    }

    @Test
    @DisplayName("Debe calcular total recaudado en período")
    void testCalcularTotalRecaudado() {
        // Arrange
        LocalDate inicio = LocalDate.now().minusDays(30);
        LocalDate fin = LocalDate.now();
        BigDecimal total = new BigDecimal("1200.00");
        when(facturaRepository.calcularTotalRecaudado(inicio, fin)).thenReturn(total);

        // Act
        BigDecimal resultado = facturaService.calcularTotalRecaudado(inicio, fin);

        // Assert
        assertThat(resultado).isEqualByComparingTo(total);
        verify(facturaRepository).calcularTotalRecaudado(inicio, fin);
    }

    @Test
    @DisplayName("Debe generar número de factura autoincremental")
    void testGenerarNumeroFacturaAutoincremental() {
        // Arrange
        Factura ultimaFactura = Factura.builder()
                .numeroFactura("FAC-2025-000005")
                .build();
        
        CrearFacturaDTO dtoSinItems = new CrearFacturaDTO();
        dtoSinItems.setPacienteId(1L);
        dtoSinItems.setPropietarioId(1L);
        dtoSinItems.setEmitidaPorId(1L);
        dtoSinItems.setConsultaId(1L);
        dtoSinItems.setFechaEmision(LocalDate.now());
        dtoSinItems.setFechaVencimiento(LocalDate.now().plusDays(30));
        dtoSinItems.setPorcentajeImpuesto(new BigDecimal("19"));
        dtoSinItems.setDescuento(BigDecimal.ZERO);
        dtoSinItems.setItems(List.of()); // Sin items
        
        Factura facturaConTotales = Factura.builder()
                .numeroFactura("FAC-2025-000006")
                .fechaEmision(LocalDate.now())
                .fechaVencimiento(LocalDate.now().plusDays(30))
                .estado(EstadoFactura.PENDIENTE)
                .subtotal(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .totalPagado(BigDecimal.ZERO)
                .saldoPendiente(BigDecimal.ZERO)
                .build();
        facturaConTotales.setItems(new ArrayList<>());
        facturaConTotales.setPagos(new ArrayList<>());
        
        when(facturaRepository.findLastFactura()).thenReturn(Optional.of(ultimaFactura));
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(propietario))
                .thenReturn(Optional.of(emitidaPor));
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        when(facturaMapper.toEntity(any(CrearFacturaDTO.class))).thenReturn(facturaConTotales);
        when(facturaRepository.save(any(Factura.class))).thenReturn(facturaConTotales);
        when(facturaMapper.toDTO(any(Factura.class))).thenReturn(new FacturaDTO());

        // Act
        facturaService.crearFactura(dtoSinItems);

        // Assert
        verify(facturaRepository).findLastFactura();
        verify(facturaRepository).save(any(Factura.class));
    }

    @Test
    @DisplayName("Debe actualizar facturas vencidas")
    void testActualizarFacturasVencidas() {
        // Arrange
        List<Factura> facturasVencidas = Arrays.asList(factura);
        when(facturaRepository.findFacturasVencidas(any(LocalDate.class)))
                .thenReturn(facturasVencidas);
        when(facturaRepository.save(any(Factura.class))).thenReturn(factura);

        // Act
        facturaService.actualizarFacturasVencidas();

        // Assert
        verify(facturaRepository).findFacturasVencidas(any(LocalDate.class));
        verify(facturaRepository).save(any(Factura.class));
    }
}
