package com.looprex.shopping.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
    description = "Respuesta estándar de la API. Encapsula todos los datos de respuesta con metadatos sobre el resultado de la operación",
    example = "{ \"success\": true, \"statusCode\": 200, \"message\": \"Operación exitosa\", \"data\": {...}, \"count\": 10 }"
)
public class ApiResponse<T> {

    @Schema(
        description = "Indica si la operación fue exitosa",
        example = "true"
    )
    private boolean success;

    @Schema(
        description = "Código de estado HTTP de la respuesta",
        example = "200"
    )
    private int statusCode;

    @Schema(
        description = "Mensaje descriptivo del resultado de la operación",
        example = "Compra creada exitosamente"
    )
    private String message;

    @Schema(
        description = "Datos de respuesta. Puede ser un objeto, lista o cualquier tipo de dato",
        nullable = true
    )
    private T data;

    @Schema(
        description = "Cantidad de elementos en data (solo cuando data es una lista)",
        example = "10",
        nullable = true
    )
    private Integer count;

    public ApiResponse(boolean success, int statusCode, String message, T data) {
        this.success = success;
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(boolean success, int statusCode, String message) {
        this.success = success;
        this.statusCode = statusCode;
        this.message = message;
    }
}