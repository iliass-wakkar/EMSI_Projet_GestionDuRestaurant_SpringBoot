package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.Users;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.enums.UserRole;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Users;

import java.time.LocalDateTime;

public record UserResponse(
        Integer id,
        String email,
        String fullName,
        UserRole role,
        LocalDateTime creationDate
) {
    // Conversion method in your Users entity
    public static UserResponse fromEntity(Users user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.getCreationDate()
        );
    }
}