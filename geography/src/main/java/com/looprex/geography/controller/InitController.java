package com.looprex.geography.controller;

import com.looprex.geography.model.Region;
import com.looprex.geography.model.Comuna;
import com.looprex.geography.repository.RegionRepository;
import com.looprex.geography.repository.ComunaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/init")
@Tag(name = "Inicialización", description = "Endpoints para cargar datos iniciales")
public class InitController {

    private final RegionRepository regionRepository;
    private final ComunaRepository comunaRepository;

    public InitController(RegionRepository regionRepository, ComunaRepository comunaRepository) {
        this.regionRepository = regionRepository;
        this.comunaRepository = comunaRepository;
    }

    @Operation(summary = "Cargar datos iniciales", description = "Inserta regiones y comunas de Chile")
    @PostMapping("/seed")
    public ResponseEntity<String> seedData() {
        StringBuilder mensaje = new StringBuilder();

        if (regionRepository.count() == 0) {
            // Crear regiones
            Region metropolitana = new Region();
            metropolitana.setName("Región Metropolitana");
            regionRepository.save(metropolitana);

            Region valparaiso = new Region();
            valparaiso.setName("Región de Valparaíso");
            regionRepository.save(valparaiso);

            mensaje.append("Regiones creadas. ");

            // Crear comunas
            Comuna santiago = new Comuna();
            santiago.setName("Santiago");
            santiago.setRegion(metropolitana);
            comunaRepository.save(santiago);

            Comuna providencia = new Comuna();
            providencia.setName("Providencia");
            providencia.setRegion(metropolitana);
            comunaRepository.save(providencia);

            Comuna vinaDelMar = new Comuna();
            vinaDelMar.setName("Viña del Mar");
            vinaDelMar.setRegion(valparaiso);
            comunaRepository.save(vinaDelMar);

            Comuna valparaisoCiudad = new Comuna();
            valparaisoCiudad.setName("Valparaíso");
            valparaisoCiudad.setRegion(valparaiso);
            comunaRepository.save(valparaisoCiudad);

            mensaje.append("Comunas creadas.");
        } else {
            mensaje.append("Los datos ya existen.");
        }

        return ResponseEntity.ok(mensaje.toString());
    }
}