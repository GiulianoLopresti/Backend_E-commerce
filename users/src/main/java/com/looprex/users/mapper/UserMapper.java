package com.looprex.users.mapper;

import com.looprex.users.dto.UserResponse;
import com.looprex.users.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // Convierte User entity → UserResponse DTO (sin password)
    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .rut(user.getRut())
                .name(user.getName())
                .lastName(user.getLastname())
                .phone(user.getPhone())
                .email(user.getEmail())
                .profilePhoto(user.getProfilePhoto())
                .roleId(user.getRole().getRoleId())
                .roleName(user.getRole().getName())
                .statusId(user.getStatusId())
                .build();
    }
}