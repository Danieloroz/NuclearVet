package com.nuclearvet.modulos.administrativo.dto;

import com.nuclearvet.modulos.administrativo.entity.TipoItemFactura;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para items de factura.
 * RF6.1 - Gestión de facturación
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemFacturaDTO {
    
    private Long id;
    private TipoItemFactura tipo;
    private String descripcion;
    
    // Producto asociado (si aplica)
    private Long productoId;
    private String productoNombre;
    
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private String observaciones;
}
