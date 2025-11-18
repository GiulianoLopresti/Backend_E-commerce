package com.looprex.shopping.controller;

import com.looprex.shopping.dto.ApiResponse;
import com.looprex.shopping.model.Buy;
import com.looprex.shopping.service.BuyService;
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
@RequestMapping("/api/buys")
@Tag(name = "Compras", description = "Endpoints para gestionar las órdenes de compra del e-commerce")
public class BuyController {

    private final BuyService buyService;
    
    private static final String BUY_WITH_ID = "Compra con ID ";
    private static final String NOT_FOUND = " no encontrada";

    public BuyController(BuyService buyService) {
        this.buyService = buyService;
    }

    @GetMapping
    @Operation(
        summary = "Obtener todas las compras",
        description = "Retorna una lista completa de todas las compras ordenadas por fecha descendente (más reciente primero)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lista de compras obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "No hay compras en el sistema",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<List<Buy>>> getAllBuys() {
        List<Buy> buys = buyService.getAllBuys();

        if (buys.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse<>(false, 204, "No se encontraron compras en el sistema", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, 200, "Compras obtenidas exitosamente", buys, buys.size()));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener compra por ID",
        description = "Retorna una compra específica según su identificador único"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Compra encontrada exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Compra no encontrada",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<Buy>> getBuyById(
            @Parameter(description = "ID de la compra a buscar", example = "1", required = true)
            @PathVariable Long id) {
        return buyService.getBuyById(id)
                .map(buy -> ResponseEntity.ok(new ApiResponse<>(true, 200, "Compra encontrada exitosamente", buy)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, 404, BUY_WITH_ID + id + NOT_FOUND, null)));
    }

    @GetMapping("/order/{orderNumber}")
    @Operation(
        summary = "Obtener compra por número de orden",
        description = "Retorna una compra específica según su número de orden único"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Compra encontrada exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Compra no encontrada",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<Buy>> getBuyByOrderNumber(
            @Parameter(description = "Número de orden de la compra", example = "ORD-2025-001", required = true)
            @PathVariable String orderNumber) {
        return buyService.getBuyByOrderNumber(orderNumber)
                .map(buy -> ResponseEntity.ok(new ApiResponse<>(true, 200, "Compra encontrada exitosamente", buy)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, 404, "Compra con número de orden " + orderNumber + NOT_FOUND, null)));
    }

    @GetMapping("/user/{userId}")
    @Operation(
        summary = "Obtener compras por usuario",
        description = "Retorna todas las compras realizadas por un usuario específico"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Compras del usuario obtenidas exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "El usuario no tiene compras",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrado",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<List<Buy>>> getBuysByUser(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Long userId) {
        try {
            List<Buy> buys = buyService.getBuysByUser(userId);

            if (buys.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse<>(false, 204, "El usuario no tiene compras", null));
            }

            return ResponseEntity.ok(new ApiResponse<>(true, 200, "Compras del usuario obtenidas exitosamente", buys, buys.size()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, 404, e.getMessage(), null));
        }
    }

    @GetMapping("/status/{statusId}")
    @Operation(
        summary = "Obtener compras por estado",
        description = "Retorna todas las compras que tienen un estado específico"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Compras por estado obtenidas exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "No hay compras con este estado",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<List<Buy>>> getBuysByStatus(
            @Parameter(description = "ID del estado", example = "1", required = true)
            @PathVariable Long statusId) {
        try {
            List<Buy> buys = buyService.getBuysByStatus(statusId);

            if (buys.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse<>(false, 204, "No hay compras con este estado", null));
            }

            return ResponseEntity.ok(new ApiResponse<>(true, 200, "Compras por estado obtenidas exitosamente", buys, buys.size()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, 404, e.getMessage(), null));
        }
    }

    @PostMapping
    @Operation(
        summary = "Crear nueva compra",
        description = "Crea una nueva orden de compra. Valida que el usuario, dirección y estado existan en sus respectivos microservicios"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Compra creada exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<Buy>> createBuy(
            @Parameter(description = "Datos de la compra a crear", required = true)
            @RequestBody Buy buy) {
        try {
            Buy createdBuy = buyService.createBuy(buy);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, 201, "Compra creada exitosamente", createdBuy));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, 400, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar compra",
        description = "Actualiza los datos de una compra existente (principalmente el estado)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Compra actualizada exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Compra no encontrada",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<Buy>> updateBuy(
            @Parameter(description = "ID de la compra a actualizar", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Datos actualizados de la compra", required = true)
            @RequestBody Buy buy) {
        try {
            return buyService.updateBuy(id, buy)
                    .map(updatedBuy -> ResponseEntity.ok(
                            new ApiResponse<>(true, 200, "Compra actualizada exitosamente", updatedBuy)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(false, 404, BUY_WITH_ID + id + NOT_FOUND, null)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, 400, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar compra",
        description = "Elimina una compra del sistema"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Compra eliminada exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Compra no encontrada",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<Void>> deleteBuy(
            @Parameter(description = "ID de la compra a eliminar", example = "1", required = true)
            @PathVariable Long id) {
        if (buyService.deleteBuy(id)) {
            return ResponseEntity.ok(new ApiResponse<>(true, 200, "Compra eliminada exitosamente", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, 404, BUY_WITH_ID + id + NOT_FOUND, null));
    }
}