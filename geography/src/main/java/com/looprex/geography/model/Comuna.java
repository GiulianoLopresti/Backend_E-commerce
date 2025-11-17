package com.looprex.geography.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comunas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa una comuna")
public class Comuna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comunaId")
    @Schema(description = "ID único de la comuna", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long comunaId;

    @Column(name = "name", nullable = false, length = 100)
    @Schema(description = "Nombre de la comuna", example = "Santiago")
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "regionId", nullable = false)
    @Schema(description = "Región a la que pertenece la comuna")
    private Region region;
}