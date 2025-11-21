package com.looprex.shopping.controller;

import com.looprex.shopping.dto.ApiResponse;
import com.looprex.shopping.dto.DetailResponse;
import com.looprex.shopping.mapper.DetailMapper;
import com.looprex.shopping.model.Detail;
import com.looprex.shopping.service.DetailService;
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
@RequestMapping("/api/details")
@Tag(name = "Detalles de Compra", description = "Endpoints para gestionar los detalles/líneas de productos de las compras")
public class DetailController {

    private final DetailService detailService;
    private final DetailMapper detailMapper;

    private static final String DETAIL_WITH_ID = "Detalle con ID ";
    private static final String NOT_FOUND = " no encontrado";

    public DetailController(DetailService detailService, DetailMapper detailMapper) {
        this.detailService = detailService;
        this.detailMapper = detailMapper;
    }

    @GetMapping
    @Operation(
        summary = "Obtener todos los detalles",
        description = "Retorna una lista completa de todos los detalles de compra en el sistema"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lista de detalles obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "No hay detalles en el sistema",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<List<DetailResponse>>> getAllDetails() {
        List<Detail> details = detailService.getAllDetails();

        if (details.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse<>(false, 204, "No se encontraron detalles en el sistema", null));
        }

        List<DetailResponse> responses = detailMapper.toResponseList(details);
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "Detalles obtenidos exitosamente", responses, responses.size()));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener detalle por ID",
        description = "Retorna un detalle de compra específico según su identificador único"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Detalle encontrado exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Detalle no encontrado",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<DetailResponse>> getDetailById(
            @Parameter(description = "ID del detalle a buscar", example = "1", required = true)
            @PathVariable Long id) {
        return detailService.getDetailById(id)
                .map(detail -> ResponseEntity.ok(new ApiResponse<>(true, 200, "Detalle encontrado exitosamente", detailMapper.toResponse(detail))))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, 404, DETAIL_WITH_ID + id + NOT_FOUND, null)));
    }

    @GetMapping("/buy/{buyId}")
    @Operation(
        summary = "Obtener detalles por compra",
        description = "Retorna todos los detalles/líneas de productos de una compra específica"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Detalles de la compra obtenidos exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "La compra no tiene detalles",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Compra no encontrada",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<List<DetailResponse>>> getDetailsByBuy(
            @Parameter(description = "ID de la compra", example = "1", required = true)
            @PathVariable Long buyId) {
        try {
            List<Detail> details = detailService.getDetailsByBuy(buyId);

            if (details.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse<>(false, 204, "La compra no tiene detalles", null));
            }

            List<DetailResponse> responses = detailMapper.toResponseList(details);
            return ResponseEntity.ok(new ApiResponse<>(true, 200, "Detalles de la compra obtenidos exitosamente", responses, responses.size()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, 404, e.getMessage(), null));
        }
    }

    @GetMapping("/product/{productId}")
    @Operation(
        summary = "Obtener detalles por producto",
        description = "Retorna todos los detalles de compras que incluyen un producto específico (historial de ventas del producto)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Detalles del producto obtenidos exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "El producto no tiene ventas",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<List<DetailResponse>>> getDetailsByProduct(
            @Parameter(description = "ID del producto", example = "1", required = true)
            @PathVariable Long productId) {
        try {
            List<Detail> details = detailService.getDetailsByProduct(productId);

            if (details.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse<>(false, 204, "El producto no tiene ventas", null));
            }

            List<DetailResponse> responses = detailMapper.toResponseList(details);
            return ResponseEntity.ok(new ApiResponse<>(true, 200, "Detalles del producto obtenidos exitosamente", responses, responses.size()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, 404, e.getMessage(), null));
        }
    }

    @PostMapping
    @Operation(
        summary = "Crear nuevo detalle",
        description = "Crea un nuevo detalle de compra. Valida que la compra y el producto existan"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Detalle creado exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<DetailResponse>> createDetail(
            @Parameter(description = "Datos del detalle a crear", required = true)
            @RequestBody DetailResponse detailRequest) {
        try {
            Detail detail = detailMapper.toEntity(detailRequest);
            Detail createdDetail = detailService.createDetail(detail);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, 201, "Detalle creado exitosamente", detailMapper.toResponse(createdDetail)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, 400, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar detalle",
        description = "Actualiza los datos de un detalle de compra existente"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Detalle actualizado exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Detalle no encontrado",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<DetailResponse>> updateDetail(
            @Parameter(description = "ID del detalle a actualizar", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Datos actualizados del detalle", required = true)
            @RequestBody DetailResponse detailRequest) {
        try {
            Detail detail = detailMapper.toEntity(detailRequest);
            return detailService.updateDetail(id, detail)
                    .map(updatedDetail -> ResponseEntity.ok(
                            new ApiResponse<>(true, 200, "Detalle actualizado exitosamente", detailMapper.toResponse(updatedDetail))))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(false, 404, DETAIL_WITH_ID + id + NOT_FOUND, null)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, 400, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar detalle",
        description = "Elimina un detalle de compra del sistema"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Detalle eliminado exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Detalle no encontrado",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<Void>> deleteDetail(
            @Parameter(description = "ID del detalle a eliminar", example = "1", required = true)
            @PathVariable Long id) {
        if (detailService.deleteDetail(id)) {
            return ResponseEntity.ok(new ApiResponse<>(true, 200, "Detalle eliminado exitosamente", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, 404, DETAIL_WITH_ID + id + NOT_FOUND, null));
    }
}