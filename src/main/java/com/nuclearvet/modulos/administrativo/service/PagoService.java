package com.nuclearvet.modulos.administrativo.service;

import com.nuclearvet.common.exception.RecursoNoEncontradoException;
import com.nuclearvet.common.exception.ValidacionException;
import com.nuclearvet.modulos.administrativo.dto.PagoDTO;
import com.nuclearvet.modulos.administrativo.dto.RegistrarPagoDTO;
import com.nuclearvet.modulos.administrativo.entity.Factura;
import com.nuclearvet.modulos.administrativo.entity.MetodoPago;
import com.nuclearvet.modulos.administrativo.entity.Pago;
import com.nuclearvet.modulos.administrativo.mapper.PagoMapper;
import com.nuclearvet.modulos.administrativo.repository.FacturaRepository;
import com.nuclearvet.modulos.administrativo.repository.PagoRepository;
import com.nuclearvet.modulos.usuarios.entity.Usuario;
import com.nuclearvet.modulos.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de pagos.
 * RF6.2 - Registro de pagos
 * RF6.3 - Control de pagos parciales
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PagoService {

    private final PagoRepository pagoRepository;
    private final FacturaRepository facturaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PagoMapper pagoMapper;

    /**
     * RF6.2: Registrar un pago a una factura
     */
    @Transactional
    public PagoDTO registrarPago(RegistrarPagoDTO dto) {
        log.info("Registrando pago para factura: {}", dto.getFacturaId());

        // Validar factura
        Factura factura = facturaRepository.findById(dto.getFacturaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Factura", "id", dto.getFacturaId()));

        // Validar usuario que recibe
        Usuario recibidoPor = usuarioRepository.findById(dto.getRecibidoPorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "id", dto.getRecibidoPorId()));

        // Validar que la factura no esté cancelada
        if (factura.getEstado().name().equals("CANCELADA")) {
            throw new ValidacionException("No se puede registrar un pago para una factura cancelada");
        }

        // Validar que el pago no exceda el saldo pendiente
        BigDecimal saldoPendiente = factura.getSaldoPendiente();
        if (dto.getMonto().compareTo(saldoPendiente) > 0) {
            throw new ValidacionException(
                    String.format("El monto del pago ($%.2f) excede el saldo pendiente ($%.2f)", 
                            dto.getMonto(), saldoPendiente));
        }

        // Crear pago
        Pago pago = pagoMapper.toEntity(dto);
        pago.setNumeroRecibo(generarNumeroRecibo());
        pago.setFactura(factura);
        pago.setRecibidoPor(recibidoPor);
        pago.setActivo(true);

        // Guardar pago
        Pago pagoGuardado = pagoRepository.save(pago);

        // Actualizar factura
        factura.registrarPago(pagoGuardado);
        facturaRepository.save(factura);

        log.info("Pago registrado exitosamente: {} por ${}", pagoGuardado.getNumeroRecibo(), pagoGuardado.getMonto());
        return convertirADTO(pagoGuardado);
    }

    /**
     * RF6.2: Obtener pago por ID
     */
    @Transactional(readOnly = true)
    public PagoDTO obtenerPorId(Long id) {
        log.info("Obteniendo pago: {}", id);
        
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago", "id", id));
        
        return convertirADTO(pago);
    }

    /**
     * RF6.2: Obtener pago por número de recibo
     */
    @Transactional(readOnly = true)
    public PagoDTO obtenerPorNumeroRecibo(String numeroRecibo) {
        log.info("Obteniendo pago por recibo: {}", numeroRecibo);
        
        Pago pago = pagoRepository.findByNumeroRecibo(numeroRecibo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago", "numeroRecibo", numeroRecibo));
        
        return convertirADTO(pago);
    }

    /**
     * RF6.2: Listar pagos de una factura
     */
    @Transactional(readOnly = true)
    public List<PagoDTO> listarPorFactura(Long facturaId) {
        log.info("Listando pagos de la factura: {}", facturaId);
        
        List<Pago> pagos = pagoRepository.findByFacturaIdOrderByFechaPagoDesc(facturaId);
        
        return pagos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * RF6.2: Listar pagos por método
     */
    @Transactional(readOnly = true)
    public List<PagoDTO> listarPorMetodo(MetodoPago metodoPago) {
        log.info("Listando pagos por método: {}", metodoPago);
        
        List<Pago> pagos = pagoRepository.findByMetodoPagoOrderByFechaPagoDesc(metodoPago);
        
        return pagos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * RF6.2: Listar pagos en un rango de fechas
     */
    @Transactional(readOnly = true)
    public List<PagoDTO> listarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Listando pagos entre {} y {}", fechaInicio, fechaFin);
        
        if (fechaInicio.isAfter(fechaFin)) {
            throw new ValidacionException("La fecha de inicio no puede ser posterior a la fecha fin");
        }
        
        LocalDateTime inicioDateTime = fechaInicio.atStartOfDay();
        LocalDateTime finDateTime = fechaFin.atTime(23, 59, 59);
        
        List<Pago> pagos = pagoRepository.findByFechaPagoBetweenOrderByFechaPagoDesc(inicioDateTime, finDateTime);
        
        return pagos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * RF6.2: Listar pagos recibidos por un usuario
     */
    @Transactional(readOnly = true)
    public List<PagoDTO> listarPorUsuario(Long usuarioId) {
        log.info("Listando pagos recibidos por usuario: {}", usuarioId);
        
        List<Pago> pagos = pagoRepository.findByRecibidoPorIdOrderByFechaPagoDesc(usuarioId);
        
        return pagos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * RF6.4: Calcular total de pagos en un período
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPagos(LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Calculando total de pagos entre {} y {}", fechaInicio, fechaFin);
        
        LocalDateTime inicioDateTime = fechaInicio.atStartOfDay();
        LocalDateTime finDateTime = fechaFin.atTime(23, 59, 59);
        
        return pagoRepository.calcularTotalPagos(inicioDateTime, finDateTime);
    }

    /**
     * RF6.4: Calcular total por método de pago en un período
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPorMetodo(MetodoPago metodoPago, 
                                             LocalDate fechaInicio, 
                                             LocalDate fechaFin) {
        log.info("Calculando total por método {} entre {} y {}", metodoPago, fechaInicio, fechaFin);
        
        LocalDateTime inicioDateTime = fechaInicio.atStartOfDay();
        LocalDateTime finDateTime = fechaFin.atTime(23, 59, 59);
        
        return pagoRepository.calcularTotalPorMetodo(metodoPago, inicioDateTime, finDateTime);
    }

    /**
     * RF6.2: Anular un pago
     */
    @Transactional
    public void anularPago(Long id, String motivo) {
        log.info("Anulando pago: {}", id);
        
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago", "id", id));
        
        // Validar que el pago no sea muy antiguo (ejemplo: máximo 30 días)
        if (pago.getFechaPago().plusDays(30).isBefore(LocalDateTime.now())) {
            throw new ValidacionException("No se puede anular un pago con más de 30 días de antigüedad");
        }
        
        // Marcar como inactivo
        pago.setActivo(false);
        pago.setObservaciones(pago.getObservaciones() + "\nANULADO: " + motivo);
        pagoRepository.save(pago);
        
        // Actualizar factura
        Factura factura = pago.getFactura();
        BigDecimal nuevoTotalPagado = factura.getTotalPagado().subtract(pago.getMonto());
        factura.setTotalPagado(nuevoTotalPagado);
        factura.setSaldoPendiente(factura.getTotal().subtract(nuevoTotalPagado));
        
        // Actualizar estado de la factura
        if (factura.getSaldoPendiente().compareTo(factura.getTotal()) == 0) {
            factura.setEstado(com.nuclearvet.modulos.administrativo.entity.EstadoFactura.PENDIENTE);
        } else if (factura.getSaldoPendiente().compareTo(BigDecimal.ZERO) > 0) {
            factura.setEstado(com.nuclearvet.modulos.administrativo.entity.EstadoFactura.PARCIAL);
        }
        
        facturaRepository.save(factura);
        
        log.info("Pago {} anulado exitosamente", pago.getNumeroRecibo());
    }

    /**
     * Genera un número único de recibo
     */
    private String generarNumeroRecibo() {
        String prefijo = "REC-";
        String anio = String.valueOf(LocalDate.now().getYear());
        
        Long ultimoNumero = pagoRepository.findLastPago()
                .map(p -> {
                    String numero = p.getNumeroRecibo();
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
     * Convierte un Pago a PagoDTO con todos los datos necesarios
     */
    private PagoDTO convertirADTO(Pago pago) {
        PagoDTO dto = pagoMapper.toDTO(pago);
        
        // Mapear campos manualmente que el mapper ignora
        // Nota: No podemos usar getId() porque EntidadBase no lo expone
        // El mapper automáticamente mapeará los campos básicos
        
        if (pago.getRecibidoPor() != null) {
            dto.setRecibidoPorNombre(pago.getRecibidoPor().getNombre() + " " + 
                                     pago.getRecibidoPor().getApellido());
        }
        
        dto.setFechaCreacion(pago.getFechaCreacion());
        dto.setActivo(pago.getActivo());
        
        return dto;
    }
}
