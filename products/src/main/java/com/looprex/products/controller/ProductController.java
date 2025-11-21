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
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Productos", description = "Endpoints para gestionar el catálogo de productos del e-commerce")
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
        description = "Retorna una lista completa de todos los productos disponibles en el catálogo, " +
                      "incluyendo sus categorías y estados"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lista de productos obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "No hay productos en el sistema",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
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
        description = "Retorna un producto específico según su identificador único, incluyendo toda su información detallada"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Producto encontrado exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
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
        description = "Retorna todos los productos que pertenecen a una categoría específica"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lista de productos de la categoría obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "No hay productos en esta categoría",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
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
        description = "Busca productos cuyo nombre contenga el texto especificado (búsqueda insensible a mayúsculas)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Búsqueda completada exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "No se encontraron productos que coincidan con la búsqueda",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
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
        description = "Retorna todos los productos que tienen un estado específico (Activo, Inactivo, etc.)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lista de productos por estado obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "No hay productos con este estado",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
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
        description = "Crea un nuevo producto en el catálogo. Debe tener una categoría y estado válidos"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Producto creado exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Parameter(description = "Datos del producto a crear", required = true)
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
        summary = "Actualizar producto",
        description = "Actualiza los datos de un producto existente"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Producto actualizado exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
             @Parameter(description = "ID del producto a actualizar", example = "1", required = true)
             @PathVariable Long id,
             @Parameter(description = "Datos actualizados del producto", required = true)
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
        description = "Elimina un producto del catálogo"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Producto eliminado exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
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