package com.nuclearvet.modulos.administrativo.dto;

import com.nuclearvet.modulos.administrativo.entity.TipoItemFactura;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para crear una nueva factura.
 * RF6.1 - Gestión de facturación
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearFacturaDTO {
    
    @NotNull(message = "El ID del paciente es obligatorio")
    private Long pacienteId;
    
    @NotNull(message = "El ID del propietario es obligatorio")
    private Long propietarioId;
    
    private Long consultaId; // Opcional
    
    @NotNull(message = "La fecha de emisión es obligatoria")
    private LocalDate fechaEmision;
    
    @Future(message = "La fecha de vencimiento debe ser futura")
    private LocalDate fechaVencimiento;
    
    @DecimalMin(value = "0.0", message = "El porcentaje de impuesto no puede ser negativo")
    @DecimalMax(value = "100.0", message = "El porcentaje de impuesto no puede ser mayor a 100")
    private BigDecimal porcentajeImpuesto;
    
    @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
    private BigDecimal descuento;
    
    @NotEmpty(message = "La factura debe tener al menos un item")
    @Builder.Default
    private List<CrearItemFacturaDTO> items = new ArrayList<>();
    
    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;
    
    @NotNull(message = "El ID del usuario que emite es obligatorio")
    private Long emitidaPorId;
    
    /**
     * DTO interno para crear items de factura
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CrearItemFacturaDTO {
        
        @NotNull(message = "El tipo de item es obligatorio")
        private TipoItemFactura tipo;
        
        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
        private String descripcion;
        
        private Long productoId; // Opcional, solo si es un producto
        
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        private Integer cantidad;
        
        @NotNull(message = "El precio unitario es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor a 0")
        private BigDecimal precioUnitario;
        
        @Size(max = 300, message = "Las observaciones no pueden exceder 300 caracteres")
        private String observaciones;
    }
}
