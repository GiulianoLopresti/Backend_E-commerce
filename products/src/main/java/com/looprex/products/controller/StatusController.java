package com.looprex.products.controller;

import com.looprex.products.dto.ApiResponse;
import com.looprex.products.dto.StatusResponse;
import com.looprex.products.mapper.StatusMapper;
import com.looprex.products.service.StatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statuses")
@Tag(
    name = "Estados", 
    description = "Endpoints para consultar los estados del sistema. Los estados se aplican a " +
                  "diferentes entidades: productos (Activo, Inactivo) y compras (Pendiente, Completado, " +
                  "Cancelado, En envío). Este módulo es de solo lectura, ya que los estados están " +
                  "predefinidos en la base de datos y no deben ser modificados dinámicamente"
)
public class StatusController {

    private final StatusService statusService;
    private final StatusMapper statusMapper;

    public StatusController(StatusService statusService, StatusMapper statusMapper) {
        this.statusService = statusService;
        this.statusMapper = statusMapper;
    }

    @GetMapping
    @Operation(
    summary = "Obtener todos los estados",
    description = "Retorna una lista completa de todos los estados disponibles en el sistema. " +
                  "Los estados se usan tanto para productos como para compras: " +
                  "• Estados de productos: Activo (1), Inactivo (2) " +
                  "• Estados de compras: Pendiente (3), Completado (4), Cancelado (5), En envío (6) " +
                  "Este endpoint es útil para poblar selectores de estado en formularios de " +
                  "creación/edición de productos y para filtros de estado en dashboards. " +
                  "Si no hay estados registrados, retorna un status 204 (No Content)"
                )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lista de estados obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Estados obtenidos exitosamente",
                      "data": [
                        {
                          "statusId": 1,
                          "name": "Activo"
                        },
                        {
                          "statusId": 2,
                          "name": "Inactivo"
                        },
                        {
                          "statusId": 3,
                          "name": "Pendiente"
                        },
                        {
                          "statusId": 4,
                          "name": "Completado"
                        },
                        {
                          "statusId": 5,
                          "name": "Cancelado"
                        },
                        {
                          "statusId": 6,
                          "name": "En envío"
                        }
                      ],
                      "count": 6
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "No hay estados en el sistema - Base de datos no inicializada",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 204,
                      "message": "No se encontraron estados en el sistema"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<List<StatusResponse>>> getAllStatuses() {
        List<com.looprex.products.model.Status> statuses = statusService.getAllStatuses();

        if (statuses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse<>(false, 204, "No se encontraron estados en el sistema", null));
        }

        // Mapear a DTOs
        List<StatusResponse> statusResponses = statuses.stream()
                .map(statusMapper::toStatusResponse)
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(true, 200, "Estados obtenidos exitosamente", statusResponses, statusResponses.size()));
    }

    @GetMapping("/{id}")
    @Operation(
    summary = "Obtener estado por ID",
    description = "Retorna un estado específico según su identificador único. " +
                  "Útil para verificar si un estado existe antes de asignarlo a un producto o compra, " +
                  "o para obtener el nombre descriptivo de un estado dado su ID. " +
                  "Estados comunes: " +
                  "• 1: Activo (para productos disponibles) " +
                  "• 2: Inactivo (para productos no disponibles) " +
                  "• 3: Pendiente (para compras en proceso) " +
                  "• 4: Completado (para compras finalizadas) " +
                  "• 5: Cancelado (para compras canceladas) " +
                  "• 6: En envío (para compras en tránsito)"
                )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Estado encontrado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Estado encontrado exitosamente",
                      "data": {
                        "statusId": 1,
                        "name": "Activo"
                      }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Estado no encontrado - El ID proporcionado no existe",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 404,
                      "message": "Estado con ID 999 no encontrado"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<StatusResponse>> getStatusById(
            @Parameter(description = "ID del estado a buscar", example = "1", required = true)
            @PathVariable Long id) {
        return statusService.getStatusById(id)
                .map(status -> {
                    StatusResponse response = statusMapper.toStatusResponse(status);
                    return ResponseEntity.ok(new ApiResponse<>(true, 200, "Estado encontrado exitosamente", response));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, 404, "Estado con ID " + id + " no encontrado", null)));
    }
}