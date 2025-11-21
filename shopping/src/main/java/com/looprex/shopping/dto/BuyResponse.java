package com.looprex.shopping.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta para Buy con snake_case en JSON")
public class BuyResponse {

    @JsonProperty("buy_id")
    @Schema(description = "ID de la compra", example = "1")
    private Long buyId;

    @JsonProperty("order_number")
    @Schema(description = "Número de orden", example = "ORD-2025-001")
    private String orderNumber;

    @JsonProperty("buy_date")
    @Schema(description = "Fecha de compra (timestamp)", example = "1700000000000")
    private Long buyDate;

    @JsonProperty("subtotal")
    @Schema(description = "Subtotal en CLP", example = "1899990")
    private Integer subtotal;

    @JsonProperty("iva")
    @Schema(description = "IVA en CLP", example = "361098")
    private Integer iva;

    @JsonProperty("shipping")
    @Schema(description = "Costo de envío en CLP", example = "5990")
    private Integer shipping;

    @JsonProperty("total")
    @Schema(description = "Total en CLP", example = "2267078")
    private Integer total;

    @JsonProperty("payment_method")
    @Schema(description = "Método de pago", example = "Tarjeta de Débito")
    private String paymentMethod;

    @JsonProperty("status_id")
    @Schema(description = "ID del estado", example = "1")
    private Long statusId;

    @JsonProperty("address_id")
    @Schema(description = "ID de la dirección", example = "1")
    private Long addressId;

    @JsonProperty("user_id")
    @Schema(description = "ID del usuario", example = "1")
    private Long userId;
}