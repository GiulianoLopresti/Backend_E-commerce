package com.looprex.users.controller;

import com.looprex.users.dto.ApiResponse;
import com.looprex.users.dto.RoleResponse;
import com.looprex.users.mapper.RoleMapper;
import com.looprex.users.model.Role;
import com.looprex.users.service.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@Tag(
    name = "Roles", 
    description = "Endpoints para la gestión y consulta de roles de usuario. " +
                  "Los roles definen los permisos y nivel de acceso de los usuarios en el sistema. " +
                  "Típicamente incluyen roles como ADMIN (administrador con acceso completo) y " +
                  "CLIENT (cliente con acceso limitado a funcionalidades de compra). " +
                  "Este módulo es principalmente de consulta, ya que los roles suelen ser " +
                  "predefinidos en la base de datos"
)
public class RoleController {

    private final RoleService roleService;
    private final RoleMapper roleMapper;

    public RoleController(RoleService roleService, RoleMapper roleMapper) {
        this.roleService = roleService;
        this.roleMapper = roleMapper;
    }

    @Operation(
        summary = "Obtener todos los roles disponibles",
        description = "Retorna una lista completa de todos los roles definidos en el sistema. " +
                      "Cada rol representa un conjunto de permisos y determina qué acciones puede realizar " +
                      "un usuario. Los roles más comunes son: " +
                      "ADMIN (roleId: 1) - Acceso completo a todas las funcionalidades administrativas, " +
                      "CLIENT (roleId: 2) - Acceso a funcionalidades de compra y gestión de perfil. " +
                      "Este endpoint es útil para poblar selectores de rol en formularios de registro " +
                      "o edición de usuarios desde el panel administrativo. " +
                      "Si no hay roles definidos en el sistema, retorna un status 204 (No Content)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lista de roles obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Roles del sistema",
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Roles obtenidos exitosamente",
                      "data": [
                        {
                          "roleId": 1,
                          "name": "ADMIN"
                        },
                        {
                          "roleId": 2,
                          "name": "CLIENT"
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
            description = "No hay roles definidos en el sistema - Base de datos vacía o no inicializada",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Sistema sin roles",
                    value = """
                    {
                      "success": false,
                      "statusCode": 204,
                      "message": "No se encontraron roles en el sistema",
                      "data": null,
                      "count": 0
                    }
                    """
                )
            )
        )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        
        if (roles.isEmpty()) {
            ApiResponse<List<RoleResponse>> response = new ApiResponse<>(
                false,
                HttpStatus.NO_CONTENT.value(),
                "No se encontraron roles en el sistema",
                null,
                0L
            );
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }
        
        List<RoleResponse> roleResponses = roles.stream()
                .map(roleMapper::toRoleResponse)
                .toList();
        
        ApiResponse<List<RoleResponse>> response = new ApiResponse<>(
            true,
            HttpStatus.OK.value(),
            "Roles obtenidos exitosamente",
            roleResponses,
            (long) roleResponses.size()
        );
        return ResponseEntity.ok(response);
    }
}
