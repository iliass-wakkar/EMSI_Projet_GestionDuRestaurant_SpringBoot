package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.Restaurant;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

// DTO for restaurant creation request
public class RestaurantRequest {
    @Size(max = 100, message = "Restaurant name must be less than 100 characters")
    private String name;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @Size(max = 200, message = "Address must be less than 200 characters")
    private String address;

    @Size(max = 100, message = "City must be less than 100 characters")
    private String city;

    @Size(max = 20, message = "Phone number must be less than 20 characters")
    private String phoneNumber;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email;

    private Integer managerId;
    private Integer ownerId;


    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getManagerId() { return managerId; }
    public void setManagerId(Integer managerId) { this.managerId = managerId; }
}
