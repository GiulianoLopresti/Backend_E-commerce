package com.looprex.geography.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "regiones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa una región geográfica")
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "regionId")
    @Schema(description = "ID único de la región", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long regionId;

    @Column(name = "name", nullable = false, length = 100)
    @Schema(description = "Nombre de la región", example = "Región Metropolitana", required = true)
    private String name;
}