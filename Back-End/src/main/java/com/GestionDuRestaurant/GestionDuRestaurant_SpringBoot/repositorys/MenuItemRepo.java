package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.repositorys;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Item;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Menu;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepo extends JpaRepository<MenuItem, Long> {
    // Find by menu
    List<MenuItem> findByMenu(Menu menu);

    // Find by menu ordered by display order
    List<MenuItem> findByMenuOrderByDisplayOrderAsc(Menu menu);

    // Find by item
    List<MenuItem> findByItem(Item item);

    // Find by menu and item
    Optional<MenuItem> findByMenuAndItem(Menu menu, Item item);

    // Find by valid date range
    List<MenuItem> findByMenuAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Menu menu, LocalDate currentDate,
            LocalDate currentDate2);

    // Delete by menu and item
    void deleteByMenuAndItem(Menu menu, Item item);

    // Delete by menu
    void deleteByMenu(Menu menu);

    // Delete by item
    void deleteByItem(Item item);
}