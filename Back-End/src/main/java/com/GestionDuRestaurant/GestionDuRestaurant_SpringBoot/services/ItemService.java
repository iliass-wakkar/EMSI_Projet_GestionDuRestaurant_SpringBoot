package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.services;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.Item.ItemRequest;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.enums.ItemCategory;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.enums.UserRole;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Item;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.MenuItem;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Restaurant;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Users;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.repositorys.ItemRepo;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.repositorys.MenuItemRepo;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.repositorys.RestaurantRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ItemService {

    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    private RestaurantRepo restaurantRepo;

    @Autowired
    private MenuItemRepo menuItemRepo;

    @Transactional(readOnly = true)
    public List<Item> getAllItems(Users currentUser) {
        switch (currentUser.getRole()) {
            case OWNER:
                // Owner can see items from all their restaurants' menus
                List<Restaurant> ownerRestaurants = restaurantRepo.findByOwner(currentUser);
                Set<Item> ownerItems = new HashSet<>();
                ownerRestaurants.forEach(restaurant -> {
                    restaurant.getMenus().forEach(menu -> {
                        menu.getMenuItems().forEach(menuItem -> ownerItems.add(menuItem.getItem()));
                    });
                });
                return ownerItems.stream().collect(Collectors.toList());

            case MANAGER:
                // Manager can see items from their restaurant's menus
                Restaurant managedRestaurant = restaurantRepo.findByManager(currentUser);
                if (managedRestaurant == null) {
                    throw new SecurityException("Manager not assigned to any restaurant");
                }
                Set<Item> managerItems = new HashSet<>();
                managedRestaurant.getMenus().forEach(menu -> {
                    menu.getMenuItems().forEach(menuItem -> managerItems.add(menuItem.getItem()));
                });
                return managerItems.stream().collect(Collectors.toList());

            default:
                throw new SecurityException("You are not authorized to view items");
        }
    }

    @Transactional(readOnly = true)
    public List<Item> getItemsByRestaurant(Integer restaurantId, Users currentUser) {
        Restaurant restaurant = restaurantRepo.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + restaurantId));

        // Owner can see items from their restaurant's menus
        if (currentUser.getRole().equals(UserRole.OWNER) && restaurant.getOwner().equals(currentUser)) {
            Set<Item> items = new HashSet<>();
            restaurant.getMenus().forEach(menu -> {
                menu.getMenuItems().forEach(menuItem -> items.add(menuItem.getItem()));
            });
            return items.stream().collect(Collectors.toList());
        }

        // Manager can see items from their restaurant's menus
        if (currentUser.getRole().equals(UserRole.MANAGER) && restaurant.getManager().equals(currentUser)) {
            Set<Item> items = new HashSet<>();
            restaurant.getMenus().forEach(menu -> {
                menu.getMenuItems().forEach(menuItem -> items.add(menuItem.getItem()));
            });
            return items.stream().collect(Collectors.toList());
        }

        throw new SecurityException("You are not authorized to view items for this restaurant");
    }

    @Transactional(readOnly = true)
    public List<Item> getItemsByCategory(Integer restaurantId, ItemCategory category, Users currentUser) {
        Restaurant restaurant = restaurantRepo.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + restaurantId));

        // Verify access
        verifyRestaurantAccess(restaurant, currentUser);

        Set<Item> items = new HashSet<>();
        restaurant.getMenus().forEach(menu -> {
            menu.getMenuItems().forEach(menuItem -> {
                Item item = menuItem.getItem();
                if (item.getCategory().equals(category)) {
                    items.add(item);
                }
            });
        });
        return items.stream().collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Item getItemById(Long itemId, Users currentUser) {
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found with ID: " + itemId));

        // For owners and managers, verify if the item is in any of their restaurant's
        // menus
        if (currentUser.getRole().equals(UserRole.OWNER)) {
            List<Restaurant> ownerRestaurants = restaurantRepo.findByOwner(currentUser);
            if (!hasAccessToItem(ownerRestaurants, item)) {
                throw new SecurityException("You are not authorized to view this item");
            }
        } else if (currentUser.getRole().equals(UserRole.MANAGER)) {
            Restaurant managedRestaurant = restaurantRepo.findByManager(currentUser);
            if (managedRestaurant == null) {
                throw new SecurityException("Manager not assigned to any restaurant");
            }
            if (!hasAccessToItem(List.of(managedRestaurant), item)) {
                throw new SecurityException("You are not authorized to view this item");
            }
        } else {
            throw new SecurityException("You are not authorized to view items");
        }

        return item;
    }

    @Transactional
    public Item createItem(ItemRequest itemRequest, Users currentUser) {
        // Both owners and managers can create items
        if (!currentUser.getRole().equals(UserRole.OWNER) && !currentUser.getRole().equals(UserRole.MANAGER)) {
            throw new SecurityException("You are not authorized to create items");
        }

        Item item = new Item();
        item.setName(itemRequest.getName());
        item.setDescription(itemRequest.getDescription());
        item.setPrice(itemRequest.getPrice());
        item.setCategory(itemRequest.getCategory());

        if (itemRequest.getAvailable() != null) {
            item.setAvailable(itemRequest.getAvailable());
        }

        return itemRepo.save(item);
    }

    @Transactional
    public Item updateItem(Long itemId, ItemRequest itemRequest, Users currentUser) {
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found with ID: " + itemId));

        // Verify access through menus
        boolean hasAccess = false;
        if (currentUser.getRole().equals(UserRole.OWNER)) {
            List<Restaurant> ownerRestaurants = restaurantRepo.findByOwner(currentUser);
            hasAccess = hasAccessToItem(ownerRestaurants, item);
        } else if (currentUser.getRole().equals(UserRole.MANAGER)) {
            Restaurant managedRestaurant = restaurantRepo.findByManager(currentUser);
            if (managedRestaurant != null) {
                hasAccess = hasAccessToItem(List.of(managedRestaurant), item);
            }
        }

        if (!hasAccess) {
            throw new SecurityException("You are not authorized to update this item");
        }

        if (itemRequest.getName() != null) {
            item.setName(itemRequest.getName());
        }

        if (itemRequest.getDescription() != null) {
            item.setDescription(itemRequest.getDescription());
        }

        if (itemRequest.getPrice() != null) {
            item.setPrice(itemRequest.getPrice());
        }

        if (itemRequest.getCategory() != null) {
            item.setCategory(itemRequest.getCategory());
        }

        if (itemRequest.getAvailable() != null) {
            item.setAvailable(itemRequest.getAvailable());
        }

        return itemRepo.save(item);
    }

    @Transactional
    public void deleteItem(Long itemId, Users currentUser) {
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found with ID: " + itemId));

        // Verify access through menus
        boolean hasAccess = false;
        if (currentUser.getRole().equals(UserRole.OWNER)) {
            List<Restaurant> ownerRestaurants = restaurantRepo.findByOwner(currentUser);
            hasAccess = hasAccessToItem(ownerRestaurants, item);
        } else if (currentUser.getRole().equals(UserRole.MANAGER)) {
            Restaurant managedRestaurant = restaurantRepo.findByManager(currentUser);
            if (managedRestaurant != null) {
                hasAccess = hasAccessToItem(List.of(managedRestaurant), item);
            }
        }

        if (!hasAccess) {
            throw new SecurityException("You are not authorized to delete this item");
        }

        // Remove all menu items associated with this item first
        List<MenuItem> menuItems = menuItemRepo.findByItem(item);
        menuItemRepo.deleteAll(menuItems);

        // Then delete the item
        itemRepo.delete(item);
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

    // Helper method to check if an item is accessible through any of the given
    // restaurants' menus
    private boolean hasAccessToItem(List<Restaurant> restaurants, Item item) {
        return restaurants.stream()
                .anyMatch(restaurant -> restaurant.getMenus().stream()
                        .anyMatch(menu -> menu.getMenuItems().stream()
                                .anyMatch(menuItem -> menuItem.getItem().equals(item))));
    }
}