package com.looprex.geography.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa una dirección")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "addressId")
    @Schema(description = "ID único de la dirección", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long addressId;

    @Column(name = "street", nullable = false, length = 200)
    @Schema(description = "Nombre de la calle", example = "Av. Providencia")
    private String street;

    @Column(name = "number", nullable = false, length = 20)
    @Schema(description = "Número de la dirección", example = "1234")
    private String number;

    @Column(name = "userId", nullable = false)
    @Schema(description = "ID del usuario (referencia al microservicio de usuarios)", example = "1")
    private Long userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "comunaId", nullable = false)
    @Schema(description = "Comuna donde se ubica la dirección")
    private Comuna comuna;
}