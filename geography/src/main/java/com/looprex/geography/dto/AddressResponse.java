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
@Schema(description = "Datos de la dirección")
public class AddressResponse {

    @Schema(description = "ID de la dirección", example = "1")
    private Long addressId;

    @Schema(description = "Calle", example = "Av. Providencia")
    private String street;

    @Schema(description = "Número", example = "1234")
    private String number;

    @Schema(description = "ID del usuario", example = "1")
    private Long userId;

    @Schema(description = "Comuna de la dirección")
    private ComunaResponse comuna;
}