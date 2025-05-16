package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Reservation date and time is required")
    @Column(name = "reservation_date_time", nullable = false)
    private LocalDateTime reservationDateTime;

    @NotNull(message = "Number of guests is required")
    @Min(value = 1, message = "Number of guests must be at least 1")
    @Column(name = "number_of_guests", nullable = false)
    private Integer numberOfGuests;

    @NotNull(message = "Status is required")
    @Size(max = 20, message = "Status must be less than 20 characters")
    @Column(name = "status", nullable = false)
    private String status;

    @Size(max = 500, message = "Note must be less than 500 characters")
    @Column(name = "note")
    private String note;

    @NotNull(message = "Creation date and time is required")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    // Default constructor
    public Reservation() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructor with required fields
    public Reservation(LocalDateTime reservationDateTime, Integer numberOfGuests, String status,
            Restaurant restaurant) {
        this.reservationDateTime = reservationDateTime;
        this.numberOfGuests = numberOfGuests;
        this.status = status;
        this.restaurant = restaurant;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}