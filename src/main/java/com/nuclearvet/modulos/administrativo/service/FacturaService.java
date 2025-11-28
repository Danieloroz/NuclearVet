package com.nuclearvet.modulos.administrativo.service;

import com.nuclearvet.common.exception.RecursoNoEncontradoException;
import com.nuclearvet.common.exception.ValidacionException;
import com.nuclearvet.modulos.administrativo.dto.CrearFacturaDTO;
import com.nuclearvet.modulos.administrativo.dto.FacturaDTO;
import com.nuclearvet.modulos.administrativo.dto.ItemFacturaDTO;
import com.nuclearvet.modulos.administrativo.entity.EstadoFactura;
import com.nuclearvet.modulos.administrativo.entity.Factura;
import com.nuclearvet.modulos.administrativo.entity.ItemFactura;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de facturas.
 * RF6.1 - Gestión de facturación
 * RF6.3 - Control de cuentas por cobrar
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConsultaRepository consultaRepository;
    private final ProductoRepository productoRepository;
    private final FacturaMapper facturaMapper;
    private final ItemFacturaMapper itemFacturaMapper;

    /**
     * RF6.1: Crear una nueva factura
     */
    @Transactional
    public FacturaDTO crearFactura(CrearFacturaDTO dto) {
        log.info("Creando factura para paciente: {}", dto.getPacienteId());

        // Validar paciente
        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente", "id", dto.getPacienteId()));

        // Validar propietario
        Usuario propietario = usuarioRepository.findById(dto.getPropietarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "id", dto.getPropietarioId()));

        // Validar usuario que emite
        Usuario emitidaPor = usuarioRepository.findById(dto.getEmitidaPorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "id", dto.getEmitidaPorId()));

        // Validar consulta (opcional)
        Consulta consulta = null;
        if (dto.getConsultaId() != null) {
            consulta = consultaRepository.findById(dto.getConsultaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Consulta", "id", dto.getConsultaId()));
        }

        // Crear factura base
        Factura factura = facturaMapper.toEntity(dto);
        factura.setNumeroFactura(generarNumeroFactura());
        factura.setPaciente(paciente);
        factura.setPropietario(propietario);
        factura.setConsulta(consulta);
        factura.setEmitidaPor(emitidaPor);
        factura.setEstado(EstadoFactura.PENDIENTE);
        factura.setActivo(true);

        // Procesar items
        for (CrearFacturaDTO.CrearItemFacturaDTO itemDTO : dto.getItems()) {
            ItemFactura item = ItemFactura.builder()
                    .tipo(itemDTO.getTipo())
                    .descripcion(itemDTO.getDescripcion())
                    .cantidad(itemDTO.getCantidad())
                    .precioUnitario(itemDTO.getPrecioUnitario())
                    .observaciones(itemDTO.getObservaciones())
                    .build();

            // Si es un producto, validar y asociar
            if (itemDTO.getProductoId() != null) {
                Producto producto = productoRepository.findById(itemDTO.getProductoId())
                        .orElseThrow(() -> new RecursoNoEncontradoException("Producto", "id", itemDTO.getProductoId()));
                item.setProducto(producto);
            }

            item.setActivo(true);
            factura.agregarItem(item);
        }

        // Calcular totales
        factura.calcularTotal();

        // Guardar factura
        Factura facturaGuardada = facturaRepository.save(factura);
        
        log.info("Factura creada exitosamente: {}", facturaGuardada.getNumeroFactura());
        return convertirADTO(facturaGuardada);
    }

    /**
     * RF6.1: Obtener factura por ID
     */
    @Transactional(readOnly = true)
    public FacturaDTO obtenerPorId(Long id) {
        log.info("Obteniendo factura: {}", id);
        
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Factura", "id", id));
        
        return convertirADTO(factura);
    }

    /**
     * RF6.1: Obtener factura por número
     */
    @Transactional(readOnly = true)
    public FacturaDTO obtenerPorNumero(String numeroFactura) {
        log.info("Obteniendo factura por número: {}", numeroFactura);
        
        Factura factura = facturaRepository.findByNumeroFactura(numeroFactura)
                .orElseThrow(() -> new RecursoNoEncontradoException("Factura", "numeroFactura", numeroFactura));
        
        return convertirADTO(factura);
    }

    /**
     * RF6.1: Obtener factura por consulta
     */
    @Transactional(readOnly = true)
    public FacturaDTO obtenerPorConsulta(Long consultaId) {
        log.info("Obteniendo factura por consulta: {}", consultaId);
        
        List<Factura> facturas = facturaRepository.findByConsultaIdOrderByFechaEmisionDesc(consultaId);
        
        if (facturas.isEmpty()) {
            return null;
        }
        
        return convertirADTO(facturas.get(0));
    }

    /**
     * RF6.1: Listar facturas por cliente
     */
    @Transactional(readOnly = true)
    public List<FacturaDTO> listarPorCliente(Long propietarioId) {
        log.info("Listando facturas del cliente: {}", propietarioId);
        
        List<Factura> facturas = facturaRepository.findByPropietarioIdOrderByFechaEmisionDesc(propietarioId);
        
        return facturas.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * RF6.1: Listar facturas por paciente
     */
    @Transactional(readOnly = true)
    public List<FacturaDTO> listarPorPaciente(Long pacienteId) {
        log.info("Listando facturas del paciente: {}", pacienteId);
        
        List<Factura> facturas = facturaRepository.findByPacienteIdOrderByFechaEmisionDesc(pacienteId);
        
        return facturas.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * RF6.3: Listar facturas por estado
     */
    @Transactional(readOnly = true)
    public List<FacturaDTO> listarPorEstado(EstadoFactura estado) {
        log.info("Listando facturas con estado: {}", estado);
        
        List<Factura> facturas = facturaRepository.findByEstadoOrderByFechaEmisionDesc(estado);
        
        return facturas.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * RF6.3: Listar facturas pendientes
     */
    @Transactional(readOnly = true)
    public List<FacturaDTO> listarPendientes() {
        log.info("Listando facturas pendientes de pago");
        
        List<Factura> facturas = facturaRepository.findFacturasPendientes();
        
        return facturas.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * RF6.3: Listar facturas vencidas
     */
    @Transactional(readOnly = true)
    public List<FacturaDTO> listarVencidas() {
        log.info("Listando facturas vencidas");
        
        List<Factura> facturas = facturaRepository.findFacturasVencidas(LocalDate.now());
        
        return facturas.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * RF6.1: Listar facturas por rango de fechas
     */
    @Transactional(readOnly = true)
    public List<FacturaDTO> listarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Listando facturas entre {} y {}", fechaInicio, fechaFin);
        
        if (fechaInicio.isAfter(fechaFin)) {
            throw new ValidacionException("La fecha de inicio no puede ser posterior a la fecha fin");
        }
        
        List<Factura> facturas = facturaRepository.findByFechaEmisionBetweenOrderByFechaEmisionDesc(
                fechaInicio, fechaFin);
        
        return facturas.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * RF6.1: Cancelar factura
     */
    @Transactional
    public void cancelarFactura(Long id, String motivo) {
        log.info("Cancelando factura: {}", id);
        
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Factura", "id", id));
        
        if (factura.getEstado() == EstadoFactura.PAGADA) {
            throw new ValidacionException("No se puede cancelar una factura que ya está pagada");
        }
        
        if (factura.getTotalPagado().compareTo(BigDecimal.ZERO) > 0) {
            throw new ValidacionException("No se puede cancelar una factura con pagos registrados");
        }
        
        factura.setEstado(EstadoFactura.CANCELADA);
        factura.setObservaciones(factura.getObservaciones() + "\nCANCELADA: " + motivo);
        
        facturaRepository.save(factura);
        log.info("Factura {} cancelada exitosamente", factura.getNumeroFactura());
    }

    /**
     * RF6.4: Calcular total facturado en un período
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalFacturado(LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Calculando total facturado entre {} y {}", fechaInicio, fechaFin);
        
        return facturaRepository.calcularTotalFacturado(fechaInicio, fechaFin);
    }

    /**
     * RF6.4: Calcular total recaudado en un período
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalRecaudado(LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Calculando total recaudado entre {} y {}", fechaInicio, fechaFin);
        
        return facturaRepository.calcularTotalRecaudado(fechaInicio, fechaFin);
    }

    /**
     * Actualizar estado de facturas vencidas
     */
    @Transactional
    public void actualizarFacturasVencidas() {
        log.info("Actualizando estado de facturas vencidas");
        
        List<Factura> facturasVencidas = facturaRepository.findFacturasVencidas(LocalDate.now());
        
        for (Factura factura : facturasVencidas) {
            factura.setEstado(EstadoFactura.VENCIDA);
            facturaRepository.save(factura);
        }
        
        log.info("Actualizadas {} facturas vencidas", facturasVencidas.size());
    }

    /**
     * Genera un número único de factura
     */
    private String generarNumeroFactura() {
        String prefijo = "FAC-";
        String anio = String.valueOf(LocalDate.now().getYear());
        
        Long ultimoNumero = facturaRepository.findLastFactura()
                .map(f -> {
                    String numero = f.getNumeroFactura();
                    String[] partes = numero.split("-");
                    if (partes.length == 3) {
                        return Long.parseLong(partes[2]);
                    }
                    return 0L;
                })
                .orElse(0L);
        
        Long nuevoNumero = ultimoNumero + 1;
        
        return String.format("%s%s-%06d", prefijo, anio, nuevoNumero);
    }

    /**
     * Convierte una Factura a FacturaDTO con todos los datos necesarios
     */
    private FacturaDTO convertirADTO(Factura factura) {
        FacturaDTO dto = facturaMapper.toDTO(factura);
        
        // Mapear campos manualmente que el mapper ignora
        // Nota: No podemos usar getId() porque EntidadBase no lo expone
        if (factura.getPaciente() != null) {
            dto.setPacienteNombre(factura.getPaciente().getNombre());
        }
        
        if (factura.getPropietario() != null) {
            dto.setPropietarioNombre(factura.getPropietario().getNombre() + " " + 
                                     factura.getPropietario().getApellido());
        }
        
        if (factura.getEmitidaPor() != null) {
            dto.setEmitidaPorNombre(factura.getEmitidaPor().getNombre() + " " + 
                                    factura.getEmitidaPor().getApellido());
        }
        
        // Mapear items
        List<ItemFacturaDTO> itemsDTO = factura.getItems().stream()
                .map(item -> {
                    ItemFacturaDTO itemDTO = itemFacturaMapper.toDTO(item);
                    if (item.getProducto() != null) {
                        itemDTO.setProductoNombre(item.getProducto().getNombre());
                    }
                    return itemDTO;
                })
                .collect(Collectors.toList());
        
        dto.setItems(itemsDTO);
        dto.setFechaCreacion(factura.getFechaCreacion() != null ? 
                             factura.getFechaCreacion().toLocalDate() : null);
        dto.setActivo(factura.getActivo());
        
        return dto;
    }
}
