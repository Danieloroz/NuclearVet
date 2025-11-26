package com.nuclearvet.modulos.inventario.controller;

import com.nuclearvet.common.dto.RespuestaExitosa;
import com.nuclearvet.modulos.inventario.dto.CrearProductoDTO;
import com.nuclearvet.modulos.inventario.dto.ProductoDTO;
import com.nuclearvet.modulos.inventario.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de productos e inventario.
 * Implementa RF4.1 a RF4.5
 */
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Endpoints para gestión de productos e inventario")
@SecurityRequirement(name = "Bearer Authentication")
public class ProductoController {

    private final ProductoService productoService;

    /**
     * RF4.1: Crear un nuevo producto
     */
    @Operation(
            summary = "Crear nuevo producto",
            description = "Registra un nuevo producto en el inventario con validaciones de código único y precios."
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<RespuestaExitosa<ProductoDTO>> crearProducto(
            @Valid @RequestBody CrearProductoDTO dto) {

        ProductoDTO producto = productoService.crearProducto(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(RespuestaExitosa.crear(producto, "Producto creado exitosamente, parce"));
    }

    /**
     * RF4.1: Actualizar un producto
     */
    @Operation(
            summary = "Actualizar producto",
            description = "Actualiza los datos de un producto existente."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<RespuestaExitosa<ProductoDTO>> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody CrearProductoDTO dto) {

        ProductoDTO producto = productoService.actualizarProducto(id, dto);
        return ResponseEntity.ok(RespuestaExitosa.crear(producto, "Producto actualizado correctamente"));
    }

    /**
     * RF4.1: Obtener producto por ID
     */
    @Operation(summary = "Obtener producto por ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<RespuestaExitosa<ProductoDTO>> obtenerPorId(@PathVariable Long id) {
        ProductoDTO producto = productoService.obtenerPorId(id);
        return ResponseEntity.ok(RespuestaExitosa.crear(producto));
    }

    /**
     * RF4.1: Obtener producto por código
     */
    @Operation(summary = "Obtener producto por código SKU")
    @GetMapping("/codigo/{codigo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<RespuestaExitosa<ProductoDTO>> obtenerPorCodigo(@PathVariable String codigo) {
        ProductoDTO producto = productoService.obtenerPorCodigo(codigo);
        return ResponseEntity.ok(RespuestaExitosa.crear(producto));
    }

    /**
     * RF4.1: Listar todos los productos
     */
    @Operation(
            summary = "Listar todos los productos",
            description = "Obtiene la lista completa de productos activos ordenados por nombre."
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<RespuestaExitosa<List<ProductoDTO>>> listarTodos() {
        List<ProductoDTO> productos = productoService.listarTodos();
        return ResponseEntity.ok(RespuestaExitosa.crear(productos));
    }

    /**
     * RF4.1: Listar productos por categoría
     */
    @Operation(summary = "Listar productos por categoría")
    @GetMapping("/categoria/{categoriaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<RespuestaExitosa<List<ProductoDTO>>> listarPorCategoria(
            @PathVariable Long categoriaId) {
        List<ProductoDTO> productos = productoService.listarPorCategoria(categoriaId);
        return ResponseEntity.ok(RespuestaExitosa.crear(productos));
    }

    /**
     * RF4.2: Listar productos por proveedor
     */
    @Operation(summary = "Listar productos por proveedor")
    @GetMapping("/proveedor/{proveedorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<RespuestaExitosa<List<ProductoDTO>>> listarPorProveedor(
            @PathVariable Long proveedorId) {
        List<ProductoDTO> productos = productoService.listarPorProveedor(proveedorId);
        return ResponseEntity.ok(RespuestaExitosa.crear(productos));
    }

    /**
     * RF4.3: Obtener productos con bajo stock
     */
    @Operation(
            summary = "Productos con bajo stock",
            description = "Lista los productos cuyo stock actual es igual o menor al stock mínimo configurado."
    )
    @GetMapping("/alertas/bajo-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<RespuestaExitosa<List<ProductoDTO>>> productosConBajoStock() {
        List<ProductoDTO> productos = productoService.obtenerProductosConBajoStock();
        String mensaje = productos.isEmpty() 
                ? "No hay productos con bajo stock" 
                : String.format("Hay %d productos con bajo stock que necesitan reposición", productos.size());
        return ResponseEntity.ok(RespuestaExitosa.crear(productos, mensaje));
    }

    /**
     * RF4.3: Obtener productos próximos a vencer
     */
    @Operation(
            summary = "Productos próximos a vencer",
            description = "Lista los productos que vencen en los próximos 30 días."
    )
    @GetMapping("/alertas/proximo-vencer")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<RespuestaExitosa<List<ProductoDTO>>> productosProximosAVencer() {
        List<ProductoDTO> productos = productoService.obtenerProductosProximosAVencer();
        String mensaje = productos.isEmpty() 
                ? "No hay productos próximos a vencer" 
                : String.format("Hay %d productos próximos a vencer", productos.size());
        return ResponseEntity.ok(RespuestaExitosa.crear(productos, mensaje));
    }

    /**
     * RF4.3: Obtener productos vencidos
     */
    @Operation(
            summary = "Productos vencidos",
            description = "Lista los productos que ya han vencido y deben ser retirados del inventario."
    )
    @GetMapping("/alertas/vencidos")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<RespuestaExitosa<List<ProductoDTO>>> productosVencidos() {
        List<ProductoDTO> productos = productoService.obtenerProductosVencidos();
        String mensaje = productos.isEmpty() 
                ? "No hay productos vencidos" 
                : String.format("¡Cuidado! Hay %d productos vencidos", productos.size());
        return ResponseEntity.ok(RespuestaExitosa.crear(productos, mensaje));
    }

    /**
     * Buscar productos por nombre
     */
    @Operation(summary = "Buscar productos por nombre")
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<RespuestaExitosa<List<ProductoDTO>>> buscarPorNombre(
            @RequestParam String nombre) {
        List<ProductoDTO> productos = productoService.buscarPorNombre(nombre);
        return ResponseEntity.ok(RespuestaExitosa.crear(productos));
    }

    /**
     * Desactivar un producto
     */
    @Operation(summary = "Desactivar producto")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RespuestaExitosa<Void>> desactivarProducto(@PathVariable Long id) {
        productoService.desactivarProducto(id);
        return ResponseEntity.ok(RespuestaExitosa.crear(null, "Producto desactivado correctamente"));
    }

    /**
     * Ajustar stock manualmente
     */
    @Operation(
            summary = "Ajustar stock",
            description = "Ajusta manualmente el stock de un producto. Para movimientos normales usar el endpoint de movimientos."
    )
    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<RespuestaExitosa<ProductoDTO>> ajustarStock(
            @PathVariable Long id,
            @RequestParam Integer nuevoStock) {
        ProductoDTO producto = productoService.ajustarStock(id, nuevoStock);
        return ResponseEntity.ok(RespuestaExitosa.crear(producto, "Stock ajustado correctamente"));
    }

    /**
     * Obtener resumen de alertas de inventario
     */
    @Operation(
            summary = "Resumen de alertas",
            description = "Obtiene un resumen consolidado de todas las alertas del inventario."
    )
    @GetMapping("/alertas/resumen")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    public ResponseEntity<RespuestaExitosa<Map<String, Object>>> resumenAlertas() {
        List<ProductoDTO> bajoStock = productoService.obtenerProductosConBajoStock();
        List<ProductoDTO> proximosVencer = productoService.obtenerProductosProximosAVencer();
        List<ProductoDTO> vencidos = productoService.obtenerProductosVencidos();

        Map<String, Object> resumen = new HashMap<>();
        resumen.put("productosBajoStock", bajoStock.size());
        resumen.put("productosProximosVencer", proximosVencer.size());
        resumen.put("productosVencidos", vencidos.size());
        resumen.put("totalAlertas", bajoStock.size() + proximosVencer.size() + vencidos.size());

        String mensaje = "Resumen de alertas del inventario";
        return ResponseEntity.ok(RespuestaExitosa.crear(resumen, mensaje));
    }
}
