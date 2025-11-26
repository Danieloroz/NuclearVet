package com.nuclearvet.modulos.inventario.repository;

import com.nuclearvet.modulos.inventario.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para Categorías.
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    
    Optional<Categoria> findByNombre(String nombre);
    
    List<Categoria> findByTipoCategoriaAndActivoTrue(String tipoCategoria);
    
    List<Categoria> findByActivoTrueOrderByNombreAsc();
}
