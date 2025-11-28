package com.nuclearvet.modulos.administrativo.controller;

import com.nuclearvet.modulos.administrativo.dto.PagoDTO;
import com.nuclearvet.modulos.administrativo.dto.RegistrarPagoDTO;
import com.nuclearvet.modulos.administrativo.entity.MetodoPago;
import com.nuclearvet.modulos.administrativo.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para la gestión de pagos.
 * Expone endpoints para registrar, consultar y anular pagos,
 * así como generar reportes de pagos.
 * 
 * @author NuclearVet Team
 * @version 1.0
 * @since 2025-01-28
 */
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "API para la gestión de pagos de facturas")
public class PagoController {

    private final PagoService pagoService;

    /**
     * Registra un nuevo pago para una factura.
     * RF6.2: Gestión de pagos - Registrar pagos
     * 
     * @param dto Datos del pago a registrar
     * @return El pago registrado
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @Operation(summary = "Registrar un nuevo pago", description = "Registra un pago y actualiza el estado de la factura")
    public ResponseEntity<PagoDTO> registrarPago(@Valid @RequestBody RegistrarPagoDTO dto) {
        PagoDTO pago = pagoService.registrarPago(dto);
        return new ResponseEntity<>(pago, HttpStatus.CREATED);
    }

    /**
     * Obtiene un pago por su ID.
     * 
     * @param id ID del pago
     * @return El pago encontrado
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @Operation(summary = "Obtener pago por ID", description = "Retorna un pago específico por su ID")
    public ResponseEntity<PagoDTO> obtenerPorId(@PathVariable Long id) {
        PagoDTO pago = pagoService.obtenerPorId(id);
        return ResponseEntity.ok(pago);
    }

    /**
     * Obtiene un pago por su número de recibo.
     * 
     * @param numeroRecibo Número del recibo (ej: REC-2025-000001)
     * @return El pago encontrado
     */
    @GetMapping("/recibo/{numeroRecibo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @Operation(summary = "Obtener pago por número de recibo", description = "Retorna un pago por su número único de recibo")
    public ResponseEntity<PagoDTO> obtenerPorNumeroRecibo(@PathVariable String numeroRecibo) {
        PagoDTO pago = pagoService.obtenerPorNumeroRecibo(numeroRecibo);
        return ResponseEntity.ok(pago);
    }

    /**
     * Lista todos los pagos de una factura.
     * RF6.2: Consultar historial de pagos de una factura
     * 
     * @param facturaId ID de la factura
     * @return Lista de pagos de la factura
     */
    @GetMapping("/factura/{facturaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @Operation(summary = "Listar pagos por factura", description = "Retorna todos los pagos realizados a una factura específica")
    public ResponseEntity<List<PagoDTO>> listarPorFactura(@PathVariable Long facturaId) {
        List<PagoDTO> pagos = pagoService.listarPorFactura(facturaId);
        return ResponseEntity.ok(pagos);
    }

    /**
     * Lista todos los pagos realizados con un método específico.
     * RF6.4: Reportes - Pagos por método
     * 
     * @param metodoPago Método de pago
     * @return Lista de pagos con el método especificado
     */
    @GetMapping("/metodo/{metodoPago}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @Operation(summary = "Listar pagos por método", description = "Retorna todos los pagos realizados con un método específico")
    public ResponseEntity<List<PagoDTO>> listarPorMetodo(@PathVariable MetodoPago metodoPago) {
        List<PagoDTO> pagos = pagoService.listarPorMetodo(metodoPago);
        return ResponseEntity.ok(pagos);
    }

    /**
     * Lista pagos realizados en un rango de fechas.
     * RF6.4: Reportes - Pagos por período
     * 
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de pagos en el rango
     */
    @GetMapping("/rango")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @Operation(summary = "Listar pagos por rango de fechas", description = "Retorna todos los pagos realizados en un período")
    public ResponseEntity<List<PagoDTO>> listarPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<PagoDTO> pagos = pagoService.listarPorRangoFechas(fechaInicio, fechaFin);
        return ResponseEntity.ok(pagos);
    }

    /**
     * Lista todos los pagos recibidos por un usuario.
     * 
     * @param usuarioId ID del usuario que recibió los pagos
     * @return Lista de pagos recibidos por el usuario
     */
    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    @Operation(summary = "Listar pagos por usuario", description = "Retorna todos los pagos recibidos por un usuario específico")
    public ResponseEntity<List<PagoDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<PagoDTO> pagos = pagoService.listarPorUsuario(usuarioId);
        return ResponseEntity.ok(pagos);
    }

    /**
     * Anula un pago registrado.
     * RF6.2: Gestión de pagos - Anular pagos
     * Solo se pueden anular pagos con menos de 30 días de antigüedad.
     * 
     * @param id ID del pago a anular
     * @param motivo Motivo de la anulación
     * @return Respuesta exitosa
     */
    @PostMapping("/{id}/anular")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    @Operation(summary = "Anular un pago", description = "Anula un pago y revierte el estado de la factura (máximo 30 días)")
    public ResponseEntity<String> anularPago(
            @PathVariable Long id,
            @RequestParam String motivo) {
        pagoService.anularPago(id, motivo);
        return ResponseEntity.ok("Pago anulado exitosamente");
    }

    /**
     * Calcula el total de pagos en un período.
     * RF6.4: Reportes financieros - Total de pagos
     * 
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @return Total de pagos en el período
     */
    @GetMapping("/reportes/total")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    @Operation(summary = "Calcular total de pagos", description = "Retorna el total de pagos recibidos en un período")
    public ResponseEntity<BigDecimal> calcularTotalPagos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        BigDecimal total = pagoService.calcularTotalPagos(fechaInicio, fechaFin);
        return ResponseEntity.ok(total);
    }

    /**
     * Calcula el total de pagos por método en un período.
     * RF6.4: Reportes financieros - Pagos por método de pago
     * 
     * @param metodoPago Método de pago a consultar
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @return Total de pagos con el método especificado
     */
    @GetMapping("/reportes/por-metodo")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    @Operation(summary = "Calcular total por método de pago", description = "Retorna el total de pagos con un método específico en un período")
    public ResponseEntity<BigDecimal> calcularTotalPorMetodo(
            @RequestParam MetodoPago metodoPago,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        BigDecimal total = pagoService.calcularTotalPorMetodo(metodoPago, fechaInicio, fechaFin);
        return ResponseEntity.ok(total);
    }
}
