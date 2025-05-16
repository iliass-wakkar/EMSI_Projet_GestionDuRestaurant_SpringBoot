package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipale implements UserDetails {
    private final transient Users user; // transient to prevent serialization

    public UserPrincipale(Users user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getRole() == null) {
            throw new IllegalStateException("User role must be assigned");
        }
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(
                "ROLE_" + user.getRole().name().toUpperCase()
        ));
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // Already hashed
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // Using email as username
    }

    @Override
    public boolean isAccountNonExpired() {
        return !user.isExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !user.isCredentialsExpired();
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }

    public String getFullName() {
        return user.getFullName();
    }

    public Users getUser() {
        return user;
    }

    // For logging/debugging
    @Override
    public String toString() {
        return "UserPrincipale{" +
                "email='" + getUsername() + '\'' +
                ", roles=" + getAuthorities() +
                ", active=" + isEnabled() +
                '}';
    }
}
