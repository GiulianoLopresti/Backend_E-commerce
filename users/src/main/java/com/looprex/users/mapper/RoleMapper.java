package com.looprex.users.mapper;

import com.looprex.users.dto.RoleResponse;
import com.looprex.users.model.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    // Convierte Role entity → RoleResponse DTO
    public RoleResponse toRoleResponse(Role role) {
        return RoleResponse.builder()
                .roleId(role.getRoleId())
                .name(role.getName())
                .build();
    }
}