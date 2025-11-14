package com.looprex.users.controller;

import com.looprex.users.dto.ApiResponse;
import com.looprex.users.dto.UserResponse;
import com.looprex.users.mapper.UserMapper;
import com.looprex.users.model.User;
import com.looprex.users.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Usuarios", description = "API para gestión de usuarios")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    private static final String USER_NOT_FOUND = "Usuario no encontrado";

    // 1. Login
    @Operation(summary = "Login de usuario")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@RequestBody Map<String, String> credentials) {
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
    @Operation(summary = "Registrar usuario")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody User user, BindingResult result) {
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
    @Operation(summary = "Obtener usuario por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
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
    @Operation(summary = "Obtener todos los usuarios")
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
    @Operation(summary = "Actualizar datos personales")
    @PutMapping("/{id}/personal-data")
    public ResponseEntity<ApiResponse<UserResponse>> updatePersonalData(
            @PathVariable Long id,
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
    @Operation(summary = "Actualizar foto de perfil")
    @PutMapping("/{id}/profile-photo")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfilePhoto(
            @PathVariable Long id,
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
    @Operation(summary = "Cambiar contraseña")
    @PutMapping("/{id}/password")
    public ResponseEntity<ApiResponse<String>> updatePassword(
            @PathVariable Long id,
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
    @Operation(summary = "Cambiar email")
    @PutMapping("/{id}/email")
    public ResponseEntity<ApiResponse<UserResponse>> updateEmail(
            @PathVariable Long id,
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