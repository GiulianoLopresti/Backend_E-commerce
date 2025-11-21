package com.looprex.products.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO de respuesta para productos con categoría y estado anidados")
public class ProductResponse {

    @Schema(description = "ID único del producto", example = "1")
    private Long productId;

    @Schema(description = "Cantidad en stock", example = "15")
    private Integer stock;

    @Schema(description = "URL de la foto del producto", example = "https://example.com/image.jpg", nullable = true)
    private String productPhoto;

    @Schema(description = "Nombre del producto", example = "ASUS ROG Strix RTX 4090")
    private String name;

    @Schema(description = "Descripción del producto", example = "Tarjeta gráfica de alto rendimiento")
    private String description;

    @Schema(description = "Precio en pesos chilenos", example = "1899990")
    private Integer price;

    @Schema(description = "ID del estado", example = "1")
    private Long statusId;

    @Schema(description = "ID de la categoría", example = "1")
    private Long categoryId;

    @Schema(description = "Objeto de categoría completo")
    private CategoryResponse category;

    @Schema(description = "Objeto de estado completo")
    private StatusResponse status;
}