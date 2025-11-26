package com.nuclearvet.modulos.inventario.controller;

import com.nuclearvet.common.dto.RespuestaExitosa;
import com.nuclearvet.modulos.inventario.dto.CategoriaDTO;
import com.nuclearvet.modulos.inventario.dto.CrearCategoriaDTO;
import com.nuclearvet.modulos.inventario.entity.TipoCategoria;
import com.nuclearvet.modulos.inventario.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de categorías de productos.
 * RF4.1 - Gestión de categorías
 */
@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "Endpoints para gestión de categorías de productos")
@SecurityRequirement(name = "bearerAuth")
public class CategoriaController {

    private final CategoriaService categoriaService;

    /**
     * RF4.1: Crear una nueva categoría
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    @Operation(summary = "Crear categoría", description = "Crea una nueva categoría de productos")
    public RespuestaExitosa<CategoriaDTO> crearCategoria(@Valid @RequestBody CrearCategoriaDTO dto) {
        CategoriaDTO categoria = categoriaService.crearCategoria(dto);
        return RespuestaExitosa.crear(
            categoria,
            String.format("Categoría '%s' creada exitosamente", categoria.getNombre())
        );
    }

    /**
     * RF4.1: Actualizar una categoría existente
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    @Operation(summary = "Actualizar categoría", description = "Actualiza los datos de una categoría")
    public RespuestaExitosa<CategoriaDTO> actualizarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody CrearCategoriaDTO dto) {
        CategoriaDTO categoria = categoriaService.actualizarCategoria(id, dto);
        return RespuestaExitosa.crear(
            categoria,
            String.format("Categoría '%s' actualizada correctamente", categoria.getNombre())
        );
    }

    /**
     * Obtener una categoría por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    @Operation(summary = "Obtener categoría", description = "Obtiene los detalles de una categoría por ID")
    public RespuestaExitosa<CategoriaDTO> obtenerCategoria(@PathVariable Long id) {
        CategoriaDTO categoria = categoriaService.obtenerPorId(id);
        return RespuestaExitosa.crear(
            categoria,
            "Categoría encontrada"
        );
    }

    /**
     * RF4.1: Listar todas las categorías activas
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    @Operation(summary = "Listar categorías", description = "Lista todas las categorías activas")
    public RespuestaExitosa<List<CategoriaDTO>> listarCategorias() {
        List<CategoriaDTO> categorias = categoriaService.listarTodas();
        String mensaje = categorias.isEmpty() 
            ? "No hay categorías registradas aún" 
            : String.format("Se encontraron %d categorías", categorias.size());
        return RespuestaExitosa.crear(categorias, mensaje);
    }

    /**
     * RF4.1: Listar categorías por tipo
     */
    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    @Operation(summary = "Listar por tipo", description = "Lista categorías filtradas por tipo (MEDICAMENTO, ALIMENTO, etc.)")
    public RespuestaExitosa<List<CategoriaDTO>> listarPorTipo(@PathVariable TipoCategoria tipo) {
        List<CategoriaDTO> categorias = categoriaService.listarPorTipo(tipo);
        String mensaje = categorias.isEmpty()
            ? String.format("No hay categorías de tipo %s", tipo)
            : String.format("Se encontraron %d categorías de tipo %s", categorias.size(), tipo);
        return RespuestaExitosa.crear(categorias, mensaje);
    }

    /**
     * RF4.1: Desactivar una categoría
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desactivar categoría", description = "Desactiva una categoría (solo si no tiene productos activos)")
    public RespuestaExitosa<Void> desactivarCategoria(@PathVariable Long id) {
        categoriaService.desactivarCategoria(id);
        return RespuestaExitosa.crear(
            null,
            "Categoría desactivada exitosamente"
        );
    }
}
