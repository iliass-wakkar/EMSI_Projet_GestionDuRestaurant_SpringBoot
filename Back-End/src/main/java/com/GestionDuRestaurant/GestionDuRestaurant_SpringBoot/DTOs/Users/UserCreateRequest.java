package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.Users;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank String fullName,
        @Email  @NotBlank String email,
        @Size(min = 8) String password,
        UserRole role
) {}
