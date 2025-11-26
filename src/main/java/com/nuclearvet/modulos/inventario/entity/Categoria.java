package com.nuclearvet.modulos.inventario.entity;

import com.nuclearvet.common.entity.EntidadBase;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad para categorías de productos.
 * RF4.1 - Categorización de productos
 */
@Entity
@Table(name = "categorias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Categoria extends EntidadBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_categoria", length = 50)
    private TipoCategoria tipoCategoria; // MEDICAMENTO, ALIMENTO, ACCESORIO, EQUIPAMIENTO, SERVICIO
}
