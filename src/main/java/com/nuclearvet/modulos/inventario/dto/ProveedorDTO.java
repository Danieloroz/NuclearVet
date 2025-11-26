package com.nuclearvet.modulos.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para Proveedor.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorDTO {
    
    private Long id;
    private String nombre;
    private String nombreContacto;
    private String nit;
    private String telefono;
    private String email;
    private String direccion;
    private String ciudad;
    private String pais;
    private String observaciones;
    private Integer diasCredito;
    private Integer calificacion;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}
