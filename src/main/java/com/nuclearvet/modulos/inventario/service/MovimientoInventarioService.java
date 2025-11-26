package com.nuclearvet.modulos.inventario.service;

import com.nuclearvet.common.exception.ConflictoException;
import com.nuclearvet.common.exception.RecursoNoEncontradoException;
import com.nuclearvet.modulos.inventario.dto.MovimientoInventarioDTO;
import com.nuclearvet.modulos.inventario.dto.RegistrarMovimientoDTO;
import com.nuclearvet.modulos.inventario.entity.MovimientoInventario;
import com.nuclearvet.modulos.inventario.entity.Producto;
import com.nuclearvet.modulos.inventario.entity.TipoMovimiento;
import com.nuclearvet.modulos.inventario.mapper.MovimientoInventarioMapper;
import com.nuclearvet.modulos.inventario.repository.MovimientoInventarioRepository;
import com.nuclearvet.modulos.inventario.repository.ProductoRepository;
import com.nuclearvet.modulos.inventario.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de movimientos de inventario.
 * RF4.4 - Control de entradas y salidas de medicamentos e insumos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MovimientoInventarioService {

    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;
    private final MovimientoInventarioMapper movimientoMapper;

    /**
     * RF4.4: Registrar un movimiento de inventario (ENTRADA/SALIDA/AJUSTE/DEVOLUCION)
     */
    @Transactional
    public MovimientoInventarioDTO registrarMovimiento(RegistrarMovimientoDTO dto) {
        log.info("Registrando movimiento de tipo {} para producto ID {}", dto.getTipoMovimiento(), dto.getProductoId());

        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", "id", dto.getProductoId()));

        TipoMovimiento tipo = TipoMovimiento.valueOf(dto.getTipoMovimiento());
        Integer stockAnterior = producto.getStockActual();
        Integer nuevoStock = calcularNuevoStock(stockAnterior, dto.getCantidad(), tipo);

        // Validar que el stock no quede negativo
        if (nuevoStock < 0) {
            throw new ConflictoException(
                String.format("Stock insuficiente para %s. Stock actual: %d, cantidad solicitada: %d", 
                    producto.getNombre(), stockAnterior, dto.getCantidad())
            );
        }

        // Validar stock máximo en entradas
        if (tipo == TipoMovimiento.ENTRADA && producto.getStockMaximo() != null && nuevoStock > producto.getStockMaximo()) {
            throw new ConflictoException(
                String.format("El movimiento excede el stock máximo permitido (%d). Stock resultante: %d", 
                    producto.getStockMaximo(), nuevoStock)
            );
        }

        // Crear movimiento
        MovimientoInventario movimiento = movimientoMapper.toEntity(dto);
        movimiento.setProducto(producto);
        movimiento.setStockAnterior(stockAnterior);
        movimiento.setStockNuevo(nuevoStock);
        movimiento.setFechaMovimiento(LocalDateTime.now());

        // Asignar proveedor si es ENTRADA o DEVOLUCION
        if (dto.getProveedorId() != null) {
            movimiento.setProveedor(proveedorRepository.findById(dto.getProveedorId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor", "id", dto.getProveedorId())));
        }

        // Actualizar stock del producto
        producto.setStockActual(nuevoStock);
        productoRepository.save(producto);
        movimiento = movimientoRepository.save(movimiento);

        log.info("Movimiento registrado con ID: {}. Stock actualizado de {} a {}", 
            movimiento.getId(), stockAnterior, nuevoStock);
        
        return movimientoMapper.toDTO(movimiento);
    }

    /**
     * Calcular el nuevo stock basado en el tipo de movimiento
     */
    private Integer calcularNuevoStock(Integer stockActual, Integer cantidad, TipoMovimiento tipo) {
        return switch (tipo) {
            case ENTRADA -> stockActual + cantidad;
            case SALIDA -> stockActual - cantidad;
            case AJUSTE -> cantidad; // En ajuste, la cantidad es el nuevo stock absoluto
            case DEVOLUCION -> stockActual + cantidad; // Devolución suma al stock
        };
    }

    /**
     * Obtener movimiento por ID
     */
    @Transactional(readOnly = true)
    public MovimientoInventarioDTO obtenerPorId(Long id) {
        log.debug("Buscando movimiento con ID: {}", id);
        MovimientoInventario movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Movimiento de inventario", "id", id));
        return movimientoMapper.toDTO(movimiento);
    }

    /**
     * Listar todos los movimientos de un producto
     */
    @Transactional(readOnly = true)
    public List<MovimientoInventarioDTO> listarPorProducto(Long productoId) {
        log.info("Listando movimientos del producto ID: {}", productoId);
        
        // Verificar que el producto existe
        if (!productoRepository.existsById(productoId)) {
            throw new RecursoNoEncontradoException("Producto", "id", productoId);
        }

        List<MovimientoInventario> movimientos = movimientoRepository.findByProductoIdOrderByFechaMovimientoDesc(productoId);
        return movimientos.stream()
                .map(movimientoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar movimientos por tipo
     */
    @Transactional(readOnly = true)
    public List<MovimientoInventarioDTO> listarPorTipo(String tipoMovimiento) {
        log.info("Listando movimientos de tipo: {}", tipoMovimiento);
        
        TipoMovimiento tipo = TipoMovimiento.valueOf(tipoMovimiento);
        List<MovimientoInventario> movimientos = movimientoRepository.findByTipoMovimientoOrderByFechaMovimientoDesc(tipo);
        
        return movimientos.stream()
                .map(movimientoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * RF4.4: Listar movimientos en un rango de fechas
     */
    @Transactional(readOnly = true)
    public List<MovimientoInventarioDTO> listarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Listando movimientos entre {} y {}", fechaInicio, fechaFin);
        
        if (fechaInicio.isAfter(fechaFin)) {
            throw new ConflictoException("La fecha de inicio no puede ser posterior a la fecha fin");
        }

        List<MovimientoInventario> movimientos = movimientoRepository.findByFechaMovimientoBetweenOrderByFechaMovimientoDesc(fechaInicio, fechaFin);
        return movimientos.stream()
                .map(movimientoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar movimientos del día actual
     */
    @Transactional(readOnly = true)
    public List<MovimientoInventarioDTO> listarMovimientosHoy() {
        log.info("Listando movimientos del día");
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = inicioDia.plusDays(1).minusSeconds(1);
        
        List<MovimientoInventario> movimientos = movimientoRepository.findByFechaMovimientoBetweenOrderByFechaMovimientoDesc(inicioDia, finDia);
        return movimientos.stream()
                .map(movimientoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar últimos movimientos (para dashboard)
     */
    @Transactional(readOnly = true)
    public List<MovimientoInventarioDTO> listarUltimosMovimientos(int limite) {
        log.info("Listando últimos {} movimientos", limite);
        List<MovimientoInventario> movimientos = movimientoRepository.findTop10ByOrderByFechaMovimientoDesc();
        
        return movimientos.stream()
                .limit(limite)
                .map(movimientoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener total de movimientos por producto
     */
    @Transactional(readOnly = true)
    public Long contarMovimientosPorProducto(Long productoId) {
        log.debug("Contando movimientos del producto ID: {}", productoId);
        return movimientoRepository.countByProductoId(productoId);
    }
}
