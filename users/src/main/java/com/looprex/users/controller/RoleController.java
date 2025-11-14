package com.looprex.users.controller;

import com.looprex.users.dto.ApiResponse;
import com.looprex.users.dto.RoleResponse;
import com.looprex.users.mapper.RoleMapper;
import com.looprex.users.model.Role;
import com.looprex.users.service.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "API para gestión de roles")
public class RoleController {

    private final RoleService roleService;
    private final RoleMapper roleMapper;

    public RoleController(RoleService roleService, RoleMapper roleMapper) {
        this.roleService = roleService;
        this.roleMapper = roleMapper;
    }

    @Operation(summary = "Obtener todos los roles")
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
