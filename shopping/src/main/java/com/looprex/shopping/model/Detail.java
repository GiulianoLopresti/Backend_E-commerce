package com.looprex.shopping.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "details")
@Schema(
    description = "Entidad que representa un detalle o línea de producto dentro de una compra. " +
                  "Cada detalle contiene información del producto comprado, cantidad y precios",
    example = "{ \"detailId\": 1, \"buyId\": 1, \"productId\": 1, \"quantity\": 2, " +
              "\"unitPrice\": 1899990, \"subtotal\": 3799980 }"
)
public class Detail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
        description = "Identificador único del detalle de compra",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long detailId;

    @Column(nullable = false)
    @Schema(
        description = "ID de la compra a la que pertenece este detalle. Relación con Buy del mismo microservicio",
        example = "1"
    )
    private Long buyId;

    @Column(nullable = false)
    @Schema(
        description = "ID del producto comprado. Referencia al microservicio de productos",
        example = "1"
    )
    private Long productId;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    @Schema(
        description = "Cantidad de unidades del producto compradas. Debe ser al menos 1",
        example = "2",
        minimum = "1"
    )
    private Integer quantity;

    @Min(value = 0, message = "El precio unitario no puede ser negativo")
    @Column(nullable = false)
    @Schema(
        description = "Precio unitario del producto al momento de la compra, en pesos chilenos (CLP). " +
                      "Este valor se guarda para mantener historial de precios",
        example = "1899990",
        minimum = "0"
    )
    private Integer unitPrice;

    @Min(value = 0, message = "El subtotal no puede ser negativo")
    @Column(nullable = false)
    @Schema(
        description = "Subtotal de esta línea (quantity * unitPrice) en pesos chilenos (CLP)",
        example = "3799980",
        minimum = "0"
    )
    private Integer subtotal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "buyId", insertable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(
        description = "Objeto de compra completo con todos sus datos. Solo disponible en respuestas (READ_ONLY)",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Buy buy;
}