package com.nuclearvet.modulos.administrativo.dto;

import com.nuclearvet.modulos.administrativo.entity.EstadoFactura;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para visualizar facturas.
 * RF6.1 - Gestión de facturación
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDTO {
    
    private Long id;
    private String numeroFactura;
    
    // Información del paciente y propietario
    private Long pacienteId;
    private String pacienteNombre;
    private Long propietarioId;
    private String propietarioNombre;
    
    // Información de la consulta (opcional)
    private Long consultaId;
    
    // Fechas
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    
    // Estado y totales
    private EstadoFactura estado;
    private BigDecimal subtotal;
    private BigDecimal porcentajeImpuesto;
    private BigDecimal valorImpuesto;
    private BigDecimal descuento;
    private BigDecimal total;
    private BigDecimal totalPagado;
    private BigDecimal saldoPendiente;
    
    // Items de la factura
    @Builder.Default
    private List<ItemFacturaDTO> items = new ArrayList<>();
    
    // Pagos realizados
    @Builder.Default
    private List<PagoDTO> pagos = new ArrayList<>();
    
    private String observaciones;
    
    // Usuario que emitió
    private Long emitidaPorId;
    private String emitidaPorNombre;
    
    // Metadata
    private LocalDate fechaCreacion;
    private Boolean activo;
}
