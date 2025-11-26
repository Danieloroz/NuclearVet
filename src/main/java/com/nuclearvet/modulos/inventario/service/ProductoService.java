package com.nuclearvet.modulos.inventario.service;

import com.nuclearvet.common.exception.ConflictoException;
import com.nuclearvet.common.exception.RecursoNoEncontradoException;
import com.nuclearvet.common.exception.ValidacionException;
import com.nuclearvet.modulos.inventario.dto.CrearProductoDTO;
import com.nuclearvet.modulos.inventario.dto.ProductoDTO;
import com.nuclearvet.modulos.inventario.entity.Categoria;
import com.nuclearvet.modulos.inventario.entity.Producto;
import com.nuclearvet.modulos.inventario.entity.Proveedor;
import com.nuclearvet.modulos.inventario.mapper.ProductoMapper;
import com.nuclearvet.modulos.inventario.repository.CategoriaRepository;
import com.nuclearvet.modulos.inventario.repository.ProductoRepository;
import com.nuclearvet.modulos.inventario.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de productos e inventario.
 * Implementa RF4.1 a RF4.5
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoMapper productoMapper;

    /**
     * RF4.1: Crear un nuevo producto
     */
    @Transactional
    public ProductoDTO crearProducto(CrearProductoDTO dto) {
        log.info("Creando producto con código: {}", dto.getCodigo());

        // Validar que el código sea único
        if (productoRepository.findByCodigo(dto.getCodigo()).isPresent()) {
            throw new ConflictoException("Ya existe un producto con ese código, parce");
        }

        // Validar que la categoría existe
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria", "id", dto.getCategoriaId()));

        // Validar proveedor si viene
        Proveedor proveedor = null;
        if (dto.getProveedorId() != null) {
            proveedor = proveedorRepository.findById(dto.getProveedorId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor", "id", dto.getProveedorId()));
        }

        // Validar que el precio de venta sea mayor al de compra
        if (dto.getPrecioVenta().compareTo(dto.getPrecioCompra()) < 0) {
            throw new ValidacionException("El precio de venta no puede ser menor al precio de compra");
        }

        // Validar stock máximo
        if (dto.getStockMaximo() != null && dto.getStockMaximo() < dto.getStockMinimo()) {
            throw new ValidacionException("El stock máximo no puede ser menor al stock mínimo");
        }

        // Crear producto
        Producto producto = productoMapper.toEntity(dto);
        producto.setCategoria(categoria);
        producto.setProveedor(proveedor);

        producto = productoRepository.save(producto);
        log.info("Producto creado exitosamente con ID: {}", producto.getId());
        return productoMapper.toDTO(producto);
    }

    /**
     * RF4.1: Actualizar un producto existente
     */
    @Transactional
    public ProductoDTO actualizarProducto(Long id, CrearProductoDTO dto) {
        log.info("Actualizando producto con ID: {}", id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", "id", id));

        // Validar código único si cambió
        if (!producto.getCodigo().equals(dto.getCodigo())) {
            if (productoRepository.findByCodigo(dto.getCodigo()).isPresent()) {
                throw new ConflictoException("Ya existe otro producto con ese código");
            }
        }

        // Validar categoría
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria", "id", dto.getCategoriaId()));

        // Validar proveedor si viene
        Proveedor proveedor = null;
        if (dto.getProveedorId() != null) {
            proveedor = proveedorRepository.findById(dto.getProveedorId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor", "id", dto.getProveedorId()));
        }

        // Validar precios
        if (dto.getPrecioVenta().compareTo(dto.getPrecioCompra()) < 0) {
            throw new ValidacionException("El precio de venta no puede ser menor al precio de compra");
        }

        // Actualizar campos
        productoMapper.updateEntity(dto, producto);
        producto.setCategoria(categoria);
        producto.setProveedor(proveedor);

        producto = productoRepository.save(producto);
        log.info("Producto actualizado: {}", id);
        return productoMapper.toDTO(producto);
    }

    /**
     * RF4.1: Obtener producto por ID
     */
    @Transactional(readOnly = true)
    public ProductoDTO obtenerPorId(Long id) {
        log.debug("Buscando producto con ID: {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", "id", id));
        return productoMapper.toDTO(producto);
    }

    /**
     * RF4.1: Obtener producto por código
     */
    @Transactional(readOnly = true)
    public ProductoDTO obtenerPorCodigo(String codigo) {
        log.debug("Buscando producto con código: {}", codigo);
        Producto producto = productoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", "codigo", codigo));
        return productoMapper.toDTO(producto);
    }

    /**
     * RF4.1: Listar todos los productos activos
     */
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarTodos() {
        log.info("Listando todos los productos activos");
        List<Producto> productos = productoRepository.findByActivoTrueOrderByNombreAsc();
        return productos.stream()
                .map(productoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * RF4.1: Listar productos por categoría
     */
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarPorCategoria(Long categoriaId) {
        log.info("Listando productos de la categoría: {}", categoriaId);
        
        // Validar que la categoría existe
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new RecursoNoEncontradoException("Categoria", "id", categoriaId);
        }

        List<Producto> productos = productoRepository.findByCategoriaIdAndActivoTrue(categoriaId);
        return productos.stream()
                .map(productoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * RF4.2: Listar productos por proveedor
     */
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarPorProveedor(Long proveedorId) {
        log.info("Listando productos del proveedor: {}", proveedorId);
        
        // Validar que el proveedor existe
        if (!proveedorRepository.existsById(proveedorId)) {
            throw new RecursoNoEncontradoException("Proveedor", "id", proveedorId);
        }

        List<Producto> productos = productoRepository.findByProveedorIdAndActivoTrue(proveedorId);
        return productos.stream()
                .map(productoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * RF4.3: Obtener productos con bajo stock
     */
    @Transactional(readOnly = true)
    public List<ProductoDTO> obtenerProductosConBajoStock() {
        log.info("Consultando productos con bajo stock");
        List<Producto> productos = productoRepository.findProductosConBajoStock();
        return productos.stream()
                .map(productoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * RF4.3: Obtener productos próximos a vencer (30 días)
     */
    @Transactional(readOnly = true)
    public List<ProductoDTO> obtenerProductosProximosAVencer() {
        log.info("Consultando productos próximos a vencer");
        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = hoy.plusDays(30);
        
        List<Producto> productos = productoRepository.findProductosProximosAVencer(hoy, fechaLimite);
        return productos.stream()
                .map(productoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * RF4.3: Obtener productos vencidos
     */
    @Transactional(readOnly = true)
    public List<ProductoDTO> obtenerProductosVencidos() {
        log.info("Consultando productos vencidos");
        List<Producto> productos = productoRepository.findProductosVencidos();
        return productos.stream()
                .map(productoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar productos por nombre
     */
    @Transactional(readOnly = true)
    public List<ProductoDTO> buscarPorNombre(String nombre) {
        log.info("Buscando productos con nombre: {}", nombre);
        List<Producto> productos = productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
        return productos.stream()
                .map(productoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Desactivar un producto
     */
    @Transactional
    public void desactivarProducto(Long id) {
        log.info("Desactivando producto con ID: {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", "id", id));
        
        producto.setActivo(false);
        productoRepository.save(producto);
        log.info("Producto desactivado: {}", id);
    }

    /**
     * Ajustar stock de un producto manualmente
     */
    @Transactional
    public ProductoDTO ajustarStock(Long id, Integer nuevoStock) {
        log.info("Ajustando stock del producto {} a {}", id, nuevoStock);
        
        if (nuevoStock < 0) {
            throw new ValidacionException("El stock no puede ser negativo, parce");
        }

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", "id", id));
        
        producto.setStockActual(nuevoStock);
        producto = productoRepository.save(producto);
        
        log.info("Stock ajustado para producto {}: {} unidades", id, nuevoStock);
        return productoMapper.toDTO(producto);
    }
}
