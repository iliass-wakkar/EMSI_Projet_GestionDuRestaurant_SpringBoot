package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.Users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 4, message = "Password must be at least 8 characters")
        String password
) {}
