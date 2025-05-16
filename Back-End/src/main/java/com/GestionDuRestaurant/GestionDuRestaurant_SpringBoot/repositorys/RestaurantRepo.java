package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.repositorys;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Restaurant;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepo extends JpaRepository<Restaurant, Integer> {
    // Find restaurants by city
    List<Restaurant> findByCity(String city);

    // Find restaurant by email
    Optional<Restaurant> findByEmail(String email);

    // Find restaurants by name (partial match)
    List<Restaurant> findByNameContainingIgnoreCase(String name);

    // Find restaurants by manager
    Restaurant findByManager(Users manager);

    // Find restaurants by owner
    List<Restaurant> findByOwner(Users owner);

    // Find restaurants by manager and city
    List<Restaurant> findByManagerAndCity(Users manager, String city);

    // Find restaurants by owner and city
    List<Restaurant> findByOwnerAndCity(Users owner, String city);
}
