package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.services;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.Users.AuthRequest;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.Users.UserCreateRequest;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.Users.UserUpdateRequest;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.enums.UserRole;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.Users;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.repositorys.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileStorageService fileStorageService;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    public Users createUser(UserCreateRequest request, Users user) throws Exception {
        // 1. Quick privilege check
        if (request.role() == UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Cannot create admin accounts");
        }
        if (userRepo.existsByEmail((request.email()))) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already registered");
        } else if (user.getRole() == UserRole.OWNER || UserRole.ADMIN == user.getRole()) {
            // 2. Create new user
            Users newUser = new Users();

            // Set manager relationship if applicable
            if (request.role() == UserRole.MANAGER && user.getRole() == UserRole.OWNER) {
                newUser.setManager(user);
            }

            newUser.setFullName(request.fullName());
            newUser.setEmail(request.email());
            newUser.setPassword(bCryptPasswordEncoder.encode(request.password()));
            newUser.setRole(request.role());
            newUser.setCreationDate(LocalDateTime.now());

            // 3. Save
            return userRepo.save(newUser);
        } else
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have the right to create a user");
    }

    public String verify(AuthRequest authRequest) throws Exception {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.email(),
                            authRequest.password()));
            String token = jwtService.generateToken((UserDetails) authentication.getPrincipal());
            return token;
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        } catch (DisabledException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account disabled");
        }
    }

    public void deleteUser(Long userId, Users currentUser) {
        // Find the user to be deleted
        Users userToDelete = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Authorization checks based on user roles
        switch (currentUser.getRole()) {
            case OWNER:
                // Owner can delete:
                // 1. Their own account
                // 2. Any of their managers
                if (currentUser.getId().equals(userId)) {
                    // If owner is deleting themselves, cascade delete their managers
                    List<Users> managedUsers = currentUser.getManagedUsers();
                    userRepo.deleteAll(managedUsers);
                    currentUser.setActive(false);
                    userRepo.save(currentUser);
                } else if (currentUser.managesUser(userId)) {
                    // Owner deleting their manager
                    currentUser.removeManager(userToDelete);
                    userRepo.delete(userToDelete);
                } else {
                    throw new AccessDeniedException("Not authorized to delete this user");
                }
                break;

            case MANAGER:
                if (!currentUser.getId().equals(userId)) {
                    throw new AccessDeniedException("Managers can only delete their own account");
                }
                // Soft delete instead of hard delete
                userToDelete.setActive(false);
                userRepo.save(userToDelete);
                break;

            case ADMIN:
                // Admin can delete any user except other admins
                if (userToDelete.getRole().equals(UserRole.ADMIN)) {
                    throw new AccessDeniedException("Cannot delete an admin account");
                }
                // If the user being deleted is a manager, remove from owner's managed users
                if (userToDelete.getManager() != null) {
                    Users manager = userToDelete.getManager();
                    manager.removeManager(userToDelete);
                }
                userRepo.delete(userToDelete);
                break;

            default:
                throw new AccessDeniedException("Not authorized to delete users");
        }
    }

    @Transactional
    public Users updateUserProfile(Users user, UserUpdateRequest updateRequest) {
        try {
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
            }

            System.out.println("Starting profile update for user: " + user.getId());

            // Validate fullName if provided
            if (updateRequest.getFullName() != null) {
                if (updateRequest.getFullName().trim().isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Full name cannot be empty");
                }
                System.out.println("Updating fullName to: " + updateRequest.getFullName());
                user.setFullName(updateRequest.getFullName());
            }

            // Validate email if provided
            if (updateRequest.getEmail() != null) {
                if (!updateRequest.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
                }
                if (!updateRequest.getEmail().equals(user.getEmail()) && 
                    userRepo.existsByEmail(updateRequest.getEmail())) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
                }
                System.out.println("Updating email to: " + updateRequest.getEmail());
                user.setEmail(updateRequest.getEmail());
            }

            // Validate phone number if provided
            if (updateRequest.getPhoneNumber() != null) {
                if (!updateRequest.getPhoneNumber().matches("^\\+?[1-9][0-9]{7,14}$")) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                        "Invalid phone number format. Must be 8-15 digits and may start with +");
                }
                System.out.println("Updating phoneNumber to: " + updateRequest.getPhoneNumber());
                user.setPhoneNumber(updateRequest.getPhoneNumber());
            }

            // Handle image upload if provided
            if (updateRequest.getImage() != null && !updateRequest.getImage().isEmpty()) {
                try {
                    // Delete old image if exists
                    if (user.getImageUrl() != null) {
                        System.out.println("Deleting old image: " + user.getImageUrl());
                        fileStorageService.deleteFile(user.getImageUrl());
                    }
                    // Store new image
                    String imageUrl = fileStorageService.storeFile(updateRequest.getImage());
                    System.out.println("New image stored at: " + imageUrl);
                    user.setImageUrl(imageUrl);
                } catch (IOException e) {
                    System.out.println("Error storing image: " + e.getMessage());
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to store image: " + e.getMessage());
                }
            }

            // Handle password update if provided
            if (updateRequest.getNewPassword() != null) {
                if (updateRequest.getNewPassword().length() < 6) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                        "New password must be at least 6 characters long");
                }
                // Verify current password if changing password
                if (updateRequest.getCurrentPassword() == null ||
                        !passwordEncoder.matches(updateRequest.getCurrentPassword(), user.getPassword())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
                }
                System.out.println("Updating password");
                user.setPassword(passwordEncoder.encode(updateRequest.getNewPassword()));
            }

            System.out.println("Attempting to save user to database");
            Users savedUser = userRepo.save(user);
            System.out.println("User saved successfully with ID: " + savedUser.getId());
            return savedUser;
        } catch (ResponseStatusException e) {
            System.out.println("ResponseStatusException caught: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error updating profile: " + e.getMessage());
        }
    }

    public Users updateUserProfile(Long userId, Users currentUser, UserUpdateRequest updateRequest) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        // Find the user to be updated
        Users userToUpdate = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Check if trying to update an admin
        if (userToUpdate.getRole() == UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cannot update admin accounts");
        }

        // Authorization checks based on roles
        switch (currentUser.getRole()) {
            case ADMIN:
                // Admin can update any user except other admins
                if (userToUpdate.getRole() == UserRole.OWNER || userToUpdate.getRole() == UserRole.MANAGER) {
                    // Update owner's information
                    updateUserFields(userToUpdate, updateRequest);
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin can only update owner accounts");
                }
                break;

            case OWNER:
                // Owner can only update their managers
                if (userToUpdate.getRole() == UserRole.MANAGER && currentUser.managesUser(userId)) {
                    updateUserFields(userToUpdate, updateRequest);
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Owner can only update their managers");
                }
                break;

            default:
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient privileges to update user");
        }

        return userRepo.save(userToUpdate);
    }

    private void updateUserFields(Users user, UserUpdateRequest updateRequest) {
        if (updateRequest.getFullName() != null) {
            user.setFullName(updateRequest.getFullName());
        }
        if (updateRequest.getEmail() != null) {
            user.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getPhoneNumber() != null) {
            user.setPhoneNumber(updateRequest.getPhoneNumber());
        }

        // Handle image upload if provided
        if (updateRequest.getImage() != null && !updateRequest.getImage().isEmpty()) {
            try {
                // Delete old image if exists
                if (user.getImageUrl() != null) {
                    fileStorageService.deleteFile(user.getImageUrl());
                }
                // Store new image
                String imageUrl = fileStorageService.storeFile(updateRequest.getImage());
                user.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store image");
            }
        }

        // Handle password update if provided
        if (updateRequest.getNewPassword() != null) {
            // Verify current password if changing password
            if (updateRequest.getCurrentPassword() == null ||
                    !passwordEncoder.matches(updateRequest.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Current password is incorrect");
            }

            // Encode and set new password
            user.setPassword(passwordEncoder.encode(updateRequest.getNewPassword()));
        }
    }

    @Transactional(readOnly = true)
    public List<?> getAllUsers(Users currentUser) {
        if (currentUser == null) {
            throw new AccessDeniedException("Authentication required");
        }

        switch (currentUser.getRole()) {
            case ADMIN:
                // Admin sees all users
                return userRepo.findAll();

            case OWNER:
                // Owner sees their managed users
                return userRepo.findByManager(currentUser);

            default:
                throw new AccessDeniedException("Insufficient privileges to view user list");
        }
    }

    @Transactional(readOnly = true)
    public Users getUserProfile(Long userId, Users currentUser) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        // Find the user whose profile is being requested
        Users requestedUser = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if trying to access admin profile
        if (requestedUser.getRole() == UserRole.ADMIN) {
            throw new AccessDeniedException("Cannot access admin profiles");
        }

        // Authorization checks based on roles
        switch (currentUser.getRole()) {
            case ADMIN:
                // Admin can view any user's profile except other admins
                if (requestedUser.getRole() == UserRole.OWNER || requestedUser.getRole() == UserRole.MANAGER) {
                    return requestedUser;
                }
                throw new AccessDeniedException("Admin can only view owner and manager profiles");

            case OWNER:
                // Owner can view their own profile or their managers' profiles
                if (currentUser.getId().equals(userId) ||
                        (requestedUser.getRole() == UserRole.MANAGER && currentUser.managesUser(userId))) {
                    return requestedUser;
                }
                throw new AccessDeniedException("Owner can only view their own profile or their managers' profiles");

            case MANAGER:
                // Manager can only view their own profile
                if (currentUser.getId().equals(userId)) {
                    return requestedUser;
                }
                throw new AccessDeniedException("Managers can only view their own profile");

            default:
                throw new AccessDeniedException("Insufficient privileges to view user profile");
        }
    }

    @Transactional(readOnly = true)
    public Users getCurrentUserProfile(Users currentUser) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        
        // Return the current user's profile
        return userRepo.findById(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
