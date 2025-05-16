package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.controllers;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.Users.AuthRequest;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.Users.AuthResponse;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.Users.UserCreateRequest;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.Users.UserUpdateRequest;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.enums.UserRole;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.annotations.CurrentUser;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Users;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        try {
            if (authRequest.email() == null || authRequest.password() == null) {
                return ResponseEntity.badRequest().body(new AuthResponse("Email and password are required"));
            }

            String token = userService.verify(authRequest);
            return ResponseEntity.ok(new AuthResponse(token));

        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new AuthResponse(e.getReason()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Invalid credentials"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new AuthResponse("Login service unavailable"));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@CurrentUser Users currentUser) {
        try {
            List<?> users = userService.getAllUsers(currentUser);

            if (users.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(Map.of("message", "No users found"));
            }

            return ResponseEntity.ok(users);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Access Denied", "message", e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Not Found", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error", "message", "An unexpected error occurred"));
        }
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> register(
            @RequestBody @Valid UserCreateRequest request,
            @CurrentUser Users admin) {
        try {
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Authentication required");
            }
            Users savedUser = userService.createUser(request, admin);

            if (savedUser == null) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "User creation failed unexpectedly");
            }
            return ResponseEntity.ok(savedUser.toResponse());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Internal server error: " + e.getMessage());
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Integer userId,
            @CurrentUser Users currentUser) {

        try {
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Authentication required");
            }

            userService.deleteUser(userId, currentUser);
            return ResponseEntity.ok("User successfully deleted");

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You don't have permission to delete this user");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error deleting user: " + e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateCurrentUserProfile(
            @CurrentUser Users currentUser,
            @RequestBody @Valid UserUpdateRequest updateRequest) {
        try {
            Users updatedUser = userService.updateUserProfile(currentUser, updateRequest);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating profile: " + e.getMessage());
        }
    }

    @PutMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserProfile(
            @PathVariable Integer userId,
            @RequestBody @Valid UserUpdateRequest updateRequest,
            @CurrentUser Users currentUser) {
        try {
            if (currentUser == null || !currentUser.getRole().equals(UserRole.ADMIN)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only administrators can update user profiles");
            }

            userService.updateUserProfile(userId, currentUser, updateRequest);
            return ResponseEntity.ok().body("User updated successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + userId + " not found");
        } catch (ValidationException e) {
            return ResponseEntity.badRequest()
                    .body("Validation error: " + e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You do not have permission to update this user");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while updating the user profile");
        }
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<?> getUserProfile(
            @PathVariable Integer userId,
            Authentication authentication) {
        try {
            Users currentUser = (Users) authentication.getPrincipal();
            Users userProfile = userService.getUserProfile(userId, currentUser);
            return ResponseEntity.ok(userProfile.toResponse());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body("Access denied: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while fetching the profile");
        }
    }
}
