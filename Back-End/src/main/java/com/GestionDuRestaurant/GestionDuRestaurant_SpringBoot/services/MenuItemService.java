package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.services;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.MenuItem.MenuItemRequest;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.enums.UserRole;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Item;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Menu;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.MenuItem;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Restaurant;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Users;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.repositorys.ItemRepo;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.repositorys.MenuItemRepo;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.repositorys.MenuRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MenuItemService {

    @Autowired
    private MenuItemRepo menuItemRepo;

    @Autowired
    private MenuRepo menuRepo;

    @Autowired
    private ItemRepo itemRepo;

    @Transactional(readOnly = true)
    public List<MenuItem> getMenuItemsByMenu(Integer menuId, Users currentUser) {
        Menu menu = menuRepo.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("Menu not found with ID: " + menuId));

        // Verify access
        verifyRestaurantAccess(menu.getRestaurant(), currentUser);

        return menuItemRepo.findByMenuOrderByDisplayOrderAsc(menu);
    }

    @Transactional(readOnly = true)
    public MenuItem getMenuItemById(Integer menuItemId, Users currentUser) {
        MenuItem menuItem = menuItemRepo.findById(menuItemId)
                .orElseThrow(() -> new EntityNotFoundException("MenuItem not found with ID: " + menuItemId));

        // Verify access
        verifyRestaurantAccess(menuItem.getMenu().getRestaurant(), currentUser);

        return menuItem;
    }

    @Transactional
    public MenuItem addItemToMenu(MenuItemRequest menuItemRequest, Users currentUser) {
        Menu menu = menuRepo.findById(menuItemRequest.getMenuId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Menu not found with ID: " + menuItemRequest.getMenuId()));

        Item item = itemRepo.findById(menuItemRequest.getItemId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Item not found with ID: " + menuItemRequest.getItemId()));

        // Verify access for modification
        verifyRestaurantAccess(menu.getRestaurant(), currentUser);

        // Check if the item is already in the menu
        Optional<MenuItem> existingMenuItem = menuItemRepo.findByMenuAndItem(menu, item);
        if (existingMenuItem.isPresent()) {
            throw new IllegalArgumentException("This item is already in the menu");
        }

        MenuItem menuItem = new MenuItem();
        menuItem.setMenu(menu);
        menuItem.setItem(item);

        // Set optional fields if provided
        if (menuItemRequest.getDisplayOrder() != null) {
            menuItem.setDisplayOrder(menuItemRequest.getDisplayOrder());
        }

        if (menuItemRequest.getStartDate() != null) {
            menuItem.setStartDate(menuItemRequest.getStartDate());
        }

        if (menuItemRequest.getEndDate() != null) {
            menuItem.setEndDate(menuItemRequest.getEndDate());
        }

        if (menuItemRequest.getPriceOverride() != null) {
            menuItem.setPriceOverride(menuItemRequest.getPriceOverride());
        }

        return menuItemRepo.save(menuItem);
    }

    @Transactional
    public MenuItem updateMenuItem(Integer menuItemId, MenuItemRequest menuItemRequest, Users currentUser) {
        MenuItem menuItem = menuItemRepo.findById(menuItemId)
                .orElseThrow(() -> new EntityNotFoundException("MenuItem not found with ID: " + menuItemId));

        // Verify access for modification
        verifyRestaurantAccess(menuItem.getMenu().getRestaurant(), currentUser);

        // Update display order if provided
        if (menuItemRequest.getDisplayOrder() != null) {
            menuItem.setDisplayOrder(menuItemRequest.getDisplayOrder());
        }

        // Update date range if provided
        if (menuItemRequest.getStartDate() != null) {
            menuItem.setStartDate(menuItemRequest.getStartDate());
        }

        if (menuItemRequest.getEndDate() != null) {
            menuItem.setEndDate(menuItemRequest.getEndDate());
        }

        // Update price override if provided
        if (menuItemRequest.getPriceOverride() != null) {
            menuItem.setPriceOverride(menuItemRequest.getPriceOverride());
        }

        return menuItemRepo.save(menuItem);
    }

    @Transactional
    public void removeItemFromMenu(Integer menuItemId, Users currentUser) {
        MenuItem menuItem = menuItemRepo.findById(menuItemId)
                .orElseThrow(() -> new EntityNotFoundException("MenuItem not found with ID: " + menuItemId));

        // Verify access for modification
        verifyRestaurantAccess(menuItem.getMenu().getRestaurant(), currentUser);

        menuItemRepo.delete(menuItem);
    }

    @Transactional
    public void removeAllItemsFromMenu(Integer menuId, Users currentUser) {
        Menu menu = menuRepo.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("Menu not found with ID: " + menuId));

        // Verify access for modification
        verifyRestaurantAccess(menu.getRestaurant(), currentUser);

        menuItemRepo.deleteByMenu(menu);
    }

    // Helper method for authorization
    private void verifyRestaurantAccess(Restaurant restaurant, Users currentUser) {
        // Owner can access their restaurant's data
        if (currentUser.getRole().equals(UserRole.OWNER) && restaurant.getOwner().equals(currentUser)) {
            return;
        }

        // Manager can access their restaurant's data
        if (currentUser.getRole().equals(UserRole.MANAGER) && restaurant.getManager().equals(currentUser)) {
            return;
        }

        throw new SecurityException("You are not authorized to access data for this restaurant");
    }
}