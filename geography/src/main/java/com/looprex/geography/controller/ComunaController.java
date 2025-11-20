package com.looprex.geography.controller;

import com.looprex.geography.dto.ApiResponse;
import com.looprex.geography.dto.ComunaResponse;
import com.looprex.geography.mapper.ComunaMapper;
import com.looprex.geography.model.Comuna;
import com.looprex.geography.service.ComunaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comunas")
@Tag(name = "Comunas", description = "API para gestión de comunas")
public class ComunaController {

    private final ComunaService comunaService;
    private final ComunaMapper comunaMapper;  // Agregar mapper

    private static final String COMUNA_NOT_FOUND = "Comuna no encontrada";

    public ComunaController(ComunaService comunaService, ComunaMapper comunaMapper) {
        this.comunaService = comunaService;
        this.comunaMapper = comunaMapper;  // Inyectar mapper
    }

    @Operation(summary = "Obtener todas las comunas")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ComunaResponse>>> getAllComunas() {
        List<Comuna> comunas = comunaService.getAllComunas();
        
        if (comunas.isEmpty()) {
            ApiResponse<List<ComunaResponse>> response = new ApiResponse<>(
                false,
                HttpStatus.NO_CONTENT.value(),
                "No se encontraron comunas en el sistema",
                null,
                0L
            );
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }
        
        // Mapear a DTOs
        List<ComunaResponse> comunaResponses = comunas.stream()
                .map(comunaMapper::toComunaResponse)
                .toList();
        
        ApiResponse<List<ComunaResponse>> response = new ApiResponse<>(
            true,
            HttpStatus.OK.value(),
            "Comunas obtenidas exitosamente",
            comunaResponses,
            (long) comunaResponses.size()
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener comuna por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ComunaResponse>> getComunaById(@PathVariable Long id) {
        Optional<Comuna> comunaOpt = comunaService.getComunaById(id);
        
        if (comunaOpt.isPresent()) {
            // Mapear a DTO
            ComunaResponse comunaResponse = comunaMapper.toComunaResponse(comunaOpt.get());
            
            ApiResponse<ComunaResponse> response = new ApiResponse<>(
                true,
                HttpStatus.OK.value(),
                "Comuna encontrada",
                comunaResponse
            );
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<ComunaResponse> response = new ApiResponse<>(
                false,
                HttpStatus.NOT_FOUND.value(),
                COMUNA_NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(summary = "Obtener comunas por región")
    @GetMapping("/region/{regionId}")
    public ResponseEntity<ApiResponse<List<ComunaResponse>>> getComunasByRegionId(@PathVariable Long regionId) {
        List<Comuna> comunas = comunaService.getComunasByRegionId(regionId);
        
        // Mapear a DTOs
        List<ComunaResponse> comunaResponses = comunas.stream()
                .map(comunaMapper::toComunaResponse)
                .toList();
        
        ApiResponse<List<ComunaResponse>> response = new ApiResponse<>(
            true,
            HttpStatus.OK.value(),
            "Comunas obtenidas exitosamente",
            comunaResponses,
            (long) comunaResponses.size()
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear nueva comuna")
    @PostMapping
    public ResponseEntity<ApiResponse<ComunaResponse>> createComuna(@RequestBody Comuna comuna) {
        try {
            Comuna created = comunaService.createComuna(comuna);
            
            // Mapear a DTO
            ComunaResponse comunaResponse = comunaMapper.toComunaResponse(created);
            
            ApiResponse<ComunaResponse> response = new ApiResponse<>(
                true,
                HttpStatus.CREATED.value(),
                "Comuna creada exitosamente",
                comunaResponse
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<ComunaResponse> response = new ApiResponse<>(
                false,
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Actualizar comuna")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ComunaResponse>> updateComuna(@PathVariable Long id, @RequestBody Comuna updatedComuna) {
        Optional<Comuna> comunaOpt = comunaService.updateComuna(id, updatedComuna);
        
        if (comunaOpt.isPresent()) {
            // Mapear a DTO
            ComunaResponse comunaResponse = comunaMapper.toComunaResponse(comunaOpt.get());
            
            ApiResponse<ComunaResponse> response = new ApiResponse<>(
                true,
                HttpStatus.OK.value(),
                "Comuna actualizada exitosamente",
                comunaResponse
            );
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<ComunaResponse> response = new ApiResponse<>(
                false,
                HttpStatus.NOT_FOUND.value(),
                COMUNA_NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(summary = "Eliminar comuna")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComuna(@PathVariable Long id) {
        boolean deleted = comunaService.deleteComuna(id);
        
        if (deleted) {
            ApiResponse<Void> response = new ApiResponse<>(
                true,
                HttpStatus.OK.value(),
                "Comuna eliminada exitosamente"
            );
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Void> response = new ApiResponse<>(
                false,
                HttpStatus.NOT_FOUND.value(),
                COMUNA_NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}