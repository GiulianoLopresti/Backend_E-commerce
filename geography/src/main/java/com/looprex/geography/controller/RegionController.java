package com.looprex.geography.controller;

import com.looprex.geography.dto.ApiResponse;
import com.looprex.geography.dto.RegionResponse;
import com.looprex.geography.mapper.RegionMapper;
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
    private final RegionMapper regionMapper;  // Agregar mapper

    private static final String REGION_NOT_FOUND = "Región no encontrada";

    public RegionController(RegionService regionService, RegionMapper regionMapper) {
        this.regionService = regionService;
        this.regionMapper = regionMapper;  // Inyectar mapper
    }

    @Operation(summary = "Obtener todas las regiones")
    @GetMapping
    public ResponseEntity<ApiResponse<List<RegionResponse>>> getAllRegions() {
        List<Region> regions = regionService.getAllRegions();
        
        if (regions.isEmpty()) {
            ApiResponse<List<RegionResponse>> response = new ApiResponse<>(
                false,
                HttpStatus.NO_CONTENT.value(),
                "No se encontraron regiones en el sistema",
                null,
                0L
            );
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }
        
        //Mapear a DTOs
        List<RegionResponse> regionResponses = regions.stream()
                .map(regionMapper::toRegionResponse)
                .toList();
        
        ApiResponse<List<RegionResponse>> response = new ApiResponse<>(
            true,
            HttpStatus.OK.value(),
            "Regiones obtenidas exitosamente",
            regionResponses,
            (long) regionResponses.size()
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener región por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RegionResponse>> getRegionById(@PathVariable Long id) {
        Optional<Region> regionOpt = regionService.getRegionById(id);
        
        if (regionOpt.isPresent()) {
            // Mapear a DTO
            RegionResponse regionResponse = regionMapper.toRegionResponse(regionOpt.get());
            
            ApiResponse<RegionResponse> response = new ApiResponse<>(
                true,
                HttpStatus.OK.value(),
                "Región encontrada",
                regionResponse
            );
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<RegionResponse> response = new ApiResponse<>(
                false,
                HttpStatus.NOT_FOUND.value(),
                REGION_NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(summary = "Crear nueva región")
    @PostMapping
    public ResponseEntity<ApiResponse<RegionResponse>> createRegion(@RequestBody Region region) {
        try {
            Region created = regionService.createRegion(region);
            
            // Mapear a DTO
            RegionResponse regionResponse = regionMapper.toRegionResponse(created);
            
            ApiResponse<RegionResponse> response = new ApiResponse<>(
                true,
                HttpStatus.CREATED.value(),
                "Región creada exitosamente",
                regionResponse
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<RegionResponse> response = new ApiResponse<>(
                false,
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Actualizar región")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RegionResponse>> updateRegion(@PathVariable Long id, @RequestBody Region updatedRegion) {
        Optional<Region> regionOpt = regionService.updateRegion(id, updatedRegion);
        
        if (regionOpt.isPresent()) {
            // Mapear a DTO
            RegionResponse regionResponse = regionMapper.toRegionResponse(regionOpt.get());
            
            ApiResponse<RegionResponse> response = new ApiResponse<>(
                true,
                HttpStatus.OK.value(),
                "Región actualizada exitosamente",
                regionResponse
            );
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<RegionResponse> response = new ApiResponse<>(
                false,
                HttpStatus.NOT_FOUND.value(),
                REGION_NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(summary = "Eliminar región")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRegion(@PathVariable Long id) {
        try {
            regionService.deleteRegion(id);
            
            ApiResponse<Void> response = new ApiResponse<>(
                true,
                HttpStatus.OK.value(),
                "Región eliminada exitosamente"
            );
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            // Región no encontrada
            ApiResponse<Void> response = new ApiResponse<>(
                false,
                HttpStatus.NOT_FOUND.value(),
                e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
        } catch (IllegalStateException e) {
            // Región con comunas asociadas
            ApiResponse<Void> response = new ApiResponse<>(
                false,
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}