package com.looprex.products.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@Table(name = "products")
@Schema(
    description = "Entidad que representa un producto en el e-commerce. " +
                  "Contiene toda la información necesaria para gestionar el inventario, precios y estado del producto",
    example = "{ \"productId\": 1, \"stock\": 15, \"productPhoto\": \"https://example.com/image.jpg\", " +
              "\"name\": \"ASUS ROG Strix RTX 4090\", \"description\": \"Tarjeta gráfica de alto rendimiento\", " +
              "\"price\": 1899990, \"statusId\": 1, \"categoryId\": 1 }"
)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
        description = "Identificador único del producto",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long productId;

    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    @Schema(
        description = "Cantidad disponible del producto en inventario. No puede ser negativo",
        example = "15",
        minimum = "0"
    )
    private Integer stock;

    @Size(max = 500, message = "La URL de la foto no puede exceder los 500 caracteres")
    @Column(length = 500)
    @Schema(
        description = "URL de la imagen del producto. Puede ser nulo si no tiene imagen",
        example = "https://example.com/products/rtx4090.jpg",
        nullable = true,
        maxLength = 500
    )
    private String productPhoto;

    @Size(min = 1, max = 200, message = "El nombre del producto debe tener entre 1 y 200 caracteres")
    @Column(nullable = false, length = 200)
    @Schema(
        description = "Nombre comercial del producto",
        example = "ASUS ROG Strix RTX 4090",
        maxLength = 200
    )
    private String name;

    @Size(min = 1, max = 1000, message = "La descripción debe tener entre 1 y 1000 caracteres")
    @Column(nullable = false, length = 1000)
    @Schema(
        description = "Descripción detallada del producto con sus características principales",
        example = "Tarjeta gráfica de alto rendimiento con 24GB GDDR6X, ideal para gaming 4K y renderizado profesional",
        maxLength = 1000
    )
    private String description;

    @Min(value = 0, message = "El precio no puede ser negativo")
    @Column(nullable = false)
    @Schema(
        description = "Precio del producto en pesos chilenos (CLP). Se almacena como valor entero sin decimales",
        example = "1899990",
        minimum = "0"
    )
    private Integer price;

    @Column(nullable = false)
    @Schema(
        description = "ID del estado del producto (1=Activo, etc.). Referencia al microservicio de estados",
        example = "1"
    )
    private Long statusId;

    @Column(nullable = false)
    @Schema(
        description = "ID de la categoría a la que pertenece el producto",
        example = "1"
    )
    private Long categoryId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoryId", insertable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(
        description = "Objeto de categoría completo con todos sus datos. Solo disponible en respuestas (READ_ONLY)",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "statusId", insertable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(
        description = "Objeto de estado completo con todos sus datos. Solo disponible en respuestas (READ_ONLY)",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Status status;
}