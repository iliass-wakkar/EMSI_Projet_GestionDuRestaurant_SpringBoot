package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.enums.AgeGroup;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 100, message = "City must be less than 100 characters")
    @Column(name = "city")
    private String city;

    @NotNull(message = "Gender is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @NotNull(message = "Age group is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "age_group", nullable = false)
    private AgeGroup ageGroup;

    @Size(max = 500, message = "Note must be less than 500 characters")
    @Column(name = "note")
    private String note;

    @NotNull(message = "Creation date and time is required")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Default constructor
    public Client() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructor with required fields
    public Client(Gender gender, AgeGroup ageGroup) {
        this.gender = gender;
        this.ageGroup = ageGroup;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public AgeGroup getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(AgeGroup ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}