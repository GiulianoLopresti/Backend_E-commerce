package com.looprex.users.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Data
@Schema
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    @Schema(description = "ID único del usuario", example = "1", type = "long", accessMode = Schema.AccessMode.READ_ONLY)
    private Long userId;
    
    @Column(name = "name", nullable = false, length = 100)
    @Schema(description = "Nombre del usuario", example = "Juan", type = "string", maxLength = 100)
    private String name;

    @Column(name = "lastname", nullable = false, length = 100)
    @Schema(description = "Apellido del usuario", example = "Pérez", type = "string", maxLength = 100)
    private String lastname;

    @Column(name = "rut", length = 12, unique = true)
    @Schema(description = "RUT del usuario", example = "12345678-9", type = "string", maxLength = 12)
    @Pattern(
        message = "El RUT debe tener un formato válido (ejemplo: 12345678-9)",
        regexp = "^\\d{7,8}-[\\dKk]$"
    )
    private String rut;

    @Column(name = "profilePhoto", length = 500)
    @Schema(description = "URL de la foto de perfil del usuario", example = "https://example.com/profile.jpg", type = "string", maxLength = 500)
    private String profilePhoto;

    @Column(name = "phone", length = 20)
    @Schema(description = "Número de teléfono del usuario", example = "123456789", type = "string", maxLength = 20)
    private String phone;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    @Schema(description = "Correo electrónico del usuario", example = "juan.perez@example.com", type = "string", maxLength = 100)
    @Email(
        message = "El correo electrónico debe tener un formato válido",
        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    )
    @NotBlank(message = "El correo electrónico no puede estar vacío")
    private String email;
    
    @Column(name = "password", nullable = false, length = 100)
    @Schema(description = "Contraseña del usuario", example = "securePassword123", type = "string", maxLength = 100)
    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;

    @ManyToOne
    @JoinColumn(name = "roleId", nullable = false)
    @Schema(description = "Rol asociado al usuario", example = "1", type = "object", requiredProperties = {"roleId", "name"})
    private Role role;

    @Column(name = "statusId", nullable = false)
    @Schema(description = "ID del estado del usuario", example = "1", type = "integer")
    private Long statusId;
}