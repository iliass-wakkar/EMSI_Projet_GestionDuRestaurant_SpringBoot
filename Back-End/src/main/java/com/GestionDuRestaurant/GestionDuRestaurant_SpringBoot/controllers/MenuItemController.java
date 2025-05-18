package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.controllers;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.MenuItem.MenuItemRequest;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.annotations.CurrentUser;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.MenuItem;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Users;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.services.MenuItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MenuItemController {

    @Autowired
    private MenuItemService menuItemService;

    // Get menu items by menu
    @GetMapping("/menus/{menuId}/items")
    public ResponseEntity<?> getMenuItemsByMenu(
            @PathVariable Long menuId,
            @CurrentUser Users currentUser) {
        try {
            List<MenuItem> menuItems = menuItemService.getMenuItemsByMenu(menuId, currentUser);
            return ResponseEntity.ok(menuItems);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MenuItemErrorResponse("Menu not found", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MenuItemErrorResponse("Access denied", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MenuItemErrorResponse("Failed to retrieve menu items", e.getMessage()));
        }
    }

    // Get menu item by ID
    @GetMapping("/menu-items/{menuItemId}")
    public ResponseEntity<?> getMenuItemById(
            @PathVariable Long menuItemId,
            @CurrentUser Users currentUser) {
        try {
            MenuItem menuItem = menuItemService.getMenuItemById(menuItemId, currentUser);
            return ResponseEntity.ok(menuItem);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MenuItemErrorResponse("Menu item not found", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MenuItemErrorResponse("Access denied", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MenuItemErrorResponse("Failed to retrieve menu item", e.getMessage()));
        }
    }

    // Add item to menu
    @PostMapping("/menu-items")
    public ResponseEntity<?> addItemToMenu(
            @Valid @RequestBody MenuItemRequest menuItemRequest,
            @CurrentUser Users currentUser) {
        try {
            MenuItem createdMenuItem = menuItemService.addItemToMenu(menuItemRequest, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMenuItem);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MenuItemErrorResponse("Menu or item not found", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MenuItemErrorResponse("Access denied", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MenuItemErrorResponse("Invalid menu item data", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MenuItemErrorResponse("Failed to add item to menu", e.getMessage()));
        }
    }

    // Update menu item
    @PutMapping("/menu-items/{menuItemId}")
    public ResponseEntity<?> updateMenuItem(
            @PathVariable Long menuItemId,
            @Valid @RequestBody MenuItemRequest menuItemRequest,
            @CurrentUser Users currentUser) {
        try {
            MenuItem updatedMenuItem = menuItemService.updateMenuItem(menuItemId, menuItemRequest, currentUser);
            return ResponseEntity.ok(updatedMenuItem);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MenuItemErrorResponse("Menu item not found", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MenuItemErrorResponse("Access denied", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MenuItemErrorResponse("Invalid menu item data", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MenuItemErrorResponse("Failed to update menu item", e.getMessage()));
        }
    }

    // Remove item from menu
    @DeleteMapping("/menu-items/{menuItemId}")
    public ResponseEntity<?> removeItemFromMenu(
            @PathVariable Long menuItemId,
            @CurrentUser Users currentUser) {
        try {
            menuItemService.removeItemFromMenu(menuItemId, currentUser);
            return ResponseEntity.ok().body("Item removed from menu successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MenuItemErrorResponse("Menu item not found", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MenuItemErrorResponse("Access denied", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MenuItemErrorResponse("Failed to remove item from menu", e.getMessage()));
        }
    }

    // Remove all items from menu
    @DeleteMapping("/menus/{menuId}/items")
    public ResponseEntity<?> removeAllItemsFromMenu(
            @PathVariable Long menuId,
            @CurrentUser Users currentUser) {
        try {
            menuItemService.removeAllItemsFromMenu(menuId, currentUser);
            return ResponseEntity.ok().body("All items removed from menu successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MenuItemErrorResponse("Menu not found", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MenuItemErrorResponse("Access denied", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MenuItemErrorResponse("Failed to remove items from menu", e.getMessage()));
        }
    }

    // Error response class
    public static class MenuItemErrorResponse {
        private String error;
        private String message;

        public MenuItemErrorResponse(String error, String message) {
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