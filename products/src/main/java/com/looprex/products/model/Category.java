package com.looprex.products.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
@Schema(
    description = "Entidad que representa una categoría de productos. " +
                  "Las categorías permiten clasificar y organizar los productos del e-commerce",
    example = "{ \"categoryId\": 1, \"name\": \"Tarjetas de Graficas\" }"
)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
        description = "Identificador único de la categoría",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long categoryId;

    @NotBlank(message = "El nombre de la categoría no puede estar vacío")
    @Size(max = 100, message = "El nombre de la categoría no puede exceder los 100 caracteres")
    @Column(nullable = false, length = 100)
    @Schema(
        description = "Nombre de la categoría de productos",
        example = "Tarjetas de Graficas",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 100
    )
    private String name;
}