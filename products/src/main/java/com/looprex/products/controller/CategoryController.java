package com.looprex.products.controller;

import com.looprex.products.dto.ApiResponse;
import com.looprex.products.dto.CategoryResponse;
import com.looprex.products.mapper.CategoryMapper;
import com.looprex.products.model.Category;
import com.looprex.products.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(
    name = "Categorías", 
    description = "Endpoints para la gestión completa de categorías de productos. " +
                  "Las categorías permiten organizar y clasificar los productos del e-commerce " +
                  "(ej: Tarjetas Gráficas, RAM, Procesadores, Almacenamiento). " +
                  "Incluye operaciones CRUD y validaciones para prevenir eliminación de categorías " +
                  "con productos asociados, manteniendo la integridad referencial"
)
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    private static final String NOT_FOUND = "No encontrada";
    private static final String CATEGORY_WITH_ID = "Categoría con ID ";

    public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping
    @Operation(
    summary = "Obtener todas las categorías",
    description = "Retorna una lista completa de todas las categorías de productos disponibles. " +
                  "Útil para construir menús de navegación, filtros de categorías en la tienda, " +
                  "y selectores de categoría en formularios de creación/edición de productos. " +
                  "Si no hay categorías registradas, retorna un status 204 (No Content)"
                )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lista de categorías obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Categorías obtenidas exitosamente",
                      "data": [
                        {
                          "categoryId": 1,
                          "name": "Tarjetas de Graficas"
                        },
                        {
                          "categoryId": 2,
                          "name": "Ram"
                        },
                        {
                          "categoryId": 3,
                          "name": "Procesadores"
                        }
                      ],
                      "count": 3
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "No hay categorías en el sistema",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 204,
                      "message": "No se encontraron categorías en el sistema"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();

        if (categories.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse<>(false, 204, "No se encontraron categorías en el sistema", null));
        }

        // Mapear a DTOs
        List<CategoryResponse> categoryResponses = categories.stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(true, 200, "Categorías obtenidas exitosamente", categoryResponses, categoryResponses.size()));
    }

    @GetMapping("/{id}")
    @Operation(
    summary = "Obtener categoría por ID",
    description = "Retorna una categoría específica según su identificador único. " +
                  "Útil para verificar si una categoría existe antes de asignarla a un producto, " +
                  "o para obtener detalles de una categoría específica"
                )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Categoría encontrada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Categoría encontrada exitosamente",
                      "data": {
                        "categoryId": 1,
                        "name": "Tarjetas de Graficas"
                      }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Categoría no encontrada - El ID proporcionado no existe",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 404,
                      "message": "Categoría con ID 999 no encontrada"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @Parameter(description = "ID de la categoría a buscar", example = "1", required = true)
            @PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(category -> {
                    CategoryResponse response = categoryMapper.toCategoryResponse(category);
                    return ResponseEntity.ok(new ApiResponse<>(true, 200, "Categoría encontrada exitosamente", response));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, 404, CATEGORY_WITH_ID + id + NOT_FOUND, null)));
    }

    @PostMapping
    @Operation(
    summary = "Crear nueva categoría",
    description = "Crea una nueva categoría de productos. El nombre debe ser único en el sistema " +
                  "y no puede estar vacío. Las categorías permiten organizar productos en grupos " +
                  "lógicos (ej: Tarjetas Gráficas, RAM, Procesadores). " +
                  "El nombre puede tener hasta 100 caracteres"
                )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Categoría creada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 201,
                      "message": "Categoría creada exitosamente",
                      "data": {
                        "categoryId": 4,
                        "name": "Almacenamiento"
                      }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos - Nombre vacío, duplicado o excede límite de caracteres",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Nombre duplicado",
                        value = """
                        {
                          "success": false,
                          "statusCode": 400,
                          "message": "Ya existe una categoría con ese nombre"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Nombre vacío",
                        value = """
                        {
                          "success": false,
                          "statusCode": 400,
                          "message": "El nombre de la categoría no puede estar vacío"
                        }
                        """
                    )
                }
            )
        )
    })
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Parameter(description = "Datos de la categoría a crear", required = true)
            @Valid @RequestBody Category category) {
        try {
            Category createdCategory = categoryService.createCategory(category);
            CategoryResponse response = categoryMapper.toCategoryResponse(createdCategory);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, 201, "Categoría creada exitosamente", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, 400, e.getMessage(), null));
        }
    }


    @PutMapping("/{id}")
    @Operation(
    summary = "Actualizar categoría",
    description = "Actualiza el nombre de una categoría existente. " +
                  "El nuevo nombre debe ser único (no puede coincidir con otra categoría existente). " +
                  "Si la categoría tiene productos asociados, seguirán vinculados después de la actualización"
                )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Categoría actualizada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Categoría actualizada exitosamente",
                      "data": {
                        "categoryId": 1,
                        "name": "Tarjetas Gráficas Premium"
                      }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Categoría no encontrada - El ID proporcionado no existe",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 404,
                      "message": "Categoría con ID 999 no encontrada"
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos - Nombre duplicado o vacío",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                    "success": false,
                    "statusCode": 400,
                    "message": "Ya existe otra categoría con ese nombre"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
        @Parameter(description = "ID de la categoría a actualizar", example = "1", required = true)
        @PathVariable Long id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Nuevo nombre para la categoría (máximo 100 caracteres, debe ser único)",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Category.class),
                examples = @ExampleObject(
                    value = """
                    {
                    "name": "Tarjetas Gráficas Premium"
                    }
                    """
                )
            )
        )
        @Valid @RequestBody Category category) {
        try {
            return categoryService.updateCategory(id, category)
                    .map(updatedCategory -> {
                        CategoryResponse response = categoryMapper.toCategoryResponse(updatedCategory);
                        return ResponseEntity.ok(
                                new ApiResponse<>(true, 200, "Categoría actualizada exitosamente", response));
                    })
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(false, 404, CATEGORY_WITH_ID + id + NOT_FOUND, null)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, 400, e.getMessage(), null));
        }
    }


    @DeleteMapping("/{id}")
    @Operation(
    summary = "Eliminar categoría",
    description = "Elimina una categoría del sistema de forma permanente. " +
                  "IMPORTANTE: No se puede eliminar una categoría que tenga productos asociados. " +
                  "Primero debe eliminar o reasignar todos los productos de esa categoría. " +
                  "Esta restricción mantiene la integridad referencial y evita productos huérfanos. " +
                  "Si intenta eliminar una categoría con productos, recibirá un error 400 indicando " +
                  "cuántos productos están asociados"
                )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Categoría eliminada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Categoría eliminada exitosamente"
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Categoría no encontrada - El ID proporcionado no existe",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 404,
                      "message": "Categoría no encontrada"
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Categoría tiene productos asociados - Debe eliminar o reasignar los productos primero",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 400,
                      "message": "No se puede eliminar la categoría porque tiene 5 producto(s) asociado(s). Elimina los productos primero."
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @Parameter(description = "ID de la categoría a eliminar", example = "1", required = true)
            @PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(new ApiResponse<>(true, 200, "Categoría eliminada exitosamente", null));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, 404, e.getMessage(), null));
                    
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, 400, e.getMessage(), null));
        }
    }
}