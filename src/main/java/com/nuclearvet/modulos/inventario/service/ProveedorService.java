package com.nuclearvet.modulos.inventario.service;

import com.nuclearvet.common.exception.ConflictoException;
import com.nuclearvet.common.exception.RecursoNoEncontradoException;
import com.nuclearvet.modulos.inventario.dto.CrearProveedorDTO;
import com.nuclearvet.modulos.inventario.dto.ProveedorDTO;
import com.nuclearvet.modulos.inventario.entity.Proveedor;
import com.nuclearvet.modulos.inventario.mapper.ProveedorMapper;
import com.nuclearvet.modulos.inventario.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de proveedores.
 * RF4.2 - Gestión de proveedores
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ProveedorMapper proveedorMapper;

    /**
     * RF4.2: Crear un nuevo proveedor
     */
    @Transactional
    public ProveedorDTO crearProveedor(CrearProveedorDTO dto) {
        log.info("Creando proveedor: {}", dto.getNombre());

        // Validar que el NIT sea único
        if (proveedorRepository.findByNit(dto.getNit()).isPresent()) {
            throw new ConflictoException("Ya existe un proveedor con ese NIT, parce");
        }

        Proveedor proveedor = proveedorMapper.toEntity(dto);
        proveedor = proveedorRepository.save(proveedor);
        
        log.info("Proveedor creado con ID: {}", proveedor.getId());
        return proveedorMapper.toDTO(proveedor);
    }

    /**
     * RF4.2: Actualizar un proveedor existente
     */
    @Transactional
    public ProveedorDTO actualizarProveedor(Long id, CrearProveedorDTO dto) {
        log.info("Actualizando proveedor con ID: {}", id);

        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor", "id", id));

        // Validar NIT único si cambió
        if (!proveedor.getNit().equals(dto.getNit())) {
            if (proveedorRepository.findByNit(dto.getNit()).isPresent()) {
                throw new ConflictoException("Ya existe otro proveedor con ese NIT");
            }
        }

        proveedorMapper.updateEntity(dto, proveedor);
        proveedor = proveedorRepository.save(proveedor);
        
        log.info("Proveedor actualizado: {}", id);
        return proveedorMapper.toDTO(proveedor);
    }

    /**
     * Obtener proveedor por ID
     */
    @Transactional(readOnly = true)
    public ProveedorDTO obtenerPorId(Long id) {
        log.debug("Buscando proveedor con ID: {}", id);
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor", "id", id));
        return proveedorMapper.toDTO(proveedor);
    }

    /**
     * Obtener proveedor por NIT
     */
    @Transactional(readOnly = true)
    public ProveedorDTO obtenerPorNit(String nit) {
        log.debug("Buscando proveedor con NIT: {}", nit);
        Proveedor proveedor = proveedorRepository.findByNit(nit)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor", "NIT", nit));
        return proveedorMapper.toDTO(proveedor);
    }

    /**
     * Listar todos los proveedores activos
     */
    @Transactional(readOnly = true)
    public List<ProveedorDTO> listarTodos() {
        log.info("Listando todos los proveedores activos");
        List<Proveedor> proveedores = proveedorRepository.findByActivoTrueOrderByNombreAsc();
        return proveedores.stream()
                .map(proveedorMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar proveedores por nombre
     */
    @Transactional(readOnly = true)
    public List<ProveedorDTO> buscarPorNombre(String nombre) {
        log.info("Buscando proveedores con nombre: {}", nombre);
        List<Proveedor> proveedores = proveedorRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
        return proveedores.stream()
                .map(proveedorMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar proveedores por calificación mínima
     */
    @Transactional(readOnly = true)
    public List<ProveedorDTO> listarPorCalificacion(Integer calificacionMinima) {
        log.info("Listando proveedores con calificación >= {}", calificacionMinima);
        List<Proveedor> proveedores = proveedorRepository.findByCalificacionMayorIgualAndActivoTrue(calificacionMinima);
        return proveedores.stream()
                .map(proveedorMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Desactivar un proveedor
     */
    @Transactional
    public void desactivarProveedor(Long id) {
        log.info("Desactivando proveedor con ID: {}", id);
        
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor", "id", id));
        
        proveedor.setActivo(false);
        proveedorRepository.save(proveedor);
        log.info("Proveedor desactivado: {}", id);
    }

    /**
     * Actualizar calificación de un proveedor
     */
    @Transactional
    public ProveedorDTO actualizarCalificacion(Long id, Integer nuevaCalificacion) {
        log.info("Actualizando calificación del proveedor {} a {}", id, nuevaCalificacion);
        
        if (nuevaCalificacion < 1 || nuevaCalificacion > 5) {
            throw new ConflictoException("La calificación debe estar entre 1 y 5 estrellas");
        }

        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor", "id", id));
        
        proveedor.setCalificacion(nuevaCalificacion);
        proveedor = proveedorRepository.save(proveedor);
        
        log.info("Calificación actualizada para proveedor {}: {} estrellas", id, nuevaCalificacion);
        return proveedorMapper.toDTO(proveedor);
    }
}
