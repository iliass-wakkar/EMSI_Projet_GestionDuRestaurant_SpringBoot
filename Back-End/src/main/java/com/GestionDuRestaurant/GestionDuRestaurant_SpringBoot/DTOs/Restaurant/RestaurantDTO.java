package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.Restaurant;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.Users.UserResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RestaurantDTO(
                Long id,

                @NotBlank(message = "Restaurant name is required") @Size(max = 100, message = "Restaurant name must be less than 100 characters") String name,

                @Size(max = 500, message = "Description must be less than 500 characters") String description,

                @NotBlank(message = "Address is required") @Size(max = 200, message = "Address must be less than 200 characters") String address,

                @NotBlank(message = "City is required") @Size(max = 100, message = "City must be less than 100 characters") String city,

                @NotBlank(message = "Phone number is required") @Size(max = 20, message = "Phone number must be less than 20 characters") String phoneNumber,

                @Email(message = "Invalid email format") @Size(max = 100, message = "Email must be less than 100 characters") String email,

                UserResponse manager,
                UserResponse owner) {
}
