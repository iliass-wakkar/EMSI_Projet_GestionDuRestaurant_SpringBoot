package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.repositorys;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.enums.UserRole;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<Users, Long> {
    Users findByEmail(String email);

    boolean existsByEmail(String email);

    // Find users by their manager
    List<Users> findByManager(Users manager);

    // Find users by role
    List<Users> findByRole(UserRole role);

    // Find managers for a specific owner
    List<Users> findByManagerAndRole(Users manager, UserRole role);
}
