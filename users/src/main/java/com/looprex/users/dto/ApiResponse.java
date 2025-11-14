package com.looprex.users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta estándar de la API")
public class ApiResponse<T> {

    @Schema(description = "Indica si la operación fue exitosa", example = "true")
    private boolean success;

    @Schema(description = "Código de estado HTTP", example = "200")
    private int statusCode;

    @Schema(description = "Mensaje descriptivo", example = "Operación exitosa")
    private String message;

    @Schema(description = "Datos de respuesta")
    private T data;

    @Schema(description = "Cantidad de elementos (para listas)", example = "5")
    private Long count;

    // Constructor para respuestas exitosas con data
    public ApiResponse(boolean success, int statusCode, String message, T data) {
        this.success = success;
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
        this.count = null;
    }

    // Constructor para respuestas de error sin data
    public ApiResponse(boolean success, int statusCode, String message) {
        this.success = success;
        this.statusCode = statusCode;
        this.message = message;
        this.data = null;
        this.count = null;
    }
}