package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.repositorys;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.enums.ItemCategory;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Item;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ItemRepo extends JpaRepository<Item, Long> {
    // Find items by restaurant
    @Query("SELECT DISTINCT i FROM Item i JOIN i.menuItems mi JOIN mi.menu m WHERE m.restaurant = :restaurant")
    List<Item> findByRestaurant(@Param("restaurant") Restaurant restaurant);

    // Find available items by restaurant
    @Query("SELECT DISTINCT i FROM Item i JOIN i.menuItems mi JOIN mi.menu m WHERE m.restaurant = :restaurant AND i.available = true")
    List<Item> findByRestaurantAndAvailableTrue(@Param("restaurant") Restaurant restaurant);

    // Find items by category and restaurant
    @Query("SELECT DISTINCT i FROM Item i JOIN i.menuItems mi JOIN mi.menu m WHERE m.restaurant = :restaurant AND i.category = :category")
    List<Item> findByCategoryAndRestaurant(@Param("category") ItemCategory category,
            @Param("restaurant") Restaurant restaurant);

    // Find available items by category and restaurant
    @Query("SELECT DISTINCT i FROM Item i JOIN i.menuItems mi JOIN mi.menu m WHERE m.restaurant = :restaurant AND i.category = :category AND i.available = true")
    List<Item> findByCategoryAndRestaurantAndAvailableTrue(@Param("category") ItemCategory category,
            @Param("restaurant") Restaurant restaurant);

    // Find items by name containing (case insensitive) and restaurant
    @Query("SELECT DISTINCT i FROM Item i JOIN i.menuItems mi JOIN mi.menu m WHERE m.restaurant = :restaurant AND LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Item> findByNameContainingIgnoreCaseAndRestaurant(@Param("name") String name,
            @Param("restaurant") Restaurant restaurant);

    // Find items by price range and restaurant
    @Query("SELECT DISTINCT i FROM Item i JOIN i.menuItems mi JOIN mi.menu m WHERE m.restaurant = :restaurant AND i.price BETWEEN :minPrice AND :maxPrice")
    List<Item> findByPriceBetweenAndRestaurant(@Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice, @Param("restaurant") Restaurant restaurant);
}