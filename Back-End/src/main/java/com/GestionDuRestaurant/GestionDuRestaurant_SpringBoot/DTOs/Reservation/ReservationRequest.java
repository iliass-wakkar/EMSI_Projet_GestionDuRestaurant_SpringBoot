package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.Reservation;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.enums.AgeGroup;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.enums.Gender;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class ReservationRequest {
    // Reservation fields
    @NotNull(message = "Reservation date and time is required")
    private LocalDateTime reservationDateTime;

    @NotNull(message = "Number of guests is required")
    @Min(value = 1, message = "Number of guests must be at least 1")
    private Integer numberOfGuests;

    @Size(max = 500, message = "Note must be less than 500 characters")
    private String note;

    @NotNull(message = "Restaurant ID is required")
    private Integer restaurantId;

    // Client fields
    @NotNull(message = "Gender is required")
    private Gender gender;

    private AgeGroup ageGroup;


    // Getters and Setters
    public LocalDateTime getReservationDateTime() {
        return reservationDateTime;
    }

    public void setReservationDateTime(LocalDateTime reservationDateTime) {
        this.reservationDateTime = reservationDateTime;
    }

    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(Integer numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
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

}