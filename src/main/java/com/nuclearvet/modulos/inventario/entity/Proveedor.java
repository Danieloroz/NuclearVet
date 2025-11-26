package com.nuclearvet.modulos.inventario.entity;

import com.nuclearvet.common.entity.EntidadBase;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad para proveedores de productos.
 * RF4.2 - Gestión de proveedores
 */
@Entity
@Table(name = "proveedores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Proveedor extends EntidadBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(name = "nombre_contacto", length = 150)
    private String nombreContacto;

    @Column(nullable = false, unique = true, length = 50)
    private String nit;

    @Column(length = 20)
    private String telefono;

    @Column(length = 150)
    private String email;

    @Column(length = 300)
    private String direccion;

    @Column(length = 100)
    private String ciudad;

    @Column(length = 100)
    private String pais;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "dias_credito")
    private Integer diasCredito; // Días de crédito que ofrece el proveedor

    @Column(name = "calificacion")
    private Integer calificacion; // 1-5 estrellas
}
