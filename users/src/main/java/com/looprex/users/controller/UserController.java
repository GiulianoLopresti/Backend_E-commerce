package com.looprex.users.controller;

import com.looprex.users.dto.ApiResponse;
import com.looprex.users.dto.UserResponse;
import com.looprex.users.mapper.UserMapper;
import com.looprex.users.model.User;
import com.looprex.users.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Tag(
    name = "Usuarios", 
    description = "Endpoints para la gestión completa de usuarios del sistema. " +
                  "Incluye autenticación, registro, consulta de perfiles, actualización de datos personales, " +
                  "gestión de contraseñas y cambio de email"
)
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    private static final String USER_NOT_FOUND = "Usuario no encontrado";

    // 1. Login
    @Operation(
        summary = "Autenticar usuario",
        description = "Autentica un usuario mediante email y contraseña. Valida las credenciales " +
                      "contra la base de datos usando BCrypt para verificar el hash de la contraseña. " +
                      "Si las credenciales son correctas, retorna los datos completos del usuario " +
                      "incluyendo su rol y estado, pero SIN incluir la contraseña por seguridad"
    )
    @PostMapping("/login")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Login exitoso - Credenciales válidas",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Login exitoso",
                      "data": {
                        "userId": 1,
                        "rut": "12345678-9",
                        "name": "Juan",
                        "lastname": "Pérez",
                        "phone": "912345678",
                        "email": "juan@example.com",
                        "profilePhoto": "https://example.com/photo.jpg",
                        "role": {
                          "roleId": 2,
                          "name": "CLIENT"
                        },
                        "statusId": 1
                      }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos - Email o contraseña faltantes",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 400,
                      "message": "Email y contraseña son requeridos"
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Credenciales incorrectas - Email o contraseña inválidos",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 401,
                      "message": "Credenciales inválidas"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<UserResponse>> login(@io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Credenciales de acceso del usuario (email y contraseña en texto plano)",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class),
                    examples = @ExampleObject(
                        value = """
                        {
                          "currentPassword": "OldPass123!",
                          "newPassword": "NewSecurePass456!"
                        }
                        """
                    )
                )
            )
            @RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        if (email == null || password == null) {
            ApiResponse<UserResponse> response = new ApiResponse<>(
                false,
                HttpStatus.BAD_REQUEST.value(),
                "Email y contraseña son requeridos"
            );
            return ResponseEntity.badRequest().body(response);
        }

        Optional<User> userOpt = userService.login(email, password);
        
        if (userOpt.isPresent()) {
            UserResponse userResponse = userMapper.toUserResponse(userOpt.get());
            ApiResponse<UserResponse> response = new ApiResponse<>(
                true,
                HttpStatus.OK.value(),
                "Login exitoso",
                userResponse
            );
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<UserResponse> response = new ApiResponse<>(
                false,
                HttpStatus.UNAUTHORIZED.value(),
                "Credenciales inválidas"
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // 2. Register
    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Crea una nueva cuenta de usuario en el sistema. Valida que el email y RUT no estén " +
                      "duplicados. La contraseña se encripta automáticamente usando BCrypt antes de almacenarla. " +
                      "Por defecto, los usuarios registrados obtienen el rol CLIENT (roleId: 2) y estado activo (statusId: 1). " +
                      "Todos los campos son validados según las reglas de negocio definidas en la entidad User"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Usuario creado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 201,
                      "message": "Usuario registrado exitosamente",
                      "data": {
                        "userId": 5,
                        "rut": "19876543-2",
                        "name": "María",
                        "lastname": "González",
                        "phone": "987654321",
                        "email": "maria@example.com",
                        "profilePhoto": null,
                        "role": {
                          "roleId": 2,
                          "name": "CLIENT"
                        },
                        "statusId": 1
                      }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos - Errores de validación o datos duplicados",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Email duplicado",
                        value = """
                        {
                          "success": false,
                          "statusCode": 400,
                          "message": "El correo electrónico ya está en uso"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "RUT duplicado",
                        value = """
                        {
                          "success": false,
                          "statusCode": 400,
                          "message": "El RUT ya está en uso"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Errores de validación",
                        value = """
                        {
                          "success": false,
                          "statusCode": 400,
                          "message": "name: El nombre debe tener entre 2 y 100 caracteres, email: El email debe tener un formato válido"
                        }
                        """
                    )
                }
            )
        )
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos completos del nuevo usuario a registrar. El rol por defecto es CLIENT (roleId: 2). " +
                              "La contraseña debe tener al menos 8 caracteres. El teléfono debe comenzar con 9 y tener 9 dígitos",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = User.class),
                    examples = @ExampleObject(
                        name = "Registro completo",
                        value = """
                        {
                          "rut": "19876543-2",
                          "name": "María",
                          "lastname": "González",
                          "phone": "987654321",
                          "email": "maria@example.com",
                          "password": "Secure123!",
                          "profilePhoto": "https://example.com/photo.jpg",
                          "role": {
                            "roleId": 2
                          },
                          "statusId": 1
                        }
                        """
                    )
                )
            )@Valid @RequestBody User user, BindingResult result) {
        // Si hay errores de validación
        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
            
            ApiResponse<UserResponse> response = new ApiResponse<>(
                false,
                HttpStatus.BAD_REQUEST.value(),
                errorMessage
                );
            return ResponseEntity.badRequest().body(response);
        }
            
        try {
            User newUser = userService.register(user);
            UserResponse userResponse = userMapper.toUserResponse(newUser);
                
            ApiResponse<UserResponse> response = new ApiResponse<>(
               true,
               HttpStatus.CREATED.value(),
               "Usuario registrado exitosamente",
               userResponse
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<UserResponse> response = new ApiResponse<>(
                false,
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 3. Get user by ID
    @Operation(
        summary = "Obtener usuario por ID",
        description = "Retorna los datos completos de un usuario específico según su identificador único. " +
                      "Los datos incluyen información personal, rol asignado y estado, pero NO incluye la contraseña " +
                      "por razones de seguridad. Útil para visualizar perfiles de usuario o validar información"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Usuario encontrado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Usuario encontrado",
                      "data": {
                        "userId": 1,
                        "rut": "12345678-9",
                        "name": "Juan",
                        "lastname": "Pérez",
                        "phone": "912345678",
                        "email": "juan@example.com",
                        "profilePhoto": "https://example.com/photo.jpg",
                        "role": {
                          "roleId": 2,
                          "name": "CLIENT"
                        },
                        "statusId": 1
                      }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrado - El ID proporcionado no existe en la base de datos",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 404,
                      "message": "Usuario no encontrado"
                    }
                    """
                )
            )
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById( 
        @Parameter(
            description = "ID único del usuario a buscar",
            example = "1",
            required = true
        )@PathVariable Long id) {
        Optional<User> userOpt = userService.getUserById(id);
        
        if (userOpt.isPresent()) {
            UserResponse userResponse = userMapper.toUserResponse(userOpt.get());
            ApiResponse<UserResponse> response = new ApiResponse<>(
                true,
                HttpStatus.OK.value(),
                "Usuario encontrado",
                userResponse
            );
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<UserResponse> response = new ApiResponse<>(
                false,
                HttpStatus.NOT_FOUND.value(),
                USER_NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // 4. Get all users
    @Operation(
        summary = "Obtener todos los usuarios",
        description = "Retorna una lista completa de todos los usuarios registrados en el sistema. " +
                      "Cada usuario incluye su información personal, rol y estado. Este endpoint es típicamente " +
                      "usado por administradores para gestionar usuarios. Si no hay usuarios registrados, " +
                      "retorna un status 204 (No Content)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lista de usuarios obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Usuarios obtenidos exitosamente",
                      "data": [
                        {
                          "userId": 1,
                          "rut": "12345678-9",
                          "name": "Juan",
                          "lastname": "Pérez",
                          "phone": "912345678",
                          "email": "juan@example.com",
                          "profilePhoto": "https://example.com/photo1.jpg",
                          "role": {
                            "roleId": 2,
                            "name": "CLIENT"
                          },
                          "statusId": 1
                        },
                        {
                          "userId": 2,
                          "rut": "98765432-1",
                          "name": "Admin",
                          "lastname": "Sistema",
                          "phone": "987654321",
                          "email": "admin@looprex.cl",
                          "profilePhoto": null,
                          "role": {
                            "roleId": 1,
                            "name": "ADMIN"
                          },
                          "statusId": 1
                        }
                      ],
                      "count": 2
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "No hay usuarios en el sistema - Base de datos vacía",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 204,
                      "message": "No se encontraron usuarios en el sistema",
                      "data": null,
                      "count": 0
                    }
                    """
                )
            )
        )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        
        if (users.isEmpty()) {
            ApiResponse<List<UserResponse>> response = new ApiResponse<>(
                false,
                HttpStatus.NO_CONTENT.value(),
                "No se encontraron usuarios en el sistema",
                null,
                0L
            );
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }
        
        List<UserResponse> userResponses = users.stream()
                .map(userMapper::toUserResponse)
                .toList();
        
        ApiResponse<List<UserResponse>> response = new ApiResponse<>(
            true,
            HttpStatus.OK.value(),
            "Usuarios obtenidos exitosamente",
            userResponses,
            (long) userResponses.size()
        );
        return ResponseEntity.ok(response);
    }

    // 5. Update personal data
    @Operation(
        summary = "Actualizar datos personales",
        description = "Permite actualizar información personal del usuario: RUT, nombre, apellido y teléfono. " +
                      "Solo se actualizan los campos proporcionados (actualización parcial). Si se intenta cambiar " +
                      "el RUT, se valida que no esté en uso por otro usuario. Los campos de autenticación " +
                      "(email, password) y de sistema (role, status) NO se modifican con este endpoint"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Datos personales actualizados exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Datos personales actualizados",
                      "data": {
                        "userId": 1,
                        "rut": "12345678-9",
                        "name": "Juan Carlos",
                        "lastname": "Pérez González",
                        "phone": "912345679",
                        "email": "juan@example.com",
                        "profilePhoto": "https://example.com/photo.jpg",
                        "role": {
                          "roleId": 2,
                          "name": "CLIENT"
                        },
                        "statusId": 1
                      }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos inválidos - RUT duplicado u otro error de validación",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 400,
                      "message": "El RUT ya está en uso"
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrado - El ID proporcionado no existe",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 404,
                      "message": "Usuario no encontrado"
                    }
                    """
                )
            )
        )
    })
    @PutMapping("/{id}/personal-data")
    public ResponseEntity<ApiResponse<UserResponse>> updatePersonalData(
        @Parameter(
                description = "ID único del usuario a actualizar",
                example = "1",
                required = true
            )
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos personales a actualizar. Todos los campos son opcionales - " +
                              "solo se actualizarán los campos proporcionados",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class),
                    examples = {
                        @ExampleObject(
                            name = "Actualización completa",
                            value = """
                            {
                              "rut": "12345678-9",
                              "name": "Juan Carlos",
                              "lastName": "Pérez González",
                              "phone": "912345679"
                            }
                            """
                        ),
                        @ExampleObject(
                            name = "Actualización parcial (solo nombre)",
                            value = """
                            {
                              "name": "Juan Carlos"
                            }
                            """
                        )
                    }
                )
            )
            @RequestBody Map<String, String> data) {
        try {
            Optional<User> updatedOpt = userService.updatePersonalData(
                id,
                data.get("rut"),
                data.get("name"),
                data.get("lastName"),
                data.get("phone")
            );
            
            if (updatedOpt.isPresent()) {
                UserResponse userResponse = userMapper.toUserResponse(updatedOpt.get());
                ApiResponse<UserResponse> response = new ApiResponse<>(
                    true,
                    HttpStatus.OK.value(),
                    "Datos personales actualizados",
                    userResponse
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<UserResponse> response = new ApiResponse<>(
                    false,
                    HttpStatus.NOT_FOUND.value(),
                    USER_NOT_FOUND
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (IllegalArgumentException e) {
            ApiResponse<UserResponse> response = new ApiResponse<>(
                false,
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 6. Update profile photo
    @Operation(
        summary = "Actualizar foto de perfil",
        description = "Permite actualizar la URL de la foto de perfil del usuario. Se espera una URL válida " +
                      "que apunte a la imagen almacenada en un servidor externo o CDN. Este endpoint solo " +
                      "actualiza la referencia a la imagen, no maneja la subida del archivo físico"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Foto de perfil actualizada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Foto de perfil actualizada",
                      "data": {
                        "userId": 1,
                        "rut": "12345678-9",
                        "name": "Juan",
                        "lastname": "Pérez",
                        "phone": "912345678",
                        "email": "juan@example.com",
                        "profilePhoto": "https://cdn.example.com/photos/new-profile.jpg",
                        "role": {
                          "roleId": 2,
                          "name": "CLIENT"
                        },
                        "statusId": 1
                      }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrado - El ID proporcionado no existe",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 404,
                      "message": "Usuario no encontrado"
                    }
                    """
                )
            )
        )
    })
    @PutMapping("/{id}/profile-photo")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfilePhoto(
        @Parameter(
                description = "ID único del usuario a actualizar",
                example = "1",
                required = true
            )
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "URL de la nueva foto de perfil. Puede ser null para eliminar la foto actual",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class),
                    examples = @ExampleObject(
                        value = """
                        {
                          "photoUri": "https://cdn.example.com/photos/new-profile.jpg"
                        }
                        """
                    )
                )
            )
            @RequestBody Map<String, String> data) {
        Optional<User> updatedOpt = userService.updateProfilePhoto(id, data.get("photoUri"));
        
        if (updatedOpt.isPresent()) {
            UserResponse userResponse = userMapper.toUserResponse(updatedOpt.get());
            ApiResponse<UserResponse> response = new ApiResponse<>(
                true,
                HttpStatus.OK.value(),
                "Foto de perfil actualizada",
                userResponse
            );
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<UserResponse> response = new ApiResponse<>(
                false,
                HttpStatus.NOT_FOUND.value(),
                USER_NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // 7. Update password
    @Operation(
        summary = "Cambiar contraseña",
        description = "Permite cambiar la contraseña del usuario. Requiere la contraseña actual para validación " +
                      "de seguridad. La nueva contraseña debe cumplir con los requisitos mínimos (8 caracteres). " +
                      "Ambas contraseñas se envían en texto plano, pero son encriptadas con BCrypt antes de " +
                      "almacenarlas. Si la contraseña actual no coincide, se rechaza la operación"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Contraseña actualizada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Contraseña actualizada exitosamente",
                      "data": null
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos inválidos - Contraseña actual incorrecta, nueva contraseña vacía o no cumple requisitos",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Contraseña actual incorrecta",
                        value = """
                        {
                          "success": false,
                          "statusCode": 400,
                          "message": "La contraseña actual es incorrecta"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Nueva contraseña vacía",
                        value = """
                        {
                          "success": false,
                          "statusCode": 400,
                          "message": "La nueva contraseña no puede estar vacía"
                        }
                        """
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrado - El ID proporcionado no existe",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 404,
                      "message": "Usuario no encontrado"
                    }
                    """
                )
            )
        )
    })
    @PutMapping("/{id}/password")
    public ResponseEntity<ApiResponse<String>> updatePassword(
        @Parameter(
                description = "ID único del usuario a actualizar",
                example = "1",
                required = true
            )
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Contraseña actual (para validación) y nueva contraseña. " +
                              "Ambas se envían en texto plano pero son encriptadas antes de almacenar",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class),
                    examples = @ExampleObject(
                        value = """
                        {
                        "currentPassword": "OldPass123!",
                        "newPassword": "NewSecurePass456!"
                        }
                        """
                    )
                )
            )
            @RequestBody Map<String, String> data) {
        try {
            Optional<User> updatedOpt = userService.updatePassword(
                id,
                data.get("currentPassword"),
                data.get("newPassword")
            );
            
            if (updatedOpt.isPresent()) {
                ApiResponse<String> response = new ApiResponse<>(
                    true,
                    HttpStatus.OK.value(),
                    "Contraseña actualizada exitosamente",
                    null
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<String> response = new ApiResponse<>(
                    false,
                    HttpStatus.NOT_FOUND.value(),
                    USER_NOT_FOUND
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (IllegalArgumentException e) {
            ApiResponse<String> response = new ApiResponse<>(
                false,
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 8. Update email
    @Operation(
        summary = "Cambiar email",
        description = "Permite cambiar el email del usuario. Requiere la contraseña actual como medida de seguridad " +
                      "adicional. Valida que el nuevo email no esté en uso por otro usuario, ya que el email " +
                      "es único en el sistema. El nuevo email debe tener un formato válido según las validaciones " +
                      "de Jakarta Validation (@Email)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Email actualizado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Email actualizado exitosamente",
                      "data": {
                        "userId": 1,
                        "rut": "12345678-9",
                        "name": "Juan",
                        "lastname": "Pérez",
                        "phone": "912345678",
                        "email": "nuevo.email@example.com",
                        "profilePhoto": "https://example.com/photo.jpg",
                        "role": {
                          "roleId": 2,
                          "name": "CLIENT"
                        },
                        "statusId": 1
                      }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos inválidos - Contraseña incorrecta, email duplicado o formato inválido",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Contraseña incorrecta",
                        value = """
                        {
                          "success": false,
                          "statusCode": 400,
                          "message": "La contraseña es incorrecta"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Email duplicado",
                        value = """
                        {
                          "success": false,
                          "statusCode": 400,
                          "message": "El email ya está en uso"
                        }
                        """
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrado - El ID proporcionado no existe",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 404,
                      "message": "Usuario no encontrado"
                    }
                    """
                )
            )
        )
    })
    @PutMapping("/{id}/email")
    public ResponseEntity<ApiResponse<UserResponse>> updateEmail(
            @Parameter(
                description = "ID único del usuario a actualizar",
                example = "1",
                required = true
            )
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Nuevo email (debe tener formato válido y no estar en uso) y contraseña actual " +
                              "para confirmar la operación por seguridad",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class),
                    examples = @ExampleObject(
                        value = """
                        {
                          "newEmail": "nuevo.email@example.com",
                          "confirmPassword": "Test123!"
                        }
                        """
                    )
                )
            )
            @RequestBody Map<String, String> data) {
        try {
            Optional<User> updatedOpt = userService.updateEmail(
                id,
                data.get("newEmail"),
                data.get("confirmPassword")
            );

            if (updatedOpt.isPresent()) {
                UserResponse userResponse = userMapper.toUserResponse(updatedOpt.get());
                ApiResponse<UserResponse> response = new ApiResponse<>(
                    true,
                    HttpStatus.OK.value(),
                    "Email actualizado exitosamente",
                    userResponse
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<UserResponse> response = new ApiResponse<>(
                    false,
                    HttpStatus.NOT_FOUND.value(),
                    USER_NOT_FOUND
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (IllegalArgumentException e) {
            ApiResponse<UserResponse> response = new ApiResponse<>(
                false,
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
            );
            return ResponseEntity.badRequest().body(response);
        }
    }
}