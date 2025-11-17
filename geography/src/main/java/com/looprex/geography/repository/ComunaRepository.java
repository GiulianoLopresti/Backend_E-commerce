package com.looprex.geography.repository;

import com.looprex.geography.model.Comuna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    @Query("SELECT c FROM Comuna c WHERE c.region.regionId = :regionId")
    List<Comuna> findByRegionId(@Param("regionId") Long regionId);

    @Query("SELECT c FROM Comuna c WHERE c.name = :name AND c.region.regionId = :regionId")
    Optional<Comuna> findByNameAndRegionId(@Param("name") String name, @Param("regionId") Long regionId);
}