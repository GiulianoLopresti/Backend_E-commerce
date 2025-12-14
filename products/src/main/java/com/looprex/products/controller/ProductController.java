package com.looprex.products.controller;

import com.looprex.products.dto.ApiResponse;
import com.looprex.products.dto.ProductResponse;
import com.looprex.products.mapper.ProductMapper;
import com.looprex.products.model.Product;
import com.looprex.products.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(
    name = "Productos", 
    description = "Endpoints para la gestión completa del catálogo de productos del e-commerce. " +
                  "Incluye operaciones CRUD (crear, leer, actualizar, eliminar) de productos, " +
                  "búsqueda por nombre, filtrado por categoría y estado. Cada producto contiene " +
                  "información de inventario (stock), precios, descripción, foto y relaciones " +
                  "con categorías y estados"
)
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    private static final String NOT_FOUND = "No encontrada";
    private static final String PRODUCT_WITH_ID = "Producto con ID ";

    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @GetMapping
    @Operation(
        summary = "Obtener todos los productos",
        description = "Retorna una lista completa de todos los productos disponibles en el catálogo. " +
                      "Cada producto incluye sus datos básicos (nombre, descripción, precio, stock), " +
                      "además de objetos anidados con información de su categoría y estado. " +
                      "Útil para mostrar el catálogo completo en la tienda o en paneles administrativos. " +
                      "Si no hay productos registrados, retorna un status 204 (No Content)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lista de productos obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Productos obtenidos exitosamente",
                      "data": [
                        {
                          "productId": 1,
                          "stock": 15,
                          "productPhoto": "https://example.com/rtx4090.jpg",
                          "name": "ASUS ROG Strix RTX 4090",
                          "description": "Tarjeta gráfica de alto rendimiento con 24GB GDDR6X",
                          "price": 1899990,
                          "statusId": 1,
                          "categoryId": 1,
                          "category": {
                            "categoryId": 1,
                            "name": "Tarjetas de Graficas"
                          },
                          "status": {
                            "statusId": 1,
                            "name": "Activo"
                          }
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
            description = "No hay productos en el sistema - Catálogo vacío",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 204,
                      "message": "No se encontraron productos en el sistema"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        List<Product> products = productService.getAllProducts();

        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse<>(false, 204, "No se encontraron productos en el sistema", null));
        }

        // Mapear a DTOs
        List<ProductResponse> productResponses = products.stream()
                .map(productMapper::toProductResponse)
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(true, 200, "Productos obtenidos exitosamente", productResponses, productResponses.size()));
    }


    @GetMapping("/{id}")
    @Operation(
    summary = "Obtener producto por ID",
    description = "Retorna un producto específico según su identificador único. " +
                  "Incluye todos sus datos: información básica (nombre, descripción, precio, stock, foto), " +
                  "además de objetos completos de categoría y estado. " +
                  "Este endpoint es ideal para páginas de detalle de producto o para verificar " +
                  "información específica antes de agregar al carrito"
                )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Producto encontrado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Producto encontrado exitosamente",
                      "data": {
                        "productId": 1,
                        "stock": 15,
                        "productPhoto": "https://example.com/rtx4090.jpg",
                        "name": "ASUS ROG Strix RTX 4090",
                        "description": "Tarjeta gráfica de alto rendimiento con 24GB GDDR6X",
                        "price": 1899990,
                        "statusId": 1,
                        "categoryId": 1,
                        "category": {
                          "categoryId": 1,
                          "name": "Tarjetas de Graficas"
                        },
                        "status": {
                          "statusId": 1,
                          "name": "Activo"
                        }
                      }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado - El ID proporcionado no existe en el catálogo",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 404,
                      "message": "Producto con ID 999 no encontrado"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @Parameter(description = "ID del producto a buscar", example = "1", required = true)
            @PathVariable Long id) {
        return productService.getProductById(id)
                .map(product -> {
                    ProductResponse response = productMapper.toProductResponse(product);
                    return ResponseEntity.ok(new ApiResponse<>(true, 200, "Producto encontrado exitosamente", response));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, 404, PRODUCT_WITH_ID + id + NOT_FOUND, null)));
    }


    @GetMapping("/category/{categoryId}")
    @Operation(
    summary = "Obtener productos por categoría",
    description = "Retorna todos los productos que pertenecen a una categoría específica. " +
                  "Permite filtrar el catálogo por tipo de producto (ej: todas las tarjetas gráficas, " +
                  "toda la RAM, todos los procesadores). Útil para navegación por categorías " +
                  "en la tienda y para construir menús de productos. " +
                  "Si la categoría no tiene productos, retorna 204 (No Content)"
                )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lista de productos de la categoría obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Productos de la categoría obtenidos exitosamente",
                      "data": [
                        {
                          "productId": 1,
                          "stock": 15,
                          "name": "ASUS ROG Strix RTX 4090",
                          "price": 1899990,
                          "category": {
                            "categoryId": 1,
                            "name": "Tarjetas de Graficas"
                          }
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
            description = "No hay productos en esta categoría",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 204,
                      "message": "No se encontraron productos en esta categoría"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByCategory(
            @Parameter(description = "ID de la categoría", example = "1", required = true)
            @PathVariable Long categoryId) {
        List<Product> products = productService.getProductsByCategory(categoryId);

        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse<>(false, 204, "No se encontraron productos en esta categoría", null));
        }

        // Mapear a DTOs
        List<ProductResponse> productResponses = products.stream()
                .map(productMapper::toProductResponse)
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(true, 200, "Productos de la categoría obtenidos exitosamente", productResponses, productResponses.size()));
    }

    @GetMapping("/search")
    @Operation(
    summary = "Buscar productos por nombre",
    description = "Realiza una búsqueda de productos cuyo nombre contenga el texto especificado. " +
                  "La búsqueda es case-insensitive (no distingue mayúsculas/minúsculas) y busca " +
                  "coincidencias parciales. Por ejemplo, buscar 'RTX' encontrará 'RTX 4090', " +
                  "'RTX 3080', etc. Ideal para implementar barras de búsqueda en la tienda. " +
                  "Si no hay coincidencias, retorna 204 (No Content)"
                )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Búsqueda completada exitosamente - Se encontraron coincidencias",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Búsqueda completada exitosamente",
                      "data": [
                        {
                          "productId": 1,
                          "name": "ASUS ROG Strix RTX 4090",
                          "price": 1899990
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
            description = "No se encontraron productos que coincidan con la búsqueda",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 204,
                      "message": "No se encontraron productos que coincidan con la búsqueda"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProducts(
            @Parameter(description = "Texto a buscar en el nombre del producto", example = "RTX", required = true)
            @RequestParam String query) {
        List<Product> products = productService.searchProducts(query);

        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse<>(false, 204, "No se encontraron productos que coincidan con la búsqueda", null));
        }

        // Mapear a DTOs
        List<ProductResponse> productResponses = products.stream()
                .map(productMapper::toProductResponse)
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(true, 200, "Búsqueda completada exitosamente", productResponses, productResponses.size()));
    }


    @GetMapping("/status/{statusId}")
    @Operation(
    summary = "Obtener productos por estado",
    description = "Retorna todos los productos que tienen un estado específico. " +
                  "Los estados típicos son: Activo (1) - productos disponibles para venta, " +
                  "Inactivo (2) - productos no disponibles temporalmente. " +
                  "Este endpoint es útil para filtrar productos en el panel administrativo " +
                  "o para mostrar solo productos activos en la tienda. " +
                  "Si no hay productos con ese estado, retorna 204 (No Content)"
                )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lista de productos por estado obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Productos por estado obtenidos exitosamente",
                      "data": [
                        {
                          "productId": 1,
                          "name": "ASUS ROG Strix RTX 4090",
                          "status": {
                            "statusId": 1,
                            "name": "Activo"
                          }
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
            description = "No hay productos con este estado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 204,
                      "message": "No se encontraron productos con este estado"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByStatus(
            @Parameter(description = "ID del estado", example = "1", required = true)
            @PathVariable Long statusId) {
        List<Product> products = productService.getProductsByStatus(statusId);

        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse<>(false, 204, "No se encontraron productos con este estado", null));
        }

        // Mapear a DTOs
        List<ProductResponse> productResponses = products.stream()
                .map(productMapper::toProductResponse)
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(true, 200, "Productos por estado obtenidos exitosamente", productResponses, productResponses.size()));
    }


    @PostMapping
    @Operation(
    summary = "Crear nuevo producto",
    description = "Crea un nuevo producto en el catálogo. Requiere todos los campos obligatorios: " +
                  "nombre, descripción, precio, stock, categoryId y statusId. " +
                  "El precio y stock deben ser mayores o iguales a 0. " +
                  "La foto del producto (productPhoto) es opcional y debe ser una URL válida. " +
                  "Valida que la categoría y el estado existan antes de crear el producto. " +
                  "Después de la creación exitosa, retorna el producto completo con sus relaciones cargadas"
                )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Producto creado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 201,
                      "message": "Producto creado exitosamente",
                      "data": {
                        "productId": 10,
                        "stock": 25,
                        "productPhoto": "https://example.com/producto.jpg",
                        "name": "Nuevo Producto",
                        "description": "Descripción detallada del producto",
                        "price": 199990,
                        "statusId": 1,
                        "categoryId": 2,
                        "category": {
                          "categoryId": 2,
                          "name": "Ram"
                        },
                        "status": {
                          "statusId": 1,
                          "name": "Activo"
                        }
                      }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos - Campos obligatorios faltantes, valores negativos, categoría o estado inexistentes",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Nombre vacío",
                        value = """
                        {
                          "success": false,
                          "statusCode": 400,
                          "message": "El nombre del producto no puede estar vacío"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Categoría inexistente",
                        value = """
                        {
                          "success": false,
                          "statusCode": 400,
                          "message": "La categoría con ID 999 no existe"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Precio negativo",
                        value = """
                        {
                          "success": false,
                          "statusCode": 400,
                          "message": "El precio debe ser mayor o igual a 0"
                        }
                        """
                    )
                }
            )
        )
    })
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos completos del nuevo producto a crear. " +
            "Campos obligatorios: name, description, price, stock, categoryId, statusId. " +
            "Campo opcional: productPhoto (URL de la imagen). " +
            "El precio y stock deben ser >= 0",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Product.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "stock": 25,
                      "productPhoto": "https://example.com/producto.jpg",
                      "name": "Nuevo Producto",
                      "description": "Descripción detallada del producto con sus características principales",
                      "price": 199990,
                      "categoryId": 2,
                      "statusId": 1
                    }
                    """
                )
            )
        )
        @Valid @RequestBody Product product) {
        try {
            Product createdProduct = productService.createProduct(product);
            ProductResponse response = productMapper.toProductResponse(createdProduct);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, 201, "Producto creado exitosamente", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, 400, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    @Operation(
    summary = "Actualizar producto existente",
    description = "Actualiza los datos de un producto existente. Permite actualización parcial: " +
                  "solo los campos proporcionados se modificarán, los demás permanecen sin cambios. " +
                  "Se pueden actualizar: nombre, descripción, precio, stock, foto, categoría y estado. " +
                  "Si se cambia la categoría o estado, valida que los nuevos IDs existan. " +
                  "El precio y stock deben ser >= 0. Después de actualizar, retorna el producto " +
                  "completo con sus relaciones cargadas"
                )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Producto actualizado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Producto actualizado exitosamente",
                      "data": {
                        "productId": 1,
                        "stock": 20,
                        "name": "ASUS ROG Strix RTX 4090 OC",
                        "price": 1799990,
                        "category": {
                          "categoryId": 1,
                          "name": "Tarjetas de Graficas"
                        }
                      }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado - El ID proporcionado no existe",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 404,
                      "message": "Producto con ID 999 no encontrado"
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos - Valores negativos, categoría o estado inexistentes",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 400,
                      "message": "El precio no puede ser negativo"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
        @Parameter(
            description = "ID único del producto a actualizar",
            example = "1",
            required = true
        )
        @PathVariable Long id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos a actualizar del producto. Todos los campos son opcionales - " +
                          "solo se actualizarán los campos proporcionados. " +
                          "Se pueden actualizar: name, description, price, stock, productPhoto, categoryId, statusId",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Product.class),
                examples = {
                    @ExampleObject(
                        name = "Actualización completa",
                        value = """
                        {
                          "name": "ASUS ROG Strix RTX 4090 OC",
                          "description": "Versión overclockeada",
                          "price": 1799990,
                          "stock": 20,
                          "productPhoto": "https://example.com/new.jpg",
                          "categoryId": 1,
                          "statusId": 1
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Actualización parcial (solo precio y stock)",
                        value = """
                        {
                          "price": 1799990,
                          "stock": 20
                        }
                        """
                    )
                }
            )
        )
        @RequestBody Product product) {
        try {
            return productService.updateProduct(id, product)
                    .map(updatedProduct -> {
                        ProductResponse response = productMapper.toProductResponse(updatedProduct);
                        return ResponseEntity.ok(
                                new ApiResponse<>(true, 200, "Producto actualizado exitosamente", response));
                    })
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(false, 404, PRODUCT_WITH_ID + id + NOT_FOUND, null)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, 400, e.getMessage(), null));
        }
    }


    @DeleteMapping("/{id}")
    @Operation(
    summary = "Eliminar producto",
    description = "Elimina un producto del catálogo de forma permanente. " +
                  "Esta operación es irreversible y eliminará el producto de la base de datos. " +
                  "IMPORTANTE: Si el producto tiene detalles de compra asociados en el microservicio " +
                  "shopping, puede fallar por restricciones de integridad referencial. " +
                  "Considere cambiar el estado a 'Inactivo' en lugar de eliminar para mantener " +
                  "el historial de ventas"
                )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Producto eliminado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Producto eliminado exitosamente"
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado - El ID proporcionado no existe",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 404,
                      "message": "Producto con ID 999 no encontrado"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @Parameter(description = "ID del producto a eliminar", example = "1", required = true)
            @PathVariable Long id) {
        if (productService.deleteProduct(id)) {
            return ResponseEntity.ok(new ApiResponse<>(true, 200, "Producto eliminado exitosamente", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, 404, PRODUCT_WITH_ID + id + NOT_FOUND, null));
    }
}