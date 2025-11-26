package com.nuclearvet.modulos.inventario.service;

import com.nuclearvet.common.exception.ConflictoException;
import com.nuclearvet.common.exception.RecursoNoEncontradoException;
import com.nuclearvet.modulos.inventario.dto.CategoriaDTO;
import com.nuclearvet.modulos.inventario.dto.CrearCategoriaDTO;
import com.nuclearvet.modulos.inventario.entity.Categoria;
import com.nuclearvet.modulos.inventario.mapper.CategoriaMapper;
import com.nuclearvet.modulos.inventario.repository.CategoriaRepository;
import com.nuclearvet.modulos.inventario.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de categorías de productos.
 * RF4.1 - Categorización de productos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final CategoriaMapper categoriaMapper;

    /**
     * Crear una nueva categoría
     */
    @Transactional
    public CategoriaDTO crearCategoria(CrearCategoriaDTO dto) {
        log.info("Creando categoría: {}", dto.getNombre());

        // Validar que el nombre sea único
        if (categoriaRepository.findByNombre(dto.getNombre()).isPresent()) {
            throw new ConflictoException("Ya existe una categoría con ese nombre, parce");
        }

        Categoria categoria = categoriaMapper.toEntity(dto);
        categoria = categoriaRepository.save(categoria);
        
        log.info("Categoría creada con ID: {}", categoria.getId());
        return toDTO(categoria);
    }

    /**
     * Actualizar una categoría existente
     */
    @Transactional
    public CategoriaDTO actualizarCategoria(Long id, CrearCategoriaDTO dto) {
        log.info("Actualizando categoría con ID: {}", id);

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria", "id", id));

        // Validar nombre único si cambió
        if (!categoria.getNombre().equals(dto.getNombre())) {
            if (categoriaRepository.findByNombre(dto.getNombre()).isPresent()) {
                throw new ConflictoException("Ya existe otra categoría con ese nombre");
            }
        }

        categoriaMapper.updateEntity(dto, categoria);
        categoria = categoriaRepository.save(categoria);
        
        log.info("Categoría actualizada: {}", id);
        return toDTO(categoria);
    }

    /**
     * Obtener categoría por ID
     */
    @Transactional(readOnly = true)
    public CategoriaDTO obtenerPorId(Long id) {
        log.debug("Buscando categoría con ID: {}", id);
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria", "id", id));
        return toDTO(categoria);
    }

    /**
     * Listar todas las categorías activas
     */
    @Transactional(readOnly = true)
    public List<CategoriaDTO> listarTodas() {
        log.info("Listando todas las categorías activas");
        List<Categoria> categorias = categoriaRepository.findByActivoTrueOrderByNombreAsc();
        return categorias.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar categorías por tipo
     */
    @Transactional(readOnly = true)
    public List<CategoriaDTO> listarPorTipo(String tipoCategoria) {
        log.info("Listando categorías del tipo: {}", tipoCategoria);
        List<Categoria> categorias = categoriaRepository.findByTipoCategoriaAndActivoTrue(tipoCategoria);
        return categorias.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Desactivar una categoría
     */
    @Transactional
    public void desactivarCategoria(Long id) {
        log.info("Desactivando categoría con ID: {}", id);
        
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria", "id", id));
        
        // Verificar que no tenga productos activos
        Long cantidadProductos = productoRepository.countByCategoriaIdAndActivoTrue(id);
        if (cantidadProductos > 0) {
            throw new ConflictoException(
                String.format("No se puede desactivar la categoría porque tiene %d productos activos", cantidadProductos)
            );
        }

        categoria.setActivo(false);
        categoriaRepository.save(categoria);
        log.info("Categoría desactivada: {}", id);
    }

    /**
     * Método helper para convertir a DTO con total de productos
     */
    private CategoriaDTO toDTO(Categoria categoria) {
        CategoriaDTO dto = categoriaMapper.toDTO(categoria);
        Long totalProductos = productoRepository.countByCategoriaIdAndActivoTrue(categoria.getId());
        dto.setTotalProductos(totalProductos);
        return dto;
    }
}
