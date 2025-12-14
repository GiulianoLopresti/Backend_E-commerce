package com.looprex.shopping.controller;

import com.looprex.shopping.dto.ApiResponse;
import com.looprex.shopping.dto.BuyResponse;
import com.looprex.shopping.mapper.BuyMapper;
import com.looprex.shopping.model.Buy;
import com.looprex.shopping.service.BuyService;
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
@RequestMapping("/api/buys")
@Tag(
    name = "Compras", 
    description = "Endpoints para la gestión completa de órdenes de compra del e-commerce. " +
                  "Permite crear nuevas compras, consultar historial de órdenes, actualizar estados de envío, " +
                  "filtrar por usuario o estado, y gestionar todo el ciclo de vida de una orden desde su creación " +
                  "hasta su finalización. Cada compra incluye subtotal, IVA (19%), costos de envío y método de pago. " +
                  "Las compras se relacionan con usuarios (microservicio users), direcciones (microservicio geography) " +
                  "y estados (microservicio products)"
)

public class BuyController {

    private final BuyService buyService;
    private final BuyMapper buyMapper;
    
    private static final String BUY_WITH_ID = "Compra con ID ";
    private static final String NOT_FOUND = " no encontrada";

    public BuyController(BuyService buyService, BuyMapper buyMapper) {
        this.buyService = buyService;
        this.buyMapper = buyMapper;
    }

    @GetMapping
    @Operation(
    summary = "Obtener todas las compras",
    description = "Retorna una lista completa de todas las compras registradas en el sistema, ordenadas por fecha " +
                  "descendente (más reciente primero). Cada compra incluye: número de orden único, fechas, " +
                  "desglose de montos (subtotal, IVA 19%, envío, total), método de pago utilizado, y referencias " +
                  "a usuario, dirección de envío y estado actual. Este endpoint es útil para: " +
                  "1) Panel administrativo para gestionar todas las órdenes, " +
                  "2) Reportes de ventas y análisis de negocio, " +
                  "3) Auditoría y seguimiento de transacciones. " +
                  "Los montos están expresados en pesos chilenos (CLP) sin decimales. " +
                  "Si no hay compras registradas, retorna status 204 (No Content)"
                )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lista de compras obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Compras obtenidas exitosamente",
                      "data": [
                        {
                          "buy_id": 1,
                          "order_number": "ORD-2025-001",
                          "buy_date": 1734134400000,
                          "subtotal": 1899990,
                          "iva": 361098,
                          "shipping": 5990,
                          "total": 2267078,
                          "payment_method": "Tarjeta de Débito",
                          "status_id": 4,
                          "address_id": 1,
                          "user_id": 1
                        },
                        {
                          "buy_id": 2,
                          "order_number": "ORD-2025-002",
                          "buy_date": 1734048000000,
                          "subtotal": 729980,
                          "iva": 138696,
                          "shipping": 5990,
                          "total": 874666,
                          "payment_method": "Tarjeta de Crédito",
                          "status_id": 2,
                          "address_id": 2,
                          "user_id": 2
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
            description = "No hay compras en el sistema - Base de datos vacía o sin órdenes registradas",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 204,
                      "message": "No se encontraron compras en el sistema",
                      "data": null
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<List<BuyResponse>>> getAllBuys() {
        List<Buy> buys = buyService.getAllBuys();

        if (buys.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse<>(false, 204, "No se encontraron compras en el sistema", null));
        }

        List<BuyResponse> responses = buyMapper.toResponseList(buys);
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "Compras obtenidas exitosamente", responses, responses.size()));
    }


    @GetMapping("/{id}")
    @Operation(
    summary = "Obtener compra por ID",
    description = "Retorna los detalles completos de una compra específica según su identificador único. " +
                  "Incluye toda la información de la orden: número de orden, fecha de compra (timestamp en milisegundos), " +
                  "desglose financiero completo (subtotal sin IVA, IVA del 19%, costo de envío, total final), " +
                  "método de pago utilizado, y referencias a: usuario que realizó la compra, dirección de envío, " +
                  "y estado actual de la orden (pendiente, completado, en envío, etc.). " +
                  "Este endpoint es esencial para: " +
                  "1) Mostrar detalles de orden al cliente, " +
                  "2) Gestión administrativa de pedidos, " +
                  "3) Generación de facturas o comprobantes. " +
                  "Si el ID no existe, retorna error 404"
                )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Compra encontrada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Compra encontrada exitosamente",
                      "data": {
                        "buy_id": 1,
                        "order_number": "ORD-2025-001",
                        "buy_date": 1734134400000,
                        "subtotal": 1899990,
                        "iva": 361098,
                        "shipping": 5990,
                        "total": 2267078,
                        "payment_method": "Tarjeta de Débito",
                        "status_id": 4,
                        "address_id": 1,
                        "user_id": 1
                      }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Compra no encontrada - El ID proporcionado no existe en la base de datos",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 404,
                      "message": "Compra con ID 999 no encontrada"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<BuyResponse>> getBuyById(
            @Parameter(
                description = "ID único de la compra a buscar. Se utiliza para recuperar los detalles completos de una orden específica",
                example = "1",
                required = true
            )
            @PathVariable Long id) {
        return buyService.getBuyById(id)
                .map(buy -> ResponseEntity.ok(new ApiResponse<>(true, 200, "Compra encontrada exitosamente", buyMapper.toResponse(buy))))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, 404, BUY_WITH_ID + id + NOT_FOUND, null)));
    }

    @GetMapping("/order/{orderNumber}")
    @Operation(
    summary = "Obtener compra por número de orden",
    description = "Retorna una compra específica buscándola por su número de orden único (formato: ORD-YYYY-###). " +
                  "El número de orden es un identificador más amigable que el ID numérico y es el que típicamente " +
                  "se comparte con los clientes para seguimiento de pedidos. Este endpoint es ideal para: " +
                  "1) Sistemas de seguimiento de pedidos donde el cliente solo conoce su número de orden, " +
                  "2) Búsquedas rápidas en servicio al cliente, " +
                  "3) Integración con sistemas externos que referencian pedidos por número de orden. " +
                  "Si el número de orden no existe o tiene formato incorrecto, retorna error 404"
                )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Compra encontrada exitosamente mediante número de orden",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Compra encontrada exitosamente",
                      "data": {
                        "buy_id": 1,
                        "order_number": "ORD-2025-001",
                        "buy_date": 1734134400000,
                        "subtotal": 1899990,
                        "iva": 361098,
                        "shipping": 5990,
                        "total": 2267078,
                        "payment_method": "Tarjeta de Débito",
                        "status_id": 4,
                        "address_id": 1,
                        "user_id": 1
                      }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Orden no encontrada - El número de orden proporcionado no existe en el sistema",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 404,
                      "message": "Compra con número de orden ORD-2025-999 no encontrada"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<BuyResponse>> getBuyByOrderNumber(
            @Parameter(
                description = "Número único de orden en formato ORD-YYYY-### (ejemplo: ORD-2025-001). " +
                "Este número se genera automáticamente al crear una compra y es visible para el cliente",
                example = "ORD-2025-001",
                required = true
            )
            @PathVariable String orderNumber) {
        return buyService.getBuyByOrderNumber(orderNumber)
                .map(buy -> ResponseEntity.ok(new ApiResponse<>(true, 200, "Compra encontrada exitosamente", buyMapper.toResponse(buy))))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, 404, "Compra con número de orden " + orderNumber + NOT_FOUND, null)));
    }


    @GetMapping("/user/{userId}")
    @Operation(
    summary = "Obtener compras por usuario",
    description = "Retorna todas las compras realizadas por un usuario específico, ordenadas cronológicamente " +
                  "(más reciente primero). Antes de consultar las compras, valida que el usuario exista en el " +
                  "microservicio de usuarios. Este endpoint es fundamental para: " +
                  "1) Historial de compras del cliente en su perfil, " +
                  "2) Gestión de pedidos por cliente desde panel administrativo, " +
                  "3) Análisis de comportamiento de compra por usuario, " +
                  "4) Generación de reportes de cliente. " +
                  "Si el usuario no existe, retorna 404. Si el usuario existe pero no tiene compras, retorna 204"
                )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Compras del usuario obtenidas exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Compras del usuario obtenidas exitosamente",
                      "data": [
                        {
                          "buy_id": 1,
                          "order_number": "ORD-2025-001",
                          "buy_date": 1734134400000,
                          "subtotal": 1899990,
                          "iva": 361098,
                          "shipping": 5990,
                          "total": 2267078,
                          "payment_method": "Tarjeta de Débito",
                          "status_id": 4,
                          "address_id": 1,
                          "user_id": 1
                        }
                      ],
                      "count": 1
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "El usuario existe pero no tiene compras registradas",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 204,
                      "message": "El usuario no tiene compras",
                      "data": null
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrado - El ID proporcionado no existe en el microservicio de usuarios",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 404,
                      "message": "El usuario con ID 999 no existe"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<List<BuyResponse>>> getBuysByUser(
            @Parameter(
                description = "ID del usuario del cual se desean obtener las compras. " +
                "El sistema valida automáticamente que el usuario exista antes de buscar sus compras",
                example = "1",
                required = true
            )
            @PathVariable Long userId) {
        try {
            List<Buy> buys = buyService.getBuysByUser(userId);

            if (buys.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse<>(false, 204, "El usuario no tiene compras", null));
            }

            List<BuyResponse> responses = buyMapper.toResponseList(buys);
            return ResponseEntity.ok(new ApiResponse<>(true, 200, "Compras del usuario obtenidas exitosamente", responses, responses.size()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, 404, e.getMessage(), null));
        }
    }

    @GetMapping("/status/{statusId}")
    @Operation(
    summary = "Obtener compras por estado",
    description = "Retorna todas las compras que tienen un estado específico, ordenadas por fecha descendente. " +
                  "Los estados típicos son: 1-Activo, 2-Completado, 3-Pendiente, 4-En envío, 5-Cancelado. " +
                  "Este endpoint valida que el estado exista en el microservicio de productos antes de consultar. " +
                  "Es esencial para: " +
                  "1) Filtrar pedidos pendientes de procesamiento, " +
                  "2) Ver todas las compras en tránsito, " +
                  "3) Reportes de órdenes completadas o canceladas, " +
                  "4) Workflow de gestión de pedidos por estado. " +
                  "Si el estado no existe, retorna 404. Si existe pero no hay compras con ese estado, retorna 204"
                )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Compras con el estado especificado obtenidas exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Compras por estado obtenidas exitosamente",
                      "data": [
                        {
                          "buy_id": 1,
                          "order_number": "ORD-2025-001",
                          "buy_date": 1734134400000,
                          "subtotal": 1899990,
                          "iva": 361098,
                          "shipping": 5990,
                          "total": 2267078,
                          "payment_method": "Tarjeta de Débito",
                          "status_id": 4,
                          "address_id": 1,
                          "user_id": 1
                        }
                      ],
                      "count": 1
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "No hay compras con el estado especificado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 204,
                      "message": "No hay compras con este estado",
                      "data": null
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Estado no encontrado - El ID proporcionado no existe en el microservicio de productos",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 404,
                      "message": "El estado con ID 999 no existe"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<List<BuyResponse>>> getBuysByStatus(
            @Parameter(
                description = "ID del estado para filtrar compras. Estados comunes: 1-Activo, 2-Completado, 3-Pendiente, 4-En envío, 5-Cancelado",
                example = "4",
                required = true
            )
            @PathVariable Long statusId) {
        try {
            List<Buy> buys = buyService.getBuysByStatus(statusId);

            if (buys.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse<>(false, 204, "No hay compras con este estado", null));
            }

            List<BuyResponse> responses = buyMapper.toResponseList(buys);
            return ResponseEntity.ok(new ApiResponse<>(true, 200, "Compras por estado obtenidas exitosamente", responses, responses.size()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, 404, e.getMessage(), null));
        }
    }

    @PostMapping
    @Operation(
    summary = "Crear nueva compra",
    description = "Crea una nueva orden de compra en el sistema. Realiza validaciones exhaustivas antes de crear: " +
                  "1) Valida que el número de orden sea único (no exista otra compra con el mismo número), " +
                  "2) Verifica que el usuario exista en el microservicio users, " +
                  "3) Verifica que la dirección de envío exista en el microservicio geography, " +
                  "4) Verifica que el estado exista en el microservicio products, " +
                  "5) Valida que todos los montos sean >= 0, " +
                  "6) Valida que el método de pago no esté vacío. " +
                  "Si no se proporciona fecha de compra (buy_date), se establece automáticamente al momento actual. " +
                  "Los montos deben ser en pesos chilenos (CLP) sin decimales. " +
                  "El número de orden debe seguir el formato recomendado: ORD-YYYY-### " +
                  "pero cualquier string único es válido"
                )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Compra creada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 201,
                      "message": "Compra creada exitosamente",
                      "data": {
                        "buy_id": 3,
                        "order_number": "ORD-2025-003",
                        "buy_date": 1734307200000,
                        "subtotal": 599990,
                        "iva": 113998,
                        "shipping": 5990,
                        "total": 719978,
                        "payment_method": "Tarjeta de Crédito",
                        "status_id": 3,
                        "address_id": 1,
                        "user_id": 1
                      }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos - Errores de validación múltiples posibles",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Número de orden vacío",
                        value = """
                        {
                          "success": false,
                          "statusCode": 400,
                          "message": "El número de orden no puede estar vacío"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Número de orden duplicado",
                        value = """
                        {
                          "success": false,
                          "statusCode": 400,
                          "message": "Ya existe una compra con el número de orden ORD-2025-001"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Usuario no existe",
                        value = """
                        {
                          "success": false,
                          "statusCode": 400,
                          "message": "El usuario con ID 999 no existe"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Dirección no existe",
                        value = """
                        {
                          "success": false,
                          "statusCode": 400,
                          "message": "La dirección con ID 999 no existe"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Estado no existe",
                        value = """
                        {
                          "success": false,
                          "statusCode": 400,
                          "message": "El estado con ID 999 no existe"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Monto negativo",
                        value = """
                        {
                          "success": false,
                          "statusCode": 400,
                          "message": "El subtotal debe ser mayor o igual a 0"
                        }
                        """
                    )
                }
            )
        )
    })
    public ResponseEntity<ApiResponse<BuyResponse>> createBuy(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos completos de la nueva compra a crear. " +
                          "IMPORTANTE: No incluir buy_id (se genera automáticamente). " +
                          "Si no se proporciona buy_date, se establece al momento actual. " +
                          "Todos los montos en CLP sin decimales. " +
                          "El sistema valida automáticamente que user_id, address_id y status_id existan en sus microservicios respectivos",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BuyResponse.class),
                examples = @ExampleObject(
                    name = "Compra completa",
                    value = """
                    {
                      "order_number": "ORD-2025-003",
                      "buy_date": 1734307200000,
                      "subtotal": 599990,
                      "iva": 113998,
                      "shipping": 5990,
                      "total": 719978,
                      "payment_method": "Tarjeta de Crédito",
                      "status_id": 3,
                      "address_id": 1,
                      "user_id": 1
                    }
                    """
                )
            )
        )
            @RequestBody BuyResponse buyRequest) {
        try {
            Buy buy = buyMapper.toEntity(buyRequest);
            Buy createdBuy = buyService.createBuy(buy);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, 201, "Compra creada exitosamente", buyMapper.toResponse(createdBuy)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, 400, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    @Operation(
    summary = "Actualizar compra",
    description = "Actualiza los datos de una compra existente. Típicamente se usa para actualizar el estado de la orden " +
                  "(ej: de 'Pendiente' a 'En envío' o 'Completado') o el método de pago. " +
                  "Solo se actualizan los campos proporcionados en el request (actualización parcial). " +
                  "Si se proporciona un nuevo status_id, el sistema valida que exista en el microservicio de productos. " +
                  "NO se recomienda modificar montos (subtotal, IVA, total) de órdenes ya creadas por integridad de datos. " +
                  "El número de orden (order_number) y la fecha (buy_date) NO se pueden modificar por diseño"
                )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Compra actualizada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Compra actualizada exitosamente",
                      "data": {
                        "buy_id": 1,
                        "order_number": "ORD-2025-001",
                        "buy_date": 1734134400000,
                        "subtotal": 1899990,
                        "iva": 361098,
                        "shipping": 5990,
                        "total": 2267078,
                        "payment_method": "Transferencia Bancaria",
                        "status_id": 2,
                        "address_id": 1,
                        "user_id": 1
                      }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Compra no encontrada - El ID proporcionado no existe en la base de datos",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 404,
                      "message": "Compra con ID 999 no encontrada"
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos inválidos - Estado no existe u otro error de validación",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 400,
                      "message": "El estado con ID 999 no existe"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<BuyResponse>> updateBuy(
            @Parameter(
                description = "ID único de la compra a actualizar",
                example = "1",
                required = true
            )
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos a actualizar. Típicamente solo se actualiza status_id o payment_method. " +
                          "Todos los campos son opcionales - solo se actualizarán los proporcionados",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BuyResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Actualizar estado",
                        value = """
                        {
                          "status_id": 2
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Actualizar método de pago",
                        value = """
                        {
                          "payment_method": "Transferencia Bancaria"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Actualizar ambos",
                        value = """
                        {
                          "status_id": 4,
                          "payment_method": "Tarjeta de Crédito"
                        }
                        """
                    )
                }
            )
        )
            @RequestBody BuyResponse buyRequest) {
        try {
            Buy buy = buyMapper.toEntity(buyRequest);
            return buyService.updateBuy(id, buy)
                    .map(updatedBuy -> ResponseEntity.ok(
                            new ApiResponse<>(true, 200, "Compra actualizada exitosamente", buyMapper.toResponse(updatedBuy))))
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
    description = "Elimina permanentemente una compra del sistema. ADVERTENCIA: Esta operación es irreversible y " +
                  "eliminará toda la información de la orden. En ambientes de producción, se recomienda usar " +
                  "actualización de estado a 'Cancelado' en lugar de eliminación física para mantener el historial. " +
                  "Los detalles de la compra (tabla details) deben eliminarse primero o manejar la cascada según " +
                  "la configuración de la base de datos. Si la compra no existe, retorna 404"
                )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Compra eliminada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Compra eliminada exitosamente",
                      "data": null
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Compra no encontrada - El ID proporcionado no existe en la base de datos",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 404,
                      "message": "Compra con ID 999 no encontrada"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Void>> deleteBuy(
            @Parameter(
                description = "ID único de la compra a eliminar. ADVERTENCIA: La eliminación es permanente e irreversible",
                example = "1",
                required = true
            )
            @PathVariable Long id) {
        if (buyService.deleteBuy(id)) {
            return ResponseEntity.ok(new ApiResponse<>(true, 200, "Compra eliminada exitosamente", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, 404, BUY_WITH_ID + id + NOT_FOUND, null));
    }
}