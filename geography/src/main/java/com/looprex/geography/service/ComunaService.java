package com.looprex.geography.service;

import com.looprex.geography.model.Comuna;
import com.looprex.geography.model.Region;
import com.looprex.geography.repository.ComunaRepository;
import com.looprex.geography.repository.RegionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComunaService {

    private final ComunaRepository comunaRepository;
    private final RegionRepository regionRepository;

    public ComunaService(ComunaRepository comunaRepository, RegionRepository regionRepository) {
        this.comunaRepository = comunaRepository;
        this.regionRepository = regionRepository;
    }

    // Obtener todas las comunas
    public List<Comuna> getAllComunas() {
        return comunaRepository.findAll();
    }

    // Obtener comuna por ID
    public Optional<Comuna> getComunaById(Long id) {
        return comunaRepository.findById(id);
    }
    
    // Obtener comunas por región
    public List<Comuna> getComunasByRegionId(Long regionId) {
        return comunaRepository.findByRegionId(regionId);
    }

    // Crear nueva comuna
    public Comuna createComuna(Comuna comuna) {
        if (comuna.getName() == null || comuna.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la comuna no puede estar vacío");
        }
        
        if (comuna.getRegion() == null || comuna.getRegion().getRegionId() == null) {
            throw new IllegalArgumentException("La comuna debe pertenecer a una región");
        }
        
        // Cargar la región completa desde la BD
        Region region = regionRepository.findById(comuna.getRegion().getRegionId())
                .orElseThrow(() -> new IllegalArgumentException("La región con ID " + comuna.getRegion().getRegionId() + " no existe"));
        
        comuna.setRegion(region);
        
        // Verificar duplicados ANTES de guardar
        if (comunaRepository.findByNameAndRegionId(comuna.getName(), region.getRegionId()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una comuna con ese nombre en esta región");
        }
        
        // Guardar
        Comuna saved = comunaRepository.save(comuna);
        
        return comunaRepository.findById(saved.getComunaId()).orElse(saved);
    }

    // Actualizar comuna
    public Optional<Comuna> updateComuna(Long id, Comuna updatedComuna) {
        return comunaRepository.findById(id).map(existingComuna -> {
            if (updatedComuna.getName() != null && !updatedComuna.getName().trim().isEmpty()) {
                Optional<Comuna> comunaWithSameName = comunaRepository.findByNameAndRegionId(
                    updatedComuna.getName(), 
                    existingComuna.getRegion().getRegionId()
                );
                if (comunaWithSameName.isPresent() && !comunaWithSameName.get().getComunaId().equals(id)) {
                    throw new IllegalArgumentException("Ya existe otra comuna con ese nombre en esta región");
                }
                existingComuna.setName(updatedComuna.getName());
            }
            
            if (updatedComuna.getRegion() != null && updatedComuna.getRegion().getRegionId() != null) {
                Region region = regionRepository.findById(updatedComuna.getRegion().getRegionId())
                        .orElseThrow(() -> new IllegalArgumentException("La región con ID " + updatedComuna.getRegion().getRegionId() + " no existe"));
                existingComuna.setRegion(region);
            }
            
            //Guardar
            Comuna saved = comunaRepository.save(existingComuna);
            
            // Recargar
            return comunaRepository.findById(saved.getComunaId()).orElse(saved);
        });
    }

    // Eliminar comuna
    public boolean deleteComuna(Long id) {
        if (comunaRepository.existsById(id)) {
            comunaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}