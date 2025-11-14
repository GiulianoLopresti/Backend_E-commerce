package com.looprex.users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos del rol")
public class RoleResponse {

    @Schema(description = "ID del rol", example = "1")
    private Long roleId;

    @Schema(description = "Nombre del rol", example = "ADMIN")
    private String name;
}