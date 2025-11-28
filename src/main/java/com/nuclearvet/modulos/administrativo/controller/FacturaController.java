package com.nuclearvet.modulos.administrativo.controller;

import com.nuclearvet.modulos.administrativo.dto.FacturaDTO;
import com.nuclearvet.modulos.administrativo.dto.CrearFacturaDTO;
import com.nuclearvet.modulos.administrativo.entity.EstadoFactura;
import com.nuclearvet.modulos.administrativo.service.FacturaService;
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
 * Controlador REST para la gestión de facturas.
 * Expone endpoints para crear, consultar, listar y cancelar facturas,
 * así como generar reportes de facturación.
 * 
 * @author NuclearVet Team
 * @version 1.0
 * @since 2025-01-28
 */
@RestController
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
@Tag(name = "Facturas", description = "API para la gestión de facturas")
public class FacturaController {

    private final FacturaService facturaService;

    /**
     * Crea una nueva factura.
     * RF6.1: Gestión de facturación - Crear facturas
     * 
     * @param dto Datos de la factura a crear
     * @return La factura creada
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @Operation(summary = "Crear una nueva factura", description = "Crea una factura con sus items asociados")
    public ResponseEntity<FacturaDTO> crearFactura(@Valid @RequestBody CrearFacturaDTO dto) {
        FacturaDTO factura = facturaService.crearFactura(dto);
        return new ResponseEntity<>(factura, HttpStatus.CREATED);
    }

    /**
     * Obtiene una factura por su ID.
     * 
     * @param id ID de la factura
     * @return La factura encontrada
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @Operation(summary = "Obtener factura por ID", description = "Retorna una factura específica por su ID")
    public ResponseEntity<FacturaDTO> obtenerPorId(@PathVariable Long id) {
        FacturaDTO factura = facturaService.obtenerPorId(id);
        return ResponseEntity.ok(factura);
    }

    /**
     * Obtiene una factura por su número.
     * 
     * @param numeroFactura Número de la factura (ej: FAC-2025-000001)
     * @return La factura encontrada
     */
    @GetMapping("/numero/{numeroFactura}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @Operation(summary = "Obtener factura por número", description = "Retorna una factura por su número único")
    public ResponseEntity<FacturaDTO> obtenerPorNumero(@PathVariable String numeroFactura) {
        FacturaDTO factura = facturaService.obtenerPorNumero(numeroFactura);
        return ResponseEntity.ok(factura);
    }

    /**
     * Lista todas las facturas de un cliente (propietario).
     * 
     * @param propietarioId ID del cliente propietario
     * @return Lista de facturas del cliente
     */
    @GetMapping("/cliente/{propietarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @Operation(summary = "Listar facturas por cliente", description = "Retorna todas las facturas de un cliente específico")
    public ResponseEntity<List<FacturaDTO>> listarPorCliente(@PathVariable Long propietarioId) {
        List<FacturaDTO> facturas = facturaService.listarPorCliente(propietarioId);
        return ResponseEntity.ok(facturas);
    }

    /**
     * Lista todas las facturas de un paciente.
     * 
     * @param pacienteId ID del paciente
     * @return Lista de facturas del paciente
     */
    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @Operation(summary = "Listar facturas por paciente", description = "Retorna todas las facturas de un paciente específico")
    public ResponseEntity<List<FacturaDTO>> listarPorPaciente(@PathVariable Long pacienteId) {
        List<FacturaDTO> facturas = facturaService.listarPorPaciente(pacienteId);
        return ResponseEntity.ok(facturas);
    }

    /**
     * Lista todas las facturas de una consulta.
     * RF6.1: Relacionar facturas con consultas
     * 
     * @param consultaId ID de la consulta
     * @return Lista de facturas de la consulta
     */
    @GetMapping("/consulta/{consultaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @Operation(summary = "Listar facturas por consulta", description = "Retorna la factura asociada a una consulta")
    public ResponseEntity<List<FacturaDTO>> listarPorConsulta(@PathVariable Long consultaId) {
        FacturaDTO factura = facturaService.obtenerPorConsulta(consultaId);
        return ResponseEntity.ok(factura != null ? List.of(factura) : List.of());
    }

    /**
     * Lista todas las facturas con un estado específico.
     * RF6.3: Reportes - Facturas por estado
     * 
     * @param estado Estado de las facturas (PENDIENTE, PAGADA, etc.)
     * @return Lista de facturas con el estado especificado
     */
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @Operation(summary = "Listar facturas por estado", description = "Retorna todas las facturas con un estado específico")
    public ResponseEntity<List<FacturaDTO>> listarPorEstado(@PathVariable EstadoFactura estado) {
        List<FacturaDTO> facturas = facturaService.listarPorEstado(estado);
        return ResponseEntity.ok(facturas);
    }

    /**
     * Lista todas las facturas pendientes de pago.
     * RF6.3: Reportes - Facturas pendientes
     * 
     * @return Lista de facturas pendientes
     */
    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @Operation(summary = "Listar facturas pendientes", description = "Retorna todas las facturas con estado PENDIENTE")
    public ResponseEntity<List<FacturaDTO>> listarPendientes() {
        List<FacturaDTO> facturas = facturaService.listarPendientes();
        return ResponseEntity.ok(facturas);
    }

    /**
     * Lista todas las facturas vencidas.
     * RF6.3: Reportes - Facturas vencidas
     * 
     * @return Lista de facturas vencidas
     */
    @GetMapping("/vencidas")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @Operation(summary = "Listar facturas vencidas", description = "Retorna todas las facturas vencidas que no han sido pagadas")
    public ResponseEntity<List<FacturaDTO>> listarVencidas() {
        List<FacturaDTO> facturas = facturaService.listarVencidas();
        return ResponseEntity.ok(facturas);
    }

    /**
     * Lista facturas emitidas en un rango de fechas.
     * RF6.3: Reportes - Facturas por período
     * 
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de facturas en el rango
     */
    @GetMapping("/rango")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @Operation(summary = "Listar facturas por rango de fechas", description = "Retorna todas las facturas emitidas en un período")
    public ResponseEntity<List<FacturaDTO>> listarPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<FacturaDTO> facturas = facturaService.listarPorRangoFechas(fechaInicio, fechaFin);
        return ResponseEntity.ok(facturas);
    }

    /**
     * Cancela una factura.
     * RF6.1: Gestión de facturación - Cancelar facturas
     * 
     * @param id ID de la factura a cancelar
     * @param motivo Motivo de la cancelación
     * @return Respuesta exitosa
     */
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    @Operation(summary = "Cancelar una factura", description = "Cancela una factura que no tiene pagos asociados")
    public ResponseEntity<String> cancelarFactura(
            @PathVariable Long id,
            @RequestParam String motivo) {
        facturaService.cancelarFactura(id, motivo);
        return ResponseEntity.ok("Factura cancelada exitosamente");
    }

    /**
     * Calcula el total facturado en un período.
     * RF6.4: Reportes financieros - Total facturado
     * 
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @return Total facturado en el período
     */
    @GetMapping("/reportes/facturado")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    @Operation(summary = "Calcular total facturado", description = "Retorna el total facturado en un período (excluye facturas canceladas)")
    public ResponseEntity<BigDecimal> calcularTotalFacturado(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        BigDecimal total = facturaService.calcularTotalFacturado(fechaInicio, fechaFin);
        return ResponseEntity.ok(total);
    }

    /**
     * Calcula el total recaudado en un período.
     * RF6.4: Reportes financieros - Total recaudado
     * 
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @return Total recaudado en el período
     */
    @GetMapping("/reportes/recaudado")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    @Operation(summary = "Calcular total recaudado", description = "Retorna el total efectivamente cobrado en un período")
    public ResponseEntity<BigDecimal> calcularTotalRecaudado(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        BigDecimal total = facturaService.calcularTotalRecaudado(fechaInicio, fechaFin);
        return ResponseEntity.ok(total);
    }

    /**
     * Actualiza el estado de las facturas vencidas.
     * RF6.3: Actualización automática de facturas vencidas
     * Este endpoint puede ser llamado por un scheduler o manualmente
     * 
     * @return Número de facturas actualizadas
     */
    @PostMapping("/actualizar-vencidas")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar facturas vencidas", description = "Actualiza el estado de las facturas que han pasado su fecha de vencimiento")
    public ResponseEntity<String> actualizarFacturasVencidas() {
        facturaService.actualizarFacturasVencidas();
        return ResponseEntity.ok("Facturas vencidas actualizadas correctamente");
    }
}
