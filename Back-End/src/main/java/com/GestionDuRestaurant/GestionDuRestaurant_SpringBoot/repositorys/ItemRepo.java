package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.repositorys;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.enums.ItemCategory;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Item;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ItemRepo extends JpaRepository<Item, Long> {
    // Find items by restaurant
    List<Item> findByRestaurant(Restaurant restaurant);
    
    // Find available items by restaurant
    List<Item> findByRestaurantAndAvailableTrue(Restaurant restaurant);
    
    // Find items by category and restaurant
    List<Item> findByCategoryAndRestaurant(ItemCategory category, Restaurant restaurant);
    
    // Find available items by category and restaurant
    List<Item> findByCategoryAndRestaurantAndAvailableTrue(ItemCategory category, Restaurant restaurant);
    
    // Find items by name containing (case insensitive) and restaurant
    List<Item> findByNameContainingIgnoreCaseAndRestaurant(String name, Restaurant restaurant);
    
    // Find items by price range and restaurant
    List<Item> findByPriceBetweenAndRestaurant(BigDecimal minPrice, BigDecimal maxPrice, Restaurant restaurant);
} 