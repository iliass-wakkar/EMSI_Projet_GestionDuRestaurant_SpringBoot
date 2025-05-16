package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.services;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.Reservation.ReservationRequest;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.enums.ReservationStatus;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.enums.UserRole;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Client;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Reservation;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Restaurant;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Users;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.repositorys.ClientRepo;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.repositorys.ReservationRepo;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.repositorys.RestaurantRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepo reservationRepo;

    @Autowired
    private RestaurantRepo restaurantRepo;

    @Autowired
    private ClientRepo clientRepo;

    @Transactional
    public Reservation createReservation(Users currentUser, ReservationRequest request) {
        // 1. Validate current user
        if (currentUser == null) {
            throw new SecurityException("Authentication required");
        }

        // 2. Get restaurant based on user role
        Restaurant restaurant;
        UserRole userRole = currentUser.getRole();

        switch (userRole) {
            case ADMIN:
                throw new SecurityException("Admins can't create reservations");
            case MANAGER:
                // For managers, get their assigned restaurant
                restaurant = restaurantRepo.findByManager(currentUser);
                if (restaurant == null) {
                    throw new SecurityException("You are not assigned to any restaurant");
                }
                break;
            case OWNER:
                // For owners, validate the requested restaurant ID
                if (request.getRestaurantId() == null) {
                    throw new IllegalArgumentException("Restaurant ID is required for owner reservations");
                }
                restaurant = restaurantRepo.findById(request.getRestaurantId())
                        .orElseThrow(() -> new EntityNotFoundException("Restaurant not found!"));

                // Verify the restaurant belongs to the owner
                if (restaurant.getOwner() == null || !restaurant.getOwner().getId().equals(currentUser.getId())) {
                    throw new SecurityException("You can only create reservations for your restaurants");
                }
                break;
            default:
                throw new SecurityException("Insufficient permissions to create reservations");
        }

        // 3. Validate reservation date
        if (request.getReservationDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reservation date cannot be in the past");
        }

        // 4. Create anonymous client
        Client client = new Client();
        client.setGender(request.getGender());
        client.setAgeGroup(request.getAgeGroup());
        client.setCity(request.getCity());
        client = clientRepo.save(client);

        // 5. Create reservation
        Reservation reservation = new Reservation();
        reservation.setReservationDateTime(request.getReservationDateTime());
        reservation.setNumberOfGuests(request.getNumberOfGuests());
        reservation.setNote(request.getNote());
        reservation.setRestaurant(restaurant);
        reservation.setClient(client);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCreatedAt(LocalDateTime.now());

        // 6. Save and return
        return reservationRepo.save(reservation);
    }

    @Transactional(readOnly = true)
    public List<Reservation> getAllReservations(Users currentUser) {
        if (currentUser == null) {
            throw new SecurityException("Authentication required");
        }

        UserRole userRole = currentUser.getRole();
        switch (userRole) {
            case MANAGER:
                Restaurant restaurant = restaurantRepo.findByManager(currentUser);
                if (restaurant == null) {
                    throw new SecurityException("You are not assigned to any restaurant");
                }
                return reservationRepo.findByRestaurant(restaurant);
            case OWNER:
                return reservationRepo.findByRestaurantOwner(currentUser);
            default:
                throw new SecurityException("Insufficient permissions to view reservations");
        }
    }

    @Transactional(readOnly = true)
    public Reservation getReservationById(Integer reservationId, Users currentUser) {
        if (currentUser == null) {
            throw new SecurityException("Authentication required");
        }

        Reservation reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found!"));

        UserRole userRole = currentUser.getRole();
        switch (userRole) {
            case MANAGER:
                if (!reservation.getRestaurant().getManager().getId().equals(currentUser.getId())) {
                    throw new SecurityException("You can only view reservations for your restaurant");
                }
                return reservation;
            case OWNER:
                if (!reservation.getRestaurant().getOwner().getId().equals(currentUser.getId())) {
                    throw new SecurityException("You can only view reservations for your restaurants");
                }
                return reservation;
            default:
                throw new SecurityException("Insufficient permissions to view reservation");
        }
    }

    @Transactional
    public Reservation updateReservation(Users currentUser, Integer reservationId, ReservationRequest request) {
        if (currentUser == null) {
            throw new SecurityException("Authentication required");
        }

        Reservation reservation = getReservationById(reservationId, currentUser);

        // Update fields
        if (request.getReservationDateTime() != null) {
            if (request.getReservationDateTime().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Reservation date cannot be in the past");
            }
            reservation.setReservationDateTime(request.getReservationDateTime());
        }

        if (request.getNumberOfGuests() != null) {
            reservation.setNumberOfGuests(request.getNumberOfGuests());
        }

        if (request.getNote() != null) {
            reservation.setNote(request.getNote());
        }

        return reservationRepo.save(reservation);
    }

    @Transactional
    public void deleteReservation(Integer reservationId, Users currentUser) {
        if (currentUser == null) {
            throw new SecurityException("Authentication required");
        }

        Reservation reservation = getReservationById(reservationId, currentUser);
        UserRole userRole = currentUser.getRole();

        switch (userRole) {
            case ADMIN:
                throw new SecurityException("Admins are not allowed to cancel reservations");
            case MANAGER:
                // Manager can only cancel for their assigned restaurant
                if (reservation.getRestaurant().getManager() == null ||
                        !reservation.getRestaurant().getManager().getId().equals(currentUser.getId())) {
                    throw new SecurityException("You can only cancel reservations for your assigned restaurant");
                }
                break;
            case OWNER:
                // Owner can only cancel for their own restaurants
                if (reservation.getRestaurant().getOwner() == null ||
                        !reservation.getRestaurant().getOwner().getId().equals(currentUser.getId())) {
                    throw new SecurityException("You can only cancel reservations for your own restaurants");
                }
                break;
            default:
                throw new SecurityException("Insufficient permissions to cancel reservations");
        }

        // Only allow cancellation of PENDING or CONFIRMED reservations
        if (reservation.getStatus() != ReservationStatus.PENDING &&
                reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("Only pending or confirmed reservations can be cancelled");
        }

        // Change status to CANCELLED instead of deleting
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepo.save(reservation);
    }
}
