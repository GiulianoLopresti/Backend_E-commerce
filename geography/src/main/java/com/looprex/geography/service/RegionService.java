package com.looprex.geography.service;

import com.looprex.geography.model.Comuna;
import com.looprex.geography.model.Region;
import com.looprex.geography.repository.ComunaRepository;
import com.looprex.geography.repository.RegionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RegionService {

    private final RegionRepository regionRepository;
    private final ComunaRepository comunaRepository;
    
    public RegionService(RegionRepository regionRepository, ComunaRepository comunaRepository) {
        this.regionRepository = regionRepository;
        this.comunaRepository = comunaRepository;
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
        
        // Verificar duplicados ANTES de guardar
        if (regionRepository.findByName(region.getName()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una región con ese nombre");
        }
        
        // Guardar y retornar directamente
        return regionRepository.save(region);
    }

    // Actualizar región
   public Optional<Region> updateRegion(Long id, Region updatedRegion) {
        return regionRepository.findById(id).map(existingRegion -> {
            if (updatedRegion.getName() != null && !updatedRegion.getName().trim().isEmpty()) {
                Optional<Region> regionWithSameName = regionRepository.findByName(updatedRegion.getName());
                if (regionWithSameName.isPresent() && !regionWithSameName.get().getRegionId().equals(id)) {
                    throw new IllegalArgumentException("Ya existe otra región con ese nombre");
                }
                existingRegion.setName(updatedRegion.getName());
            }
            
            // Guardar y retornar
            return regionRepository.save(existingRegion);
        });
    }

    // Eliminar región
    public void deleteRegion(Long id) {
        // Verificar que la región existe
        if (!regionRepository.existsById(id)) {
            throw new IllegalArgumentException("Región no encontrada");
        }
        
        // Verificar que no tenga comunas asociadas
        List<Comuna> comunas = comunaRepository.findByRegionId(id);
        if (!comunas.isEmpty()) {
            throw new IllegalStateException(
                "No se puede eliminar la región porque tiene " + 
                comunas.size() + " comuna(s) asociada(s). " +
                "Elimina las comunas primero."
            );
        }
        
        // Si no tiene comunas, eliminar
        regionRepository.deleteById(id);
    }

    // Verificar si existe por nombre
    public boolean existsByName(String name) {
        return regionRepository.findByName(name).isPresent();
    }
}