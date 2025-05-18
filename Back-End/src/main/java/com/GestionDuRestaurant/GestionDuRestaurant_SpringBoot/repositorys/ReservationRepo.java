package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.repositorys;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Reservation;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Restaurant;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepo extends JpaRepository<Reservation, Long> {
    List<Reservation> findByRestaurant(Restaurant restaurant);

    List<Reservation> findByRestaurantOwner(Users owner);
}
