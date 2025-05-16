package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.controllers;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.Reservation.ReservationErrorResponse;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.Reservation.ReservationRequest;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.annotations.CurrentUser;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Reservation;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Users;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.services.ReservationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    // Create Endpoint
    @PostMapping("/reservations")
    public ResponseEntity<?> createReservation(
            @Valid @RequestBody ReservationRequest reservationRequest,
            @CurrentUser Users currentUser) {
        try {
            Reservation createdReservation = reservationService.createReservation(currentUser, reservationRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReservation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ReservationErrorResponse("Invalid reservation data", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ReservationErrorResponse("Access denied", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ReservationErrorResponse("Reservation creation failed", e.getMessage()));
        }
    }

    // Read Endpoint (Get All)
    @GetMapping("/reservations")
    public ResponseEntity<?> getReservations(@CurrentUser Users currentUser) {
        try {
            List<Reservation> reservations = reservationService.getAllReservations(currentUser);
            return ResponseEntity.ok(reservations);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ReservationErrorResponse("Access denied", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ReservationErrorResponse("Failed to retrieve reservations", e.getMessage()));
        }
    }

    // Read Endpoint (Get by ID)
    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<?> getReservationById(
            @PathVariable Integer reservationId,
            @CurrentUser Users currentUser) {
        try {
            Reservation reservation = reservationService.getReservationById(reservationId, currentUser);
            return ResponseEntity.ok(reservation);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ReservationErrorResponse("Reservation not found", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ReservationErrorResponse("Access denied", e.getMessage()));
        }
    }

    // Update Endpoint
    @PutMapping("/reservations/{reservationId}")
    public ResponseEntity<?> updateReservation(
            @PathVariable Integer reservationId,
            @Valid @RequestBody ReservationRequest reservationRequest,
            @CurrentUser Users currentUser) {
        try {
            Reservation updatedReservation = reservationService.updateReservation(currentUser, reservationId,
                    reservationRequest);
            return ResponseEntity.ok(updatedReservation);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ReservationErrorResponse("Reservation not found", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ReservationErrorResponse("Access denied", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ReservationErrorResponse("Invalid update data", e.getMessage()));
        }
    }

    // Delete Endpoint
    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<?> cancelReservation(
            @PathVariable Integer reservationId,
            @CurrentUser Users currentUser) {
        try {
            reservationService.deleteReservation(reservationId, currentUser);
            return ResponseEntity.ok()
                    .body(new ReservationErrorResponse("Success", "Reservation cancelled successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ReservationErrorResponse("Reservation not found", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ReservationErrorResponse("Access denied", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ReservationErrorResponse("Cannot cancel reservation", e.getMessage()));
        }
    }
}