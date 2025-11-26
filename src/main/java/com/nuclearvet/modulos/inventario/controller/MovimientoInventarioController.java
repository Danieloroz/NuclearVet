package com.nuclearvet.modulos.inventario.controller;

import com.nuclearvet.common.dto.RespuestaExitosa;
import com.nuclearvet.modulos.inventario.dto.MovimientoInventarioDTO;
import com.nuclearvet.modulos.inventario.dto.RegistrarMovimientoDTO;
import com.nuclearvet.modulos.inventario.service.MovimientoInventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para gestión de movimientos de inventario.
 * RF4.4 - Control de entradas y salidas
 */
@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
@Tag(name = "Movimientos de Inventario", description = "Endpoints para registro y consulta de movimientos de inventario")
@SecurityRequirement(name = "bearerAuth")
public class MovimientoInventarioController {

    private final MovimientoInventarioService movimientoService;

    /**
     * RF4.4: Registrar un movimiento de inventario
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    @Operation(summary = "Registrar movimiento", description = "Registra un movimiento de inventario (ENTRADA, SALIDA, AJUSTE, DEVOLUCION)")
    public RespuestaExitosa<MovimientoInventarioDTO> registrarMovimiento(@Valid @RequestBody RegistrarMovimientoDTO dto) {
        MovimientoInventarioDTO movimiento = movimientoService.registrarMovimiento(dto);
        String mensaje = generarMensajeMovimiento(movimiento);
        return RespuestaExitosa.crear(movimiento, mensaje);
    }

    /**
     * Obtener un movimiento por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    @Operation(summary = "Obtener movimiento", description = "Obtiene los detalles de un movimiento por ID")
    public RespuestaExitosa<MovimientoInventarioDTO> obtenerMovimiento(@PathVariable Long id) {
        MovimientoInventarioDTO movimiento = movimientoService.obtenerPorId(id);
        return RespuestaExitosa.crear(
            movimiento,
            "Movimiento encontrado"
        );
    }

    /**
     * RF4.4: Listar movimientos de un producto
     */
    @GetMapping("/producto/{productoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    @Operation(summary = "Historial de producto", description = "Lista todos los movimientos de un producto específico")
    public RespuestaExitosa<List<MovimientoInventarioDTO>> listarPorProducto(@PathVariable Long productoId) {
        List<MovimientoInventarioDTO> movimientos = movimientoService.listarPorProducto(productoId);
        String mensaje = movimientos.isEmpty()
            ? "Este producto no tiene movimientos registrados"
            : String.format("Se encontraron %d movimientos para este producto", movimientos.size());
        return RespuestaExitosa.crear(movimientos, mensaje);
    }

    /**
     * RF4.4: Listar movimientos por tipo
     */
    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    @Operation(summary = "Listar por tipo", description = "Lista movimientos filtrados por tipo (ENTRADA, SALIDA, AJUSTE, DEVOLUCION)")
    public RespuestaExitosa<List<MovimientoInventarioDTO>> listarPorTipo(@PathVariable String tipo) {
        List<MovimientoInventarioDTO> movimientos = movimientoService.listarPorTipo(tipo);
        String mensaje = movimientos.isEmpty()
            ? String.format("No hay movimientos de tipo %s", tipo)
            : String.format("Se encontraron %d movimientos de tipo %s", movimientos.size(), tipo);
        return RespuestaExitosa.crear(movimientos, mensaje);
    }

    /**
     * RF4.4: Listar movimientos en rango de fechas
     */
    @GetMapping("/reporte")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    @Operation(summary = "Reporte por fechas", description = "Genera reporte de movimientos en un rango de fechas")
    public RespuestaExitosa<List<MovimientoInventarioDTO>> reportePorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        List<MovimientoInventarioDTO> movimientos = movimientoService.listarPorRangoFechas(fechaInicio, fechaFin);
        String mensaje = movimientos.isEmpty()
            ? "No hay movimientos en el rango de fechas especificado"
            : String.format("Se encontraron %d movimientos entre %s y %s", 
                movimientos.size(), 
                fechaInicio.toLocalDate(), 
                fechaFin.toLocalDate());
        return RespuestaExitosa.crear(movimientos, mensaje);
    }

    /**
     * Listar movimientos del día actual
     */
    @GetMapping("/hoy")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    @Operation(summary = "Movimientos de hoy", description = "Lista todos los movimientos registrados el día de hoy")
    public RespuestaExitosa<List<MovimientoInventarioDTO>> listarMovimientosHoy() {
        List<MovimientoInventarioDTO> movimientos = movimientoService.listarMovimientosHoy();
        String mensaje = movimientos.isEmpty()
            ? "No hay movimientos registrados hoy"
            : String.format("Se registraron %d movimientos hoy", movimientos.size());
        return RespuestaExitosa.crear(movimientos, mensaje);
    }

    /**
     * Listar últimos movimientos (dashboard)
     */
    @GetMapping("/recientes")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    @Operation(summary = "Movimientos recientes", description = "Lista los movimientos más recientes para el dashboard")
    public RespuestaExitosa<List<MovimientoInventarioDTO>> listarRecientes(
            @RequestParam(defaultValue = "10") int limite) {
        List<MovimientoInventarioDTO> movimientos = movimientoService.listarUltimosMovimientos(limite);
        String mensaje = String.format("Últimos %d movimientos", movimientos.size());
        return RespuestaExitosa.crear(movimientos, mensaje);
    }

    /**
     * Contar movimientos de un producto
     */
    @GetMapping("/producto/{productoId}/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')")
    @Operation(summary = "Contar movimientos", description = "Cuenta el total de movimientos de un producto")
    public RespuestaExitosa<Long> contarMovimientos(@PathVariable Long productoId) {
        Long total = movimientoService.contarMovimientosPorProducto(productoId);
        String mensaje = total == 0
            ? "Este producto no tiene movimientos"
            : String.format("Este producto tiene %d movimientos registrados", total);
        return RespuestaExitosa.crear(total, mensaje);
    }

    /**
     * Genera un mensaje descriptivo según el tipo de movimiento
     */
    private String generarMensajeMovimiento(MovimientoInventarioDTO mov) {
        return switch (mov.getTipoMovimiento()) {
            case "ENTRADA" -> String.format(
                "Entrada registrada: +%d unidades de '%s'. Stock: %d → %d",
                mov.getCantidad(), mov.getProductoNombre(), mov.getStockAnterior(), mov.getStockNuevo()
            );
            case "SALIDA" -> String.format(
                "Salida registrada: -%d unidades de '%s'. Stock: %d → %d",
                mov.getCantidad(), mov.getProductoNombre(), mov.getStockAnterior(), mov.getStockNuevo()
            );
            case "AJUSTE" -> String.format(
                "Ajuste de inventario: '%s' ajustado de %d a %d unidades",
                mov.getProductoNombre(), mov.getStockAnterior(), mov.getStockNuevo()
            );
            case "DEVOLUCION" -> String.format(
                "Devolución registrada: +%d unidades de '%s'. Stock: %d → %d",
                mov.getCantidad(), mov.getProductoNombre(), mov.getStockAnterior(), mov.getStockNuevo()
            );
            default -> "Movimiento registrado exitosamente";
        };
    }
}
