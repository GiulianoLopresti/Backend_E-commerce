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
@Schema(description = "Datos de la comuna")
public class ComunaResponse {

    @Schema(description = "ID de la comuna", example = "1")
    private Long comunaId;

    @Schema(description = "Nombre de la comuna", example = "Santiago")
    private String name;

    @Schema(description = "Región a la que pertenece")
    private RegionResponse region;
}