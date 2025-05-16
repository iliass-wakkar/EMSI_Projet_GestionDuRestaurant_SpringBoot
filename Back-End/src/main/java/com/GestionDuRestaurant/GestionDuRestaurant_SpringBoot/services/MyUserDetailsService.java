package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.services;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Users;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.UserPrincipale;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.repositorys.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = userRepo.findByEmail(email);
        System.out.println("user :"+user);
        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }
        return new UserPrincipale(user);
    }
}
