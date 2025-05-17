package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.controllers;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.Item.ItemRequest;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.annotations.CurrentUser;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.enums.ItemCategory;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Item;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Users;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.services.ItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ItemController {

    @Autowired
    private ItemService itemService;

    // Get all items (admin only)
    @GetMapping("/items")
    public ResponseEntity<?> getAllItems(@CurrentUser Users currentUser) {
        try {
            List<Item> items = itemService.getAllItems(currentUser);
            return ResponseEntity.ok(items);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ItemErrorResponse("Access denied", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ItemErrorResponse("Failed to retrieve items", e.getMessage()));
        }
    }

    // Get items by restaurant
    @GetMapping("/restaurants/{restaurantId}/items")
    public ResponseEntity<?> getItemsByRestaurant(
            @PathVariable Integer restaurantId,
            @CurrentUser Users currentUser) {
        try {
            List<Item> items = itemService.getItemsByRestaurant(restaurantId, currentUser);
            return ResponseEntity.ok(items);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ItemErrorResponse("Restaurant not found", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ItemErrorResponse("Access denied", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ItemErrorResponse("Failed to retrieve items", e.getMessage()));
        }
    }

    // Get items by restaurant and category
    @GetMapping("/restaurants/{restaurantId}/items/category/{category}")
    public ResponseEntity<?> getItemsByCategory(
            @PathVariable Integer restaurantId,
            @PathVariable ItemCategory category,
            @CurrentUser Users currentUser) {
        try {
            List<Item> items = itemService.getItemsByCategory(restaurantId, category, currentUser);
            return ResponseEntity.ok(items);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ItemErrorResponse("Restaurant not found", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ItemErrorResponse("Access denied", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ItemErrorResponse("Failed to retrieve items", e.getMessage()));
        }
    }

    // Get item by ID
    @GetMapping("/items/{itemId}")
    public ResponseEntity<?> getItemById(
            @PathVariable Long itemId,
            @CurrentUser Users currentUser) {
        try {
            Item item = itemService.getItemById(itemId, currentUser);
            return ResponseEntity.ok(item);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ItemErrorResponse("Item not found", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ItemErrorResponse("Access denied", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ItemErrorResponse("Failed to retrieve item", e.getMessage()));
        }
    }

    // Create item
    @PostMapping("/items")
    public ResponseEntity<?> createItem(
            @Valid @RequestBody ItemRequest itemRequest,
            @CurrentUser Users currentUser) {
        try {
            Item createdItem = itemService.createItem(itemRequest, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ItemErrorResponse("Restaurant not found", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ItemErrorResponse("Access denied", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ItemErrorResponse("Invalid item data", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ItemErrorResponse("Failed to create item", e.getMessage()));
        }
    }

    // Update item
    @PutMapping("/items/{itemId}")
    public ResponseEntity<?> updateItem(
            @PathVariable Long itemId,
            @Valid @RequestBody ItemRequest itemRequest,
            @CurrentUser Users currentUser) {
        try {
            Item updatedItem = itemService.updateItem(itemId, itemRequest, currentUser);
            return ResponseEntity.ok(updatedItem);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ItemErrorResponse("Item not found", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ItemErrorResponse("Access denied", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ItemErrorResponse("Invalid item data", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ItemErrorResponse("Failed to update item", e.getMessage()));
        }
    }

    // Delete item
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<?> deleteItem(
            @PathVariable Long itemId,
            @CurrentUser Users currentUser) {
        try {
            itemService.deleteItem(itemId, currentUser);
            return ResponseEntity.ok().body("Item deleted successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ItemErrorResponse("Item not found", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ItemErrorResponse("Access denied", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ItemErrorResponse("Failed to delete item", e.getMessage()));
        }
    }

    // Error response class
    public static class ItemErrorResponse {
        private String error;
        private String message;

        public ItemErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        // Getters and setters
        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}