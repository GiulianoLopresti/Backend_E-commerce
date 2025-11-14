package com.looprex.users.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa un usuario del sistema")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    @Schema(description = "ID único del usuario", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long userId;

    @NotBlank(message = "El RUT es obligatorio")
    @Size(min = 9, max = 12, message = "El RUT debe tener entre 9 y 12 caracteres")
    @Pattern(regexp = "^\\d{7,8}-[\\dKk]$", message = "El RUT debe tener formato válido (ej: 12345678-9)")
    @Column(name = "rut", nullable = false, length = 12)
    @Schema(description = "RUT del usuario con formato chileno", example = "12345678-9")
    private String rut;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El nombre solo puede contener letras")
    @Column(name = "name", nullable = false, length = 100)
    @Schema(description = "Nombre del usuario", example = "Juan")
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El apellido solo puede contener letras")
    @Column(name = "lastname", nullable = false, length = 100)
    @Schema(description = "Apellido del usuario", example = "Pérez")
    private String lastname;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^9\\d{8}$", message = "El teléfono debe comenzar con 9 y tener 9 dígitos")
    @Column(name = "phone", nullable = false, length = 20)
    @Schema(description = "Teléfono del usuario", example = "912345678")
    private String phone;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    @Schema(description = "Email del usuario (debe ser único)", example = "juan@example.com")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Column(name = "password", nullable = false, length = 255)
    @Schema(description = "Contraseña del usuario (se almacena encriptada)", example = "Pass123!", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @Size(max = 500, message = "La URL de la foto no puede exceder 500 caracteres")
    @Column(name = "profilePhoto", length = 500)
    @Schema(description = "URL o ruta de la foto de perfil del usuario", example = "https://example.com/photo.jpg")
    private String profilePhoto;

    @NotNull(message = "El rol es obligatorio")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "roleId", nullable = false)
    @Schema(description = "Rol asignado al usuario")
    private Role role;

    @NotNull(message = "El estado es obligatorio")
    @Positive(message = "El ID del estado debe ser positivo")
    @Column(name = "statusId", nullable = false)
    @Schema(description = "ID del estado del usuario", example = "1")
    private Long statusId;
}