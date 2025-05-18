package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.services;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.Menu.MenuRequest;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.enums.UserRole;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Menu;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Restaurant;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Users;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.repositorys.MenuRepo;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.repositorys.RestaurantRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MenuService {

    @Autowired
    private MenuRepo menuRepo;

    @Autowired
    private RestaurantRepo restaurantRepo;

    @Transactional(readOnly = true)
    public List<Menu> getAllMenus(Users currentUser) {
        switch (currentUser.getRole()) {
            case OWNER:
                // Owner can see menus from all their restaurants
                List<Restaurant> ownerRestaurants = restaurantRepo.findByOwner(currentUser);
                return menuRepo.findByRestaurantIn(ownerRestaurants);

            case MANAGER:
                // Manager can see menus only from their managed restaurant
                Restaurant managedRestaurant = restaurantRepo.findByManager(currentUser);
                if (managedRestaurant == null) {
                    throw new SecurityException("MANAGER not assigned to any restaurant!");
                }
                return menuRepo.findByRestaurant(managedRestaurant);

            default:
                throw new SecurityException("You are not authorized to view menus");
        }
    }

    @Transactional(readOnly = true)
    public List<Menu> getMenusByRestaurant(Long restaurantId, Users currentUser) {
        Restaurant restaurant = restaurantRepo.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + restaurantId));

        // Admin can see all menus
        if (currentUser.getRole().equals(UserRole.ADMIN)) {
            return menuRepo.findByRestaurant(restaurant);
        }

        // Owner can see their restaurant's menus
        if (currentUser.getRole().equals(UserRole.OWNER) && restaurant.getOwner().equals(currentUser)) {
            return menuRepo.findByRestaurant(restaurant);
        }

        // Manager can see their restaurant's menus
        if (currentUser.getRole().equals(UserRole.MANAGER) && restaurant.getManager().equals(currentUser)) {
            return menuRepo.findByRestaurant(restaurant);
        }

        throw new SecurityException("You are not authorized to view menus for this restaurant");
    }

    @Transactional(readOnly = true)
    public Menu getMenuById(Long menuId, Users currentUser) {
        Menu menu = menuRepo.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("Menu not found with ID: " + menuId));

        Restaurant restaurant = menu.getRestaurant();

        // Admin can see all menus
        if (currentUser.getRole().equals(UserRole.ADMIN)) {
            return menu;
        }

        // Owner can see their restaurant's menus
        if (currentUser.getRole().equals(UserRole.OWNER) && restaurant.getOwner().equals(currentUser)) {
            return menu;
        }

        // Manager can see their restaurant's menus
        if (currentUser.getRole().equals(UserRole.MANAGER) && restaurant.getManager().equals(currentUser)) {
            return menu;
        }

        throw new SecurityException("You are not authorized to view this menu");
    }

    @Transactional
    public Menu createMenu(MenuRequest menuRequest, Users currentUser) {
        Restaurant restaurant;

        switch (currentUser.getRole()) {
            case OWNER:
                restaurant = restaurantRepo.findById(menuRequest.getRestaurantId())
                        .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));
                if (!restaurant.getOwner().equals(currentUser)) {
                    throw new SecurityException("You can only create menus for your own restaurants");
                }
                break;

            case MANAGER:
                restaurant = restaurantRepo.findByManager(currentUser);
                if (restaurant == null) {
                    throw new SecurityException("MANAGER not assigned to any restaurant!");
                }
                break;

            default:
                throw new SecurityException("You are not authorized to create menus");
        }

        // Check if a menu with this title already exists for the restaurant
        Menu existingMenu = menuRepo.findByTitleAndRestaurant(menuRequest.getTitle(), restaurant);
        if (existingMenu != null) {
            throw new IllegalArgumentException("A menu with this title already exists for this restaurant");
        }

        Menu menu = new Menu();
        menu.setTitle(menuRequest.getTitle());
        menu.setDescription(menuRequest.getDescription());
        menu.setRestaurant(restaurant);

        if (menuRequest.getActive() != null) {
            menu.setActive(menuRequest.getActive());
        } else
            menu.setActive(false);

        return menuRepo.save(menu);
    }

    @Transactional
    public Menu updateMenu(Long menuId, MenuRequest menuRequest, Users currentUser) {
        Menu menu = menuRepo.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("Menu not found"));

        Restaurant restaurant = menu.getRestaurant();

        // Verify authorization
        Restaurant newrestaurant = null;
        switch (currentUser.getRole()) {
            case OWNER:
                newrestaurant = restaurantRepo.findById(menuRequest.getRestaurantId())
                        .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));
                if (!restaurant.getOwner().equals(currentUser) && !newrestaurant.getOwner().equals(currentUser)
                        && !restaurant.equals(newrestaurant)) {
                    throw new SecurityException("You can only update menus for your own restaurants");
                }
                break;

            case MANAGER:
                Restaurant managedRestaurant = restaurantRepo.findByManager(currentUser);
                if (managedRestaurant == null) {
                    throw new SecurityException("MANAGER not assigned to any restaurant!");
                }
                if (!managedRestaurant.getId().equals(restaurant.getId())) {
                    throw new SecurityException("You can only update menus for your assigned restaurant");
                }
                break;

            default:
                throw new SecurityException("You are not authorized to update menus");
        }

        // Validate and update fields
        if (menuRequest.getTitle() != null && !menuRequest.getTitle().trim().isEmpty()) {
            if (!menuRequest.getTitle().equals(menu.getTitle())) {
                Menu existingMenu = menuRepo.findByTitleAndRestaurant(menuRequest.getTitle(), restaurant);
                if (existingMenu != null && !existingMenu.getId().equals(menuId)) {
                    throw new IllegalArgumentException("A menu with this title already exists for this restaurant");
                }
                menu.setTitle(menuRequest.getTitle());
            }
        }
        if (newrestaurant != null) {
            menu.setRestaurant(newrestaurant);
        }

        if (menuRequest.getDescription() != null) {
            menu.setDescription(menuRequest.getDescription().trim());
        }

        if (menuRequest.getActive() != null) {
            menu.setActive(menuRequest.getActive());
        }

        return menuRepo.save(menu);
    }

    @Transactional
    public void deleteMenu(Long menuId, Users currentUser) {
        Menu menu = menuRepo.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("Menu not found with ID: " + menuId));

        Restaurant restaurant = menu.getRestaurant();
        if (currentUser.getRole().equals(UserRole.OWNER) && restaurant.getOwner().equals(currentUser)) {
            // Continue with deletion
        }
        // Manager can delete menus only for their restaurant
        else if (currentUser.getRole().equals(UserRole.MANAGER) && restaurant.getManager().equals(currentUser)) {
            // Continue with deletion
        } else {
            throw new SecurityException("You are not authorized to delete this menu");
        }

        menuRepo.delete(menu);
    }
}