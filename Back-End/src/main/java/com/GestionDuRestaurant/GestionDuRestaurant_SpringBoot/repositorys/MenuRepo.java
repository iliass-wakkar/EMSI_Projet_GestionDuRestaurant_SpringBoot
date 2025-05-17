package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.repositorys;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Menu;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepo extends JpaRepository<Menu, Integer> {
    // Find menus by restaurant
    List<Menu> findByRestaurant(Restaurant restaurant);


    
    // Find active menus by restaurant
    List<Menu> findByRestaurantAndActiveTrue(Restaurant restaurant);
    
    // Find menu by title and restaurant
    Menu findByTitleAndRestaurant(String title, Restaurant restaurant);
    
    // Find menus by title containing (case insensitive) and restaurant
    List<Menu> findByTitleContainingIgnoreCaseAndRestaurant(String title, Restaurant restaurant);

    // Find menus by list of restaurants
    List<Menu> findByRestaurantIn(List<Restaurant> restaurants);
} 