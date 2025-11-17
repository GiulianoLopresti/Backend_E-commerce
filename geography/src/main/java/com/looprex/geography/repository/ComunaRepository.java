package com.looprex.geography.repository;

import com.looprex.geography.model.Comuna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComunaRepository extends JpaRepository<Comuna, Long> {

    // Buscar comuna por nombre
    Optional<Comuna> findByName(String name);

    // Verificar si existe una comuna con ese nombre
    boolean existsByName(String name);

    // Obtener todas las comunas de una región (navegando por la relación @ManyToOne)
    List<Comuna> findByRegion_RegionId(Long regionId);
}