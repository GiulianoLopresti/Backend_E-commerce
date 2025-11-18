package com.looprex.shopping.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "buys")
@Schema(
    description = "Entidad que representa una compra u orden de compra en el sistema. " +
                  "Contiene información del cliente, dirección de envío, montos y estado de la compra",
    example = "{ \"buyId\": 1, \"orderNumber\": \"ORD-2025-001\", \"buyDate\": 1700000000000, " +
              "\"subtotal\": 1899990, \"iva\": 361098, \"shipping\": 5990, \"total\": 2267078, " +
              "\"paymentMethod\": \"Tarjeta de Débito\", \"statusId\": 1, \"addressId\": 1, \"userId\": 1 }"
)
public class Buy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
        description = "Identificador único de la compra",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long buyId;

    @Size(min = 1, max = 50, message = "El número de orden debe tener entre 1 y 50 caracteres")
    @Column(nullable = false, length = 50, unique = true)
    @Schema(
        description = "Número único de la orden de compra. Se genera automáticamente en formato ORD-YYYY-###",
        example = "ORD-2025-001",
        maxLength = 50
    )
    private String orderNumber;

    @Column(nullable = false)
    @Schema(
        description = "Fecha y hora de la compra en formato timestamp (milisegundos desde epoch). Se establece automáticamente al crear la compra",
        example = "1700000000000"
    )
    private Long buyDate;

    @Min(value = 0, message = "El subtotal no puede ser negativo")
    @Column(nullable = false)
    @Schema(
        description = "Subtotal de la compra sin IVA ni costos de envío, en pesos chilenos (CLP)",
        example = "1899990",
        minimum = "0"
    )
    private Integer subtotal;

    @Min(value = 0, message = "El IVA no puede ser negativo")
    @Column(nullable = false)
    @Schema(
        description = "Monto del IVA (19%) calculado sobre el subtotal, en pesos chilenos (CLP)",
        example = "361098",
        minimum = "0"
    )
    private Integer iva;

    @Min(value = 0, message = "El costo de envío no puede ser negativo")
    @Column(nullable = false)
    @Schema(
        description = "Costo del envío en pesos chilenos (CLP)",
        example = "5990",
        minimum = "0"
    )
    private Integer shipping;

    @Min(value = 0, message = "El total no puede ser negativo")
    @Column(nullable = false)
    @Schema(
        description = "Monto total de la compra (subtotal + IVA + envío) en pesos chilenos (CLP)",
        example = "2267078",
        minimum = "0"
    )
    private Integer total;

    @Size(min = 1, max = 50, message = "El método de pago debe tener entre 1 y 50 caracteres")
    @Column(nullable = false, length = 50)
    @Schema(
        description = "Método de pago utilizado para la compra",
        example = "Tarjeta de Débito",
        maxLength = 50,
        allowableValues = {"Tarjeta de Débito", "Tarjeta de Crédito", "Transferencia", "Efectivo"}
    )
    private String paymentMethod;

    @Column(nullable = false)
    @Schema(
        description = "ID del estado de la compra (1=Pendiente, 2=Completado, 3=Cancelado, 4=En envío, etc.). Referencia al microservicio de productos",
        example = "1"
    )
    private Long statusId;

    @Column(nullable = false)
    @Schema(
        description = "ID de la dirección de envío. Referencia al microservicio de geografía",
        example = "1"
    )
    private Long addressId;

    @Column(nullable = false)
    @Schema(
        description = "ID del usuario que realizó la compra. Referencia al microservicio de usuarios",
        example = "1"
    )
    private Long userId;
}