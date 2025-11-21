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
@Schema(description = "DTO de respuesta para categorías")
public class CategoryResponse {

    @Schema(description = "ID único de la categoría", example = "1")
    private Long categoryId;

    @Schema(description = "Nombre de la categoría", example = "Tarjetas de Graficas")
    private String name;
}