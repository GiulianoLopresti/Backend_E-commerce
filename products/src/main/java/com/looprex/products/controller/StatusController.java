package com.looprex.products.controller;

import com.looprex.products.dto.ApiResponse;
import com.looprex.products.model.Status;
import com.looprex.products.service.StatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statuses")
@Tag(name = "Estados", description = "Endpoints para gestionar los estados del sistema (productos y compras)")
public class StatusController {

    private final StatusService statusService;

    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping
    @Operation(
        summary = "Obtener todos los estados",
        description = "Retorna una lista completa de todos los estados disponibles en el sistema. " +
                      "Los estados se aplican tanto a productos (Activo, Inactivo) como a compras (Pendiente, Completado, Cancelado, etc.)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lista de estados obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "No hay estados en el sistema",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<List<Status>>> getAllStatuses() {
        List<Status> statuses = statusService.getAllStatuses();

        if (statuses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse<>(false, 204, "No se encontraron estados en el sistema", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, 200, "Estados obtenidos exitosamente", statuses, statuses.size()));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener estado por ID",
        description = "Retorna un estado específico según su identificador único"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Estado encontrado exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Estado no encontrado",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<Status>> getStatusById(
            @Parameter(description = "ID del estado a buscar", example = "1", required = true)
            @PathVariable Long id) {
        return statusService.getStatusById(id)
                .map(status -> ResponseEntity.ok(new ApiResponse<>(true, 200, "Estado encontrado exitosamente", status)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, 404, "Estado con ID " + id + " no encontrado", null)));
    }
}