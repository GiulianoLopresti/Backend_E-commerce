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
@Schema(description = "DTO de respuesta para estados")
public class StatusResponse {

    @Schema(description = "ID único del estado", example = "1")
    private Long statusId;

    @Schema(description = "Nombre del estado", example = "Activo")
    private String name;
}