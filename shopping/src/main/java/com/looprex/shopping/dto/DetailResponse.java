package com.looprex.shopping.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta para Detail con snake_case en JSON")
public class DetailResponse {

    @JsonProperty("detail_id")
    @Schema(description = "ID del detalle", example = "1")
    private Long detailId;

    @JsonProperty("buy_id")
    @Schema(description = "ID de la compra", example = "1")
    private Long buyId;

    @JsonProperty("product_id")
    @Schema(description = "ID del producto", example = "1")
    private Long productId;

    @JsonProperty("quantity")
    @Schema(description = "Cantidad", example = "2")
    private Integer quantity;

    @JsonProperty("unit_price")
    @Schema(description = "Precio unitario en CLP", example = "1899990")
    private Integer unitPrice;

    @JsonProperty("subtotal")
    @Schema(description = "Subtotal en CLP", example = "3799980")
    private Integer subtotal;
}