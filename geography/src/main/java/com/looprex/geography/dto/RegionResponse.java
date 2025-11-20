package com.looprex.geography.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos de la región")
public class RegionResponse {

    @Schema(description = "ID de la región", example = "1")
    private Long regionId;

    @Schema(description = "Nombre de la región", example = "Región Metropolitana")
    private String name;
}