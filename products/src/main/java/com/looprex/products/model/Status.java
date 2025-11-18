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
@Table(name = "statuses")
@Schema(
    description = "Entidad que representa un estado en el sistema. " +
                  "Los estados se aplican tanto a productos (Activo, Inactivo) como a compras (Pendiente, Completado, Cancelado, etc.)",
    example = "{ \"statusId\": 1, \"name\": \"Activo\" }"
)
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
        description = "Identificador único del estado",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long statusId;

    @NotBlank(message = "El nombre del estado no puede estar vacío")
    @Size(max = 50, message = "El nombre del estado no puede exceder los 50 caracteres")
    @Column(nullable = false, length = 50, unique = true)
    @Schema(
        description = "Nombre del estado. Debe ser único en el sistema",
        example = "Activo",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 50
    )
    private String name;
}