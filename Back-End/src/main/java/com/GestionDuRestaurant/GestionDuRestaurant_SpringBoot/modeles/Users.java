package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.Users.UserResponse;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    @Column(nullable = false)
    private String fullName;

    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    @Column(name = "image_url")
    private String imageUrl;

    private LocalDateTime lastLogin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private boolean active = true;
    private boolean locked = false;
    private boolean expired = false;
    private boolean credentialsExpired = false;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Users manager;

    @JsonManagedReference
    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Users> managedUsers = new ArrayList<>();

    // Constructors
    public Users() {
    }

    public Users(Long id, String fullName, String email, String password,
            String phoneNumber, LocalDateTime creationDate,
            LocalDateTime lastLogin, UserRole role,
            boolean active, boolean locked,
            boolean expired, boolean credentialsExpired) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.creationDate = creationDate != null ? creationDate : LocalDateTime.now();
        this.lastLogin = lastLogin;
        this.role = role;
        this.active = active;
        this.locked = locked;
        this.expired = expired;
        this.credentialsExpired = credentialsExpired;
    }

    // Utility Methods
    public UserResponse toResponse() {
        return new UserResponse(
                this.id,
                this.email,
                this.fullName,
                this.role,
                this.creationDate,
                this.imageUrl);
    }

    // Access Control Methods
    public boolean isAccountNonExpired() {
        return !expired;
    }

    public boolean isAccountNonLocked() {
        return !locked;
    }

    public boolean isCredentialsNonExpired() {
        return !credentialsExpired;
    }

    public boolean isEnabled() {
        return active;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public boolean isCredentialsExpired() {
        return credentialsExpired;
    }

    public void setCredentialsExpired(boolean credentialsExpired) {
        this.credentialsExpired = credentialsExpired;
    }

    public Users getManager() {
        return manager;
    }

    public void setManager(Users manager) {
        this.manager = manager;
    }

    public List<Users> getManagedUsers() {
        return managedUsers;
    }

    public void setManagedUsers(List<Users> managedUsers) {
        this.managedUsers = managedUsers;
    }

    public boolean managesUser(Long userId) {
        return managedUsers.stream()
                .anyMatch(user -> user.getId().equals(userId));
    }

    public void removeManager(Users manager) {
        managedUsers.remove(manager);
        manager.setManager(null);
    }

    public void addManager(Users manager) {
        managedUsers.add(manager);
        manager.setManager(this);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Equals and HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Users users = (Users) o;
        return id != null && id.equals(users.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
