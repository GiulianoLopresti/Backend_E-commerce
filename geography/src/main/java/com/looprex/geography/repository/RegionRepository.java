package com.looprex.geography.repository;

import com.looprex.geography.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

    // Buscar región por nombre
    Optional<Region> findByName(String name);

    // Verificar si existe una región con ese nombre
    boolean existsByName(String name);
}