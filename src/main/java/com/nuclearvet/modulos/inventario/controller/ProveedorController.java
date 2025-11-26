package com.nuclearvet.modulos.inventario.controller;

import com.nuclearvet.common.dto.RespuestaExitosa;
import com.nuclearvet.modulos.inventario.dto.CrearProveedorDTO;
import com.nuclearvet.modulos.inventario.dto.ProveedorDTO;
import com.nuclearvet.modulos.inventario.service.ProveedorService;
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
 * Controlador REST para gestión de proveedores.
 * RF4.2 - Gestión de proveedores
 */
@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
@Tag(name = "Proveedores", description = "Endpoints para gestión de proveedores")
@SecurityRequirement(name = "bearerAuth")
public class ProveedorController {

    private final ProveedorService proveedorService;

    /**
     * RF4.2: Crear un nuevo proveedor
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    @Operation(summary = "Crear proveedor", description = "Registra un nuevo proveedor en el sistema")
    public RespuestaExitosa<ProveedorDTO> crearProveedor(@Valid @RequestBody CrearProveedorDTO dto) {
        ProveedorDTO proveedor = proveedorService.crearProveedor(dto);
        return RespuestaExitosa.crear(
            proveedor,
            String.format("Proveedor '%s' registrado exitosamente con NIT %s", 
                proveedor.getNombre(), proveedor.getNit())
        );
    }

    /**
     * RF4.2: Actualizar un proveedor existente
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    @Operation(summary = "Actualizar proveedor", description = "Actualiza la información de un proveedor")
    public RespuestaExitosa<ProveedorDTO> actualizarProveedor(
            @PathVariable Long id,
            @Valid @RequestBody CrearProveedorDTO dto) {
        ProveedorDTO proveedor = proveedorService.actualizarProveedor(id, dto);
        return RespuestaExitosa.crear(
            proveedor,
            String.format("Proveedor '%s' actualizado correctamente", proveedor.getNombre())
        );
    }

    /**
     * Obtener un proveedor por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    @Operation(summary = "Obtener proveedor", description = "Obtiene los detalles de un proveedor por ID")
    public RespuestaExitosa<ProveedorDTO> obtenerProveedor(@PathVariable Long id) {
        ProveedorDTO proveedor = proveedorService.obtenerPorId(id);
        return RespuestaExitosa.crear(
            proveedor,
            "Proveedor encontrado"
        );
    }

    /**
     * Obtener un proveedor por NIT
     */
    @GetMapping("/nit/{nit}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    @Operation(summary = "Buscar por NIT", description = "Busca un proveedor por su número de NIT")
    public RespuestaExitosa<ProveedorDTO> obtenerPorNit(@PathVariable String nit) {
        ProveedorDTO proveedor = proveedorService.obtenerPorNit(nit);
        return RespuestaExitosa.crear(
            proveedor,
            String.format("Proveedor con NIT %s encontrado", nit)
        );
    }

    /**
     * RF4.2: Listar todos los proveedores activos
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    @Operation(summary = "Listar proveedores", description = "Lista todos los proveedores activos")
    public RespuestaExitosa<List<ProveedorDTO>> listarProveedores() {
        List<ProveedorDTO> proveedores = proveedorService.listarTodos();
        String mensaje = proveedores.isEmpty()
            ? "No hay proveedores registrados"
            : String.format("Se encontraron %d proveedores activos", proveedores.size());
        return RespuestaExitosa.crear(proveedores, mensaje);
    }

    /**
     * Buscar proveedores por nombre
     */
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    @Operation(summary = "Buscar por nombre", description = "Busca proveedores por nombre (búsqueda parcial)")
    public RespuestaExitosa<List<ProveedorDTO>> buscarPorNombre(@RequestParam String nombre) {
        List<ProveedorDTO> proveedores = proveedorService.buscarPorNombre(nombre);
        String mensaje = proveedores.isEmpty()
            ? String.format("No se encontraron proveedores con '%s' en el nombre", nombre)
            : String.format("Se encontraron %d proveedores con '%s'", proveedores.size(), nombre);
        return RespuestaExitosa.crear(proveedores, mensaje);
    }

    /**
     * Listar proveedores por calificación mínima
     */
    @GetMapping("/calificacion/{minima}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    @Operation(summary = "Filtrar por calificación", description = "Lista proveedores con calificación igual o superior a la especificada")
    public RespuestaExitosa<List<ProveedorDTO>> listarPorCalificacion(@PathVariable Integer minima) {
        List<ProveedorDTO> proveedores = proveedorService.listarPorCalificacion(minima);
        String mensaje = proveedores.isEmpty()
            ? String.format("No hay proveedores con %d o más estrellas", minima)
            : String.format("Se encontraron %d proveedores con %d+ estrellas", proveedores.size(), minima);
        return RespuestaExitosa.crear(proveedores, mensaje);
    }

    /**
     * Actualizar calificación de un proveedor
     */
    @PatchMapping("/{id}/calificacion")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    @Operation(summary = "Actualizar calificación", description = "Actualiza la calificación de un proveedor (1-5 estrellas)")
    public RespuestaExitosa<ProveedorDTO> actualizarCalificacion(
            @PathVariable Long id,
            @RequestParam Integer calificacion) {
        ProveedorDTO proveedor = proveedorService.actualizarCalificacion(id, calificacion);
        return RespuestaExitosa.crear(
            proveedor,
            String.format("Calificación actualizada a %d estrellas para '%s'", 
                calificacion, proveedor.getNombre())
        );
    }

    /**
     * RF4.2: Desactivar un proveedor
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desactivar proveedor", description = "Desactiva un proveedor del sistema")
    public RespuestaExitosa<Void> desactivarProveedor(@PathVariable Long id) {
        proveedorService.desactivarProveedor(id);
        return RespuestaExitosa.crear(
            null,
            "Proveedor desactivado exitosamente"
        );
    }
}
