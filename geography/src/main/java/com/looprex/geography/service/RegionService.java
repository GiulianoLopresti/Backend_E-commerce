package com.looprex.geography.service;

import com.looprex.geography.model.Region;
import com.looprex.geography.repository.RegionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RegionService {

    private final RegionRepository regionRepository;

    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    // Obtener todas las regiones
    public List<Region> getAllRegions() {
        return regionRepository.findAll();
    }

    // Obtener región por ID
    public Optional<Region> getRegionById(Long id) {
        return regionRepository.findById(id);
    }

    // Crear nueva región
    public Region createRegion(Region region) {
        if (region.getName() == null || region.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la región no puede estar vacío");
        }
        
        if (regionRepository.findByName(region.getName()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una región con ese nombre");
        }
        
        return regionRepository.save(region);
    }

    // Actualizar región
    public Optional<Region> updateRegion(Long id, Region updatedRegion) {
        return regionRepository.findById(id).map(existingRegion -> {
            if (updatedRegion.getName() != null && !updatedRegion.getName().trim().isEmpty()) {
                // Verificar que el nuevo nombre no esté en uso por otra región
                Optional<Region> regionWithSameName = regionRepository.findByName(updatedRegion.getName());
                if (regionWithSameName.isPresent() && !regionWithSameName.get().getRegionId().equals(id)) {
                    throw new IllegalArgumentException("Ya existe otra región con ese nombre");
                }
                existingRegion.setName(updatedRegion.getName());
            }
            return regionRepository.save(existingRegion);
        });
    }

    // Eliminar región
    public boolean deleteRegion(Long id) {
        if (regionRepository.existsById(id)) {
            regionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Verificar si existe por nombre
    public boolean existsByName(String name) {
        return regionRepository.findByName(name).isPresent();
    }
}