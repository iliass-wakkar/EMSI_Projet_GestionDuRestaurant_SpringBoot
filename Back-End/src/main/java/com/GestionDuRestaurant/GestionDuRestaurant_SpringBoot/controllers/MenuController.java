package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.controllers;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.Menu.MenuRequest;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.annotations.CurrentUser;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Menu;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Users;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.services.MenuService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MenuController {

    @Autowired
    private MenuService menuService;

    // Get all menus (admin only)
    @GetMapping("/menus")
    public ResponseEntity<?> getAllMenus(@CurrentUser Users currentUser) {
        try {
            List<Menu> menus = menuService.getAllMenus(currentUser);
            return ResponseEntity.ok(menus);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MenuErrorResponse("Access denied", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MenuErrorResponse("Failed to retrieve menus", e.getMessage()));
        }
    }

    // Get menus by restaurant
    @GetMapping("/restaurants/{restaurantId}/menus")
    public ResponseEntity<?> getMenusByRestaurant(
            @PathVariable Long restaurantId,
            @CurrentUser Users currentUser) {
        try {
            List<Menu> menus = menuService.getMenusByRestaurant(restaurantId, currentUser);
            return ResponseEntity.ok(menus);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MenuErrorResponse("Restaurant not found", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MenuErrorResponse("Access denied", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MenuErrorResponse("Failed to retrieve menus", e.getMessage()));
        }
    }

    // Get menu by ID
    @GetMapping("/menus/{menuId}")
    public ResponseEntity<?> getMenuById(
            @PathVariable Long menuId,
            @CurrentUser Users currentUser) {
        try {
            Menu menu = menuService.getMenuById(menuId, currentUser);
            return ResponseEntity.ok(menu);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MenuErrorResponse("Menu not found", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MenuErrorResponse("Access denied", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MenuErrorResponse("Failed to retrieve menu", e.getMessage()));
        }
    }

    // Create menu
    @PostMapping("/menus")
    public ResponseEntity<?> createMenu(
            @Valid @RequestBody MenuRequest menuRequest,
            @CurrentUser Users currentUser) {
        try {
            Menu createdMenu = menuService.createMenu(menuRequest, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMenu);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MenuErrorResponse("Restaurant not found", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MenuErrorResponse("Access denied", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MenuErrorResponse("Invalid menu data", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MenuErrorResponse("Failed to create menu", e.getMessage()));
        }
    }

    // Update menu
    @PutMapping("/menus/{menuId}")
    public ResponseEntity<?> updateMenu(
            @PathVariable Long menuId,
            @Valid @RequestBody MenuRequest menuRequest,
            @CurrentUser Users currentUser) {
        try {
            Menu updatedMenu = menuService.updateMenu(menuId, menuRequest, currentUser);
            return ResponseEntity.ok(updatedMenu);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MenuErrorResponse("Menu not found", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MenuErrorResponse("Access denied", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MenuErrorResponse("Invalid menu data", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MenuErrorResponse("Failed to update menu", e.getMessage()));
        }
    }

    // Delete menu
    @DeleteMapping("/menus/{menuId}")
    public ResponseEntity<?> deleteMenu(
            @PathVariable Long menuId,
            @CurrentUser Users currentUser) {
        try {
            menuService.deleteMenu(menuId, currentUser);
            return ResponseEntity.ok().body("Menu deleted successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MenuErrorResponse("Menu not found", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MenuErrorResponse("Access denied", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MenuErrorResponse("Failed to delete menu", e.getMessage()));
        }
    }

    // Error response class
    public static class MenuErrorResponse {
        private String error;
        private String message;

        public MenuErrorResponse(String error, String message) {
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