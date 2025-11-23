package com.looprex.geography.config;

import com.looprex.geography.model.Comuna;
import com.looprex.geography.model.Region;
import com.looprex.geography.repository.ComunaRepository;
import com.looprex.geography.repository.RegionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RegionRepository regionRepository;
    private final ComunaRepository comunaRepository;

    public DataSeeder(RegionRepository regionRepository, ComunaRepository comunaRepository) {
        this.regionRepository = regionRepository;
        this.comunaRepository = comunaRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Validación para no repetir datos
        if (regionRepository.count() == 0) {
            // Regiones
            Region metropolitana = new Region();
            metropolitana.setName("Región Metropolitana");
            regionRepository.save(metropolitana);

            Region valparaiso = new Region();
            valparaiso.setName("Región de Valparaíso");
            regionRepository.save(valparaiso);


            // Comunas
            Comuna santiago = new Comuna();
            santiago.setName("Santiago");
            santiago.setRegion(metropolitana);
            comunaRepository.save(santiago);

            Comuna providencia = new Comuna();
            providencia.setName("Providencia");
            providencia.setRegion(metropolitana);
            comunaRepository.save(providencia);

            Comuna vina = new Comuna();
            vina.setName("Viña del Mar");
            vina.setRegion(valparaiso);
            comunaRepository.save(vina);

            Comuna valpo = new Comuna();
            valpo.setName("Valparaíso");
            valpo.setRegion(valparaiso);
            comunaRepository.save(valpo);

        }
    }
}