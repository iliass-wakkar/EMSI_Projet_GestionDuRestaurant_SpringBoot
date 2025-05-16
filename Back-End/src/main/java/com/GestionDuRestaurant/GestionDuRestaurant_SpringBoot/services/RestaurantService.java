package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.services;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.Restaurant.RestaurantRequest;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.UserRole;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Restaurant;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Users;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.repositorys.RestaurantRepo;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.repositorys.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.UserRole.*;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepo restaurantRepo;

    @Autowired
    private UserRepo usersRepo;

    @Transactional(readOnly = true)
    public List<Restaurant> getRestaurants(Users currentUser) {
        // Admin can see all restaurants
        if (currentUser.getRole().equals(ADMIN)) {
            return restaurantRepo.findAll();
        }

        // Owner can see only their restaurants
        if (currentUser.getRole().equals(OWNER)) {
            return restaurantRepo.findByOwner(currentUser);
        }

        // Manager and other roles are not allowed
        throw new SecurityException("You are not authorized to view restaurants");
    }

    @Transactional
    public Restaurant createRestaurant(Users creator, RestaurantRequest restaurantRequest) {
        if(creator == null) {
            throw new SecurityException("Authentication required");
        }
        // 1. Check creator's role
        UserRole creatorRole = creator.getRole();
        switch (creatorRole) {
            case MANAGER:
                throw new SecurityException("Managers are not allowed to create restaurants");
            case ADMIN:
                // Admins can create restaurants directly
                break;
            case OWNER:
                break;
            default:
                throw new SecurityException("Insufficient permissions to create a restaurant");
        }

        // 2. Validate manager if provided
        Users manager = null;
        if (restaurantRequest.getManagerId() != null) {
            manager = usersRepo.findById(restaurantRequest.getManagerId())
                    .orElseThrow(() -> new EntityNotFoundException("Provided manager not found"));

            if (!manager.getRole().equals(MANAGER)) {
                throw new IllegalArgumentException("Provided user is not a manager");
            }

            // Check if manager is already assigned to a restaurant
            List<Restaurant> existingRestaurants = restaurantRepo.findByManager(manager);
            if (!existingRestaurants.isEmpty()) {
                throw new IllegalArgumentException("Manager is already assigned to a restaurant");
            }

            // For OWNER role, additional manager validation
            if (creatorRole == OWNER) {
                boolean isValidManager = isManagerValidForOwner(creator, manager);
                if (!isValidManager) {
                    throw new SecurityException("You cannot assign this manager to a restaurant");
                }
            }
        }

        // 3. Create restaurant
        Restaurant restaurant = new Restaurant();
        restaurant.setName(restaurantRequest.getName());
        restaurant.setDescription(restaurantRequest.getDescription());
        restaurant.setAddress(restaurantRequest.getAddress());
        restaurant.setCity(restaurantRequest.getCity());
        restaurant.setPhoneNumber(restaurantRequest.getPhoneNumber());
        restaurant.setEmail(restaurantRequest.getEmail());

        // Set owner
        if (creatorRole == OWNER) {
            restaurant.setOwner(creator);
        } else if (restaurantRequest.getOwnerId() != null) {
            Users potentialOwner = usersRepo.findById(restaurantRequest.getOwnerId())
                    .orElseThrow(() -> new EntityNotFoundException("Provided owner not found"));
            if (!potentialOwner.getRole().equals(OWNER)) {
                throw new IllegalArgumentException("Provided user is not an owner");
            }
            restaurant.setOwner(potentialOwner);
        }

        // Set manager
        if (manager != null) {
            restaurant.setManager(manager);
        }

        // Save and return
        return restaurantRepo.save(restaurant);
    }

    @Transactional
    public Restaurant updateRestaurant(Users currentUser, Integer restaurantId, RestaurantRequest restaurantRequest) {
        // 1. Find the restaurant
        Restaurant existingRestaurant = restaurantRepo.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with id: " + restaurantId));

        // 2. Check current user's role and permissions
        UserRole currentUserRole = currentUser.getRole();
        switch (currentUserRole) {
            case MANAGER:
                throw new SecurityException("Managers are not allowed to update restaurants");
            case ADMIN:
                // Admins can update any restaurant
                break;
            case OWNER:
                // Owners can only update their own restaurants
                if (!existingRestaurant.getOwner().equals(currentUser)) {
                    throw new SecurityException("You can only update your own restaurants");
                }
                break;
            default:
                throw new SecurityException("Insufficient permissions to update restaurant");
        }

        // 3. Validate manager if provided
        Users newManager = null;
        if (restaurantRequest.getManagerId() != null) {
            newManager = usersRepo.findById(restaurantRequest.getManagerId())
                    .orElseThrow(() -> new EntityNotFoundException("Provided manager not found"));

            if (!newManager.getRole().equals(MANAGER)) {
                throw new IllegalArgumentException("Provided user is not a manager");
            }

            // Check if manager is already assigned to another restaurant
            List<Restaurant> existingRestaurantsByManager = restaurantRepo.findByManager(newManager);
            existingRestaurantsByManager = existingRestaurantsByManager.stream()
                    .filter(r -> !r.getId().equals(restaurantId))
                    .collect(Collectors.toList());

            if (!existingRestaurantsByManager.isEmpty()) {
                throw new IllegalArgumentException("Manager is already assigned to another restaurant: " +
                        existingRestaurantsByManager.get(0).getName());
            }

            // For OWNER role, additional manager validation
            if (currentUserRole == OWNER) {
                boolean isValidManager = isManagerValidForOwner(currentUser, newManager);
                if (!isValidManager) {
                    throw new SecurityException("You cannot assign this manager to your restaurant");
                }
            }
        }

        // 4. Update restaurant details
        if (restaurantRequest.getName() != null) {
            existingRestaurant.setName(restaurantRequest.getName());
        }
        if (restaurantRequest.getDescription() != null) {
            existingRestaurant.setDescription(restaurantRequest.getDescription());
        }
        if (restaurantRequest.getAddress() != null) {
            existingRestaurant.setAddress(restaurantRequest.getAddress());
        }
        if (restaurantRequest.getCity() != null) {
            existingRestaurant.setCity(restaurantRequest.getCity());
        }
        if (restaurantRequest.getPhoneNumber() != null) {
            existingRestaurant.setPhoneNumber(restaurantRequest.getPhoneNumber());
        }
        if (restaurantRequest.getEmail() != null) {
            existingRestaurant.setEmail(restaurantRequest.getEmail());
        }

        // Update owner if provided and user is admin
        if (currentUserRole == ADMIN && restaurantRequest.getOwnerId() != null) {
            Users potentialOwner = usersRepo.findById(restaurantRequest.getOwnerId())
                    .orElseThrow(() -> new EntityNotFoundException("Provided owner not found"));
            if (!potentialOwner.getRole().equals(OWNER)) {
                throw new IllegalArgumentException("Provided user is not an owner");
            }
            existingRestaurant.setOwner(potentialOwner);
        }

        // Update manager if provided
        if (newManager != null) {
            existingRestaurant.setManager(newManager);
        }

        // 5. Save and return updated restaurant
        return restaurantRepo.save(existingRestaurant);
    }

    // Helper method to validate manager for owner
    private boolean isManagerValidForOwner(Users owner, Users manager) {
        // Check if the owner is the manager's manager
        return manager.getManager() != null && manager.getManager().equals(owner);
    }


    @Transactional(readOnly = true)
    public List<Restaurant> getAllRestaurants(Users currentUser) {
        UserRole userRole = currentUser.getRole();
        switch (userRole) {
            case ADMIN:
                return restaurantRepo.findAll();
            case MANAGER:
                return restaurantRepo.findByManager(currentUser);
            case OWNER:
                return restaurantRepo.findByOwner(currentUser);
            default:
                throw new SecurityException("Insufficient permissions to view restaurants");
        }
    }

    @Transactional(readOnly = true)
    public Restaurant getRestaurantById(Integer restaurantId, Users currentUser) {
        if (currentUser == null) {
            throw new SecurityException("Authentication required");
        }

        Restaurant restaurant = restaurantRepo.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + restaurantId));

        UserRole userRole = currentUser.getRole();
        switch (userRole) {
            case ADMIN:
                // Admin can access any restaurant
                return restaurant;

            case OWNER:
                // Owner can only access their own restaurants
                if (restaurant.getOwner() != null && restaurant.getOwner().getId().equals(currentUser.getId())) {
                    return restaurant;
                }
                throw new SecurityException("You can only access your own restaurants");

            case MANAGER:
                // Manager can only access the restaurant they are responsible for
                if (restaurant.getManager() != null && restaurant.getManager().getId().equals(currentUser.getId())) {
                    return restaurant;
                }
                throw new SecurityException("You can only access the restaurant you are responsible for");

            default:
                throw new SecurityException("Insufficient permissions to access restaurant details");
        }
    }

    @Transactional
    public void deleteRestaurant(Integer restaurantId, Users currentUser) {
        Restaurant restaurant = restaurantRepo.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found! "));

        UserRole userRole = currentUser.getRole();
        switch (userRole) {
            case ADMIN:
                restaurantRepo.delete(restaurant);
                return;
            case OWNER:
                if (restaurant.getOwner() != null && restaurant.getOwner().getId().equals(currentUser.getId())) {
                    restaurantRepo.delete(restaurant);
                    return;
                }
                break;
        }
        throw new SecurityException("You do not have permission to delete this restaurant");
    }
}
