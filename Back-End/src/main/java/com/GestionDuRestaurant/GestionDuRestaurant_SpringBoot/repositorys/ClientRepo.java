package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.repositorys;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepo extends JpaRepository<Client, Long> {
}
