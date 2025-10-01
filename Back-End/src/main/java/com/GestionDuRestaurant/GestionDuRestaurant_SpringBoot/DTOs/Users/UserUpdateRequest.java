package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.Users;

import org.springframework.web.multipart.MultipartFile;

public class UserUpdateRequest {

    private String fullName;
    private String email;
    private String phoneNumber;
    // Optional fields for password update
    private String currentPassword;
    private String newPassword;
    private MultipartFile image;

    private Long owner_id;

    // Getters and setters

    public Long getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(Long owner_id) {
        this.owner_id = owner_id;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    // Add validation annotations as needed
}
