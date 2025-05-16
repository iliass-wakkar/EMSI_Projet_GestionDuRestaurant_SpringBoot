package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.controllers;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.Restaurant.RestaurantRequest;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.annotations.CurrentUser;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Restaurant;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Users;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.services.RestaurantService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    // Create Endpoint
    @PostMapping("/restaurants")
    public ResponseEntity<?> addRestaurant(
            @Valid @RequestBody RestaurantRequest restaurantRequest,
            @CurrentUser Users currentUser) {
        try {
            Restaurant createdRestaurant = restaurantService.createRestaurant( currentUser, restaurantRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRestaurant);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RestorantErrorResponse("Invalid restaurant data", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RestorantErrorResponse("Restaurant creation failed", e.getMessage()));
        }
    }

    // Read Endpoint (Get All)
    @GetMapping("/restaurants")
    public ResponseEntity<?> getRestaurants(@CurrentUser Users currentUser) {
        try {
            List<Restaurant> restaurants = restaurantService.getAllRestaurants(currentUser);
            return ResponseEntity.ok(restaurants);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new RestorantErrorResponse("Access denied", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RestorantErrorResponse("Failed to retrieve restaurants", e.getMessage()));
        }
    }

    // Read Endpoint (Get by ID)
    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<?> getRestaurantById(
            @PathVariable Integer restaurantId,
            @CurrentUser Users currentUser) {
        try {
            Restaurant restaurant = restaurantService.getRestaurantById(restaurantId, currentUser);
            return ResponseEntity.ok(restaurant);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new RestorantErrorResponse("Restaurant not found", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new RestorantErrorResponse("Access denied", e.getMessage()));
        }
    }

    // Update Endpoint
    @PutMapping("/restaurants/{restaurantId}")
    public ResponseEntity<?> updateRestaurant(
            @PathVariable Integer restaurantId,
            @Valid @RequestBody RestaurantRequest restaurantRequest,
            @CurrentUser Users currentUser) {
        try {
            Restaurant updatedRestaurant = restaurantService.updateRestaurant(currentUser ,restaurantId, restaurantRequest);
            return ResponseEntity.ok(updatedRestaurant);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new RestorantErrorResponse("Restaurant not found", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new RestorantErrorResponse("Access denied", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new RestorantErrorResponse("Invalid update data", e.getMessage()));
        }
    }

    // Delete Endpoint
    @DeleteMapping("/restaurants/{restaurantId}")
    public ResponseEntity<?> deleteRestaurant(
            @PathVariable Integer restaurantId,
            @CurrentUser Users currentUser) {
        try {
            restaurantService.deleteRestaurant(restaurantId, currentUser);
            return ResponseEntity.status(HttpStatus.OK).body("Restaurant deleted successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new RestorantErrorResponse("Restaurant not found", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new RestorantErrorResponse("Access denied", e.getMessage()));
        }
    }

    // Error Response DTO (Inner class or separate class)
    public static class RestorantErrorResponse {
        private String error;
        private String message;

        public RestorantErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        // Getters and setters
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
