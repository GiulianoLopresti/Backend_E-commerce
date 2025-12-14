package com.looprex.shopping.controller;

import com.looprex.shopping.dto.ApiResponse;
import com.looprex.shopping.dto.DetailResponse;
import com.looprex.shopping.mapper.DetailMapper;
import com.looprex.shopping.model.Detail;
import com.looprex.shopping.service.DetailService;
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
@RequestMapping("/api/details")
@Tag(
    name = "Detalles de Compra", 
    description = "Endpoints para la gestión de detalles/líneas de productos de las compras. " +
                  "Cada detalle representa un producto específico dentro de una orden de compra, " +
                  "incluyendo cantidad, precio unitario al momento de la compra y subtotal. " +
                  "Este módulo permite consultar el historial de ventas por producto, " +
                  "gestionar los items de cada compra y analizar el detalle de las transacciones"
)
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
        summary = "Obtener todos los detalles de compra",
        description = "Retorna una lista completa de todos los detalles de compra registrados en el sistema. " +
                      "Cada detalle representa una línea de producto dentro de una orden, mostrando " +
                      "la cantidad comprada, precio unitario al momento de la venta y subtotal. " +
                      "Este endpoint es útil para reportes generales o auditorías del sistema. " +
                      "Si no hay detalles registrados, retorna un status 204 (No Content)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lista de detalles obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Lista de detalles",
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Detalles obtenidos exitosamente",
                      "data": [
                        {
                          "detail_id": 1,
                          "buy_id": 1,
                          "product_id": 1,
                          "quantity": 2,
                          "unit_price": 1899990,
                          "subtotal": 3799980
                        },
                        {
                          "detail_id": 2,
                          "buy_id": 1,
                          "product_id": 3,
                          "quantity": 1,
                          "unit_price": 49990,
                          "subtotal": 49990
                        }
                      ],
                      "count": 2
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "No hay detalles en el sistema - Base de datos vacía",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Sistema sin detalles",
                    value = """
                    {
                      "success": false,
                      "statusCode": 204,
                      "message": "No se encontraron detalles en el sistema",
                      "data": null,
                      "count": 0
                    }
                    """
                )
            )
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
        description = "Retorna un detalle de compra específico según su identificador único. " +
                      "Incluye toda la información de la línea: producto comprado, cantidad, " +
                      "precio unitario al momento de la venta y subtotal calculado. " +
                      "Este endpoint es útil para consultar información específica de una línea " +
                      "dentro de una factura o para auditorías detalladas"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Detalle encontrado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Detalle específico",
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Detalle encontrado exitosamente",
                      "data": {
                        "detail_id": 1,
                        "buy_id": 1,
                        "product_id": 1,
                        "quantity": 2,
                        "unit_price": 1899990,
                        "subtotal": 3799980
                      }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Detalle no encontrado - El ID proporcionado no existe en la base de datos",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 404,
                      "message": "Detalle con ID 999 no encontrado"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<DetailResponse>> getDetailById(
            @Parameter(
                description = "ID único del detalle a buscar. Corresponde al identificador de la línea de producto en la compra",
                example = "1",
                required = true
            )
            @PathVariable Long id) {
        return detailService.getDetailById(id)
                .map(detail -> ResponseEntity.ok(new ApiResponse<>(true, 200, "Detalle encontrado exitosamente", detailMapper.toResponse(detail))))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, 404, DETAIL_WITH_ID + id + NOT_FOUND, null)));
    }

    @GetMapping("/buy/{buyId}")
    @Operation(
        summary = "Obtener detalles por compra",
        description = "Retorna todos los detalles/líneas de productos asociados a una compra específica. " +
                      "Muestra el desglose completo de la orden: cada producto comprado, cantidades, " +
                      "precios unitarios y subtotales. Es equivalente a ver el detalle de una factura. " +
                      "Valida que la compra exista en el microservicio antes de retornar los datos. " +
                      "Si la compra existe pero no tiene productos (caso raro), retorna 204"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Detalles de la compra obtenidos exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Detalles de una compra",
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Detalles de la compra obtenidos exitosamente",
                      "data": [
                        {
                          "detail_id": 1,
                          "buy_id": 1,
                          "product_id": 1,
                          "quantity": 1,
                          "unit_price": 1899990,
                          "subtotal": 1899990
                        },
                        {
                          "detail_id": 2,
                          "buy_id": 1,
                          "product_id": 2,
                          "quantity": 2,
                          "unit_price": 129990,
                          "subtotal": 259980
                        }
                      ],
                      "count": 2
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "La compra existe pero no tiene detalles asociados",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 204,
                      "message": "La compra no tiene detalles",
                      "data": null,
                      "count": 0
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Compra no encontrada - El ID de compra proporcionado no existe",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 404,
                      "message": "La compra con ID 999 no existe"
                    }
                    """
                )
            )
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
        description = "Retorna todas las ventas/compras en las que aparece un producto específico. " +
                      "Útil para analizar el historial de ventas de un producto: cuántas veces se ha vendido, " +
                      "en qué cantidades, a qué precios y en qué fechas (indirectamente via buy_id). " +
                      "Valida que el producto exista en el microservicio de productos antes de retornar datos. " +
                      "Si el producto existe pero nunca se ha vendido, retorna 204"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Historial de ventas del producto obtenido exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Historial de ventas",
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Detalles del producto obtenidos exitosamente",
                      "data": [
                        {
                          "detail_id": 1,
                          "buy_id": 1,
                          "product_id": 1,
                          "quantity": 1,
                          "unit_price": 1899990,
                          "subtotal": 1899990
                        },
                        {
                          "detail_id": 5,
                          "buy_id": 3,
                          "product_id": 1,
                          "quantity": 2,
                          "unit_price": 1899990,
                          "subtotal": 3799980
                        }
                      ],
                      "count": 2
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "El producto existe pero no tiene ventas registradas",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 204,
                      "message": "El producto no tiene ventas",
                      "data": null,
                      "count": 0
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado - El ID proporcionado no existe en el microservicio de productos",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 404,
                      "message": "El producto con ID 999 no existe"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<List<DetailResponse>>> getDetailsByProduct(
            @Parameter(
                description = "ID único del producto para consultar su historial de ventas. " +
                              "Valida existencia en el microservicio de productos",
                example = "1",
                required = true
            )
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
        summary = "Crear nuevo detalle de compra",
        description = "Registra una nueva línea de producto en una compra existente. " +
                      "Valida que tanto la compra (buy_id) como el producto (product_id) existan en sus " +
                      "respectivos microservicios antes de crear el registro. " +
                      "La cantidad debe ser al menos 1, los precios deben ser mayores o iguales a 0, " +
                      "y el subtotal debe coincidir con (quantity × unit_price). " +
                      "Este endpoint es usado durante el proceso de checkout al registrar cada producto del carrito"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Detalle creado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Detalle creado",
                    value = """
                    {
                      "success": true,
                      "statusCode": 201,
                      "message": "Detalle creado exitosamente",
                      "data": {
                        "detail_id": 15,
                        "buy_id": 5,
                        "product_id": 2,
                        "quantity": 3,
                        "unit_price": 129990,
                        "subtotal": 389970
                      }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos - Errores de validación",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Buy ID faltante",
                        value = """
                        {
                          "success": false,
                          "statusCode": 400,
                          "message": "El detalle debe estar asociado a una compra"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Product ID faltante",
                        value = """
                        {
                          "success": false,
                          "statusCode": 400,
                          "message": "El detalle debe tener un producto"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Cantidad inválida",
                        value = """
                        {
                          "success": false,
                          "statusCode": 400,
                          "message": "La cantidad debe ser al menos 1"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Compra no existe",
                        value = """
                        {
                          "success": false,
                          "statusCode": 400,
                          "message": "La compra con ID 999 no existe"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Producto no existe",
                        value = """
                        {
                          "success": false,
                          "statusCode": 400,
                          "message": "El producto con ID 999 no existe"
                        }
                        """
                    )
                }
            )
        )
    })
    public ResponseEntity<ApiResponse<DetailResponse>> createDetail(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos del nuevo detalle a crear. Todos los campos son requeridos. " +
                              "El sistema valida que buy_id y product_id existan en sus microservicios respectivos. " +
                              "quantity debe ser >= 1, unit_price y subtotal deben ser >= 0",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DetailResponse.class),
                    examples = @ExampleObject(
                        name = "Detalle completo",
                        value = """
                        {
                          "buy_id": 5,
                          "product_id": 2,
                          "quantity": 3,
                          "unit_price": 129990,
                          "subtotal": 389970
                        }
                        """
                    )
                )
            )
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
        summary = "Actualizar detalle de compra",
        description = "Permite actualizar los datos de un detalle de compra existente. " +
                      "Típicamente usado para corregir errores o ajustar cantidades post-venta. " +
                      "Solo se actualizan los campos proporcionados (actualización parcial). " +
                      "Si se cambia product_id, valida que el nuevo producto exista en el microservicio. " +
                      "Mantiene las mismas validaciones que en creación: quantity >= 1, precios >= 0"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Detalle actualizado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Detalle actualizado",
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Detalle actualizado exitosamente",
                      "data": {
                        "detail_id": 1,
                        "buy_id": 1,
                        "product_id": 1,
                        "quantity": 3,
                        "unit_price": 1899990,
                        "subtotal": 5699970
                      }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Detalle no encontrado - El ID proporcionado no existe",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 404,
                      "message": "Detalle con ID 999 no encontrado"
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos - Errores de validación",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Cantidad inválida",
                        value = """
                        {
                          "success": false,
                          "statusCode": 400,
                          "message": "La cantidad debe ser al menos 1"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Producto no existe",
                        value = """
                        {
                          "success": false,
                          "statusCode": 400,
                          "message": "El producto con ID 999 no existe"
                        }
                        """
                    )
                }
            )
        )
    })
    public ResponseEntity<ApiResponse<DetailResponse>> updateDetail(
            @Parameter(
                description = "ID único del detalle a actualizar",
                example = "1",
                required = true
            )
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos a actualizar del detalle. Todos los campos son opcionales - " +
                              "solo se actualizarán los campos proporcionados. Mantiene las mismas validaciones",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DetailResponse.class),
                    examples = {
                        @ExampleObject(
                            name = "Actualización completa",
                            value = """
                            {
                              "quantity": 3,
                              "unit_price": 1899990,
                              "subtotal": 5699970
                            }
                            """
                        ),
                        @ExampleObject(
                            name = "Actualización parcial (solo cantidad)",
                            value = """
                            {
                              "quantity": 5
                            }
                            """
                        )
                    }
                )
            )
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
        summary = "Eliminar detalle de compra",
        description = "Elimina un detalle/línea de producto de una compra. " +
                      "Esta operación es permanente y no se puede deshacer. " +
                      "Típicamente usado para corregir errores administrativos o " +
                      "gestionar devoluciones/cancelaciones parciales de órdenes. " +
                      "IMPORTANTE: Esta operación NO actualiza automáticamente los totales de la compra asociada"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Detalle eliminado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Detalle eliminado exitosamente",
                      "data": null
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Detalle no encontrado - El ID proporcionado no existe en la base de datos",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 404,
                      "message": "Detalle con ID 999 no encontrado"
                    }
                    """
                )
            )
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