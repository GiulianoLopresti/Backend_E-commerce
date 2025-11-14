package com.looprex.users.model;

import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Entidad que representa un rol de usuario")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roleId")
    @Schema(description = "ID único del rol", example = "1", type = "long", accessMode = Schema.AccessMode.READ_ONLY)
    private Long roleId;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    @Schema(description = "Nombre del rol", example = "ADMIN", type = "string")
    private String name;
}
