package com.xmartin.carregistry.controller.mappers;

import com.xmartin.carregistry.domain.User;
import com.xmartin.carregistry.controller.dtos.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .password(user.getPassword())
                .name(user.getName())
                .build();
    }
}
