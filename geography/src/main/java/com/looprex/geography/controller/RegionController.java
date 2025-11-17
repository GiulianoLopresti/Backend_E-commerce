package com.looprex.geography.controller;

import com.looprex.geography.dto.ApiResponse;
import com.looprex.geography.model.Region;
import com.looprex.geography.service.RegionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/regions")
@Tag(name = "Regiones", description = "API para gestión de regiones")
public class RegionController {

    private final RegionService regionService;

    private static final String REGION_NOT_FOUND = "Región no encontrada";

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @Operation(summary = "Obtener todas las regiones")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Region>>> getAllRegions() {
        List<Region> regions = regionService.getAllRegions();
        
        if (regions.isEmpty()) {
            ApiResponse<List<Region>> response = new ApiResponse<>(
                false,
                HttpStatus.NO_CONTENT.value(),
                "No se encontraron regiones en el sistema",
                null,
                0L
            );
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }
        
        ApiResponse<List<Region>> response = new ApiResponse<>(
            true,
            HttpStatus.OK.value(),
            "Regiones obtenidas exitosamente",
            regions,
            (long) regions.size()
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener región por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Region>> getRegionById(@PathVariable Long id) {
        Optional<Region> regionOpt = regionService.getRegionById(id);
        
        if (regionOpt.isPresent()) {
            ApiResponse<Region> response = new ApiResponse<>(
                true,
                HttpStatus.OK.value(),
                "Región encontrada",
                regionOpt.get()
            );
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Region> response = new ApiResponse<>(
                false,
                HttpStatus.NOT_FOUND.value(),
                REGION_NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(summary = "Crear nueva región")
    @PostMapping
    public ResponseEntity<ApiResponse<Region>> createRegion(@RequestBody Region region) {
        try {
            Region created = regionService.createRegion(region);
            ApiResponse<Region> response = new ApiResponse<>(
                true,
                HttpStatus.CREATED.value(),
                "Región creada exitosamente",
                created
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<Region> response = new ApiResponse<>(
                false,
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Actualizar región")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Region>> updateRegion(@PathVariable Long id, @RequestBody Region region) {
        try {
            Optional<Region> updatedOpt = regionService.updateRegion(id, region);
            
            if (updatedOpt.isPresent()) {
                ApiResponse<Region> response = new ApiResponse<>(
                    true,
                    HttpStatus.OK.value(),
                    "Región actualizada exitosamente",
                    updatedOpt.get()
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Region> response = new ApiResponse<>(
                    false,
                    HttpStatus.NOT_FOUND.value(),
                    REGION_NOT_FOUND
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (IllegalArgumentException e) {
            ApiResponse<Region> response = new ApiResponse<>(
                false,
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Eliminar región")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRegion(@PathVariable Long id) {
        boolean deleted = regionService.deleteRegion(id);
        
        if (deleted) {
            ApiResponse<Void> response = new ApiResponse<>(
                true,
                HttpStatus.OK.value(),
                "Región eliminada exitosamente"
            );
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Void> response = new ApiResponse<>(
                false,
                HttpStatus.NOT_FOUND.value(),
                REGION_NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}