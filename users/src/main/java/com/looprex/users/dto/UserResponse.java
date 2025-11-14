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
@Schema(description = "Datos públicos del usuario (sin contraseña)")
public class UserResponse {

    @Schema(description = "ID del usuario", example = "1")
    private Long userId;

    @Schema(description = "RUT del usuario", example = "12345678-9")
    private String rut;

    @Schema(description = "Nombre del usuario", example = "Juan")
    private String name;

    @Schema(description = "Apellido del usuario", example = "Pérez")
    private String lastName;

    @Schema(description = "Teléfono del usuario", example = "912345678")
    private String phone;

    @Schema(description = "Email del usuario", example = "juan@example.com")
    private String email;

    @Schema(description = "URL de la foto de perfil")
    private String profilePhoto;

    @Schema(description = "ID del rol", example = "2")
    private Long roleId;

    @Schema(description = "Nombre del rol", example = "CLIENT")
    private String roleName;

    @Schema(description = "ID del estado", example = "1")
    private Long statusId;
}
