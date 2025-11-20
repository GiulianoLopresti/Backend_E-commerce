package com.looprex.users.mapper;

import com.looprex.users.dto.RoleResponse;
import com.looprex.users.dto.UserResponse;
import com.looprex.users.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // Convierte User entity → UserResponse DTO (sin password)
    public UserResponse toUserResponse(User user) {
        RoleResponse roleResponse = null;
        if (user.getRole() != null) {
            roleResponse = RoleResponse.builder()
                    .roleId(user.getRole().getRoleId())
                    .name(user.getRole().getName())
                    .build();
        }

        return UserResponse.builder()
                .userId(user.getUserId())
                .rut(user.getRut())
                .name(user.getName())
                .lastname(user.getLastname())
                .phone(user.getPhone())
                .email(user.getEmail())
                .profilePhoto(user.getProfilePhoto())
                .role(roleResponse) 
                .statusId(user.getStatusId())
                .build();
    }
}
