package com.example.decoration.controller;

import com.example.decoration.entity.Booking;
import com.example.decoration.entity.DecorationPackage;
import com.example.decoration.entity.User;
import com.example.decoration.entity.Role;
import com.example.decoration.repository.BookingRepository;
import com.example.decoration.repository.PackageRepository;
import com.example.decoration.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = {
       
        "https://stage-decoration-app.vercel.app",
        "https://stage-decoration-git-b84d47-joseph-antony-benedict-js-projects.vercel.app",
        "https://stage-decoration-9tj5iuwi3-joseph-antony-benedict-js-projects.vercel.app"
})
public class CustomerController {

    private final BookingRepository bookingRepo;
    private final PackageRepository packageRepo;
    private final UserRepository userRepo;

    public CustomerController(BookingRepository bookingRepo, PackageRepository packageRepo, UserRepository userRepo) {
        this.bookingRepo = bookingRepo;
        this.packageRepo = packageRepo;
        this.userRepo = userRepo;
    }

    // ===========================
    // Get all customers for an organizer
    // ===========================
    @GetMapping("/booked-customers")
    public ResponseEntity<?> getCustomers(@RequestParam Long organizerId) {
        try {
            List<Booking> bookings = bookingRepo.findByOrganizerId(organizerId);
            List<CustomerDTO> customers = bookings.stream()
                    .map(b -> new CustomerDTO(
                            b.getCustomer().getId(),
                            b.getCustomer().getName(),
                            b.getCustomer().getEmail(),
                            b.getCustomer().getPhoneNumber()
                    ))
                    .distinct()
                    .collect(Collectors.toList());

            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch customers: " + e.getMessage());
        }
    }

    // ===========================
    // Get all organizers
    // ===========================
    @GetMapping("/organizers")
    public ResponseEntity<?> getOrganizers() {
        try {
            List<OrganizerDTO> organizers = userRepo.findAll().stream()
                    .filter(u -> u.getRole() == Role.ORGANIZER)
                    .map(u -> new OrganizerDTO(
                            u.getId(),
                            u.getName(),
                            u.getEmail(),
                            u.getPhoneNumber()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(organizers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch organizers: " + e.getMessage());
        }
    }

    // ===========================
    // Get packages filtered by organizer and/or price
    // ===========================
    @GetMapping("/packages")
    public ResponseEntity<?> getFilteredPackages(
            @RequestParam(required = false) Long organizerId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        try {
            List<DecorationPackage> packages = packageRepo.findAll();

            if (organizerId != null) {
                packages = packages.stream()
                        .filter(p -> p.getOrganizers() != null && 
                                p.getOrganizers().stream()
                                        .anyMatch(org -> org.getId().equals(organizerId)))
                        .collect(Collectors.toList());
            }

            if (minPrice != null) {
                packages = packages.stream()
                        .filter(p -> p.getPrice() != null && p.getPrice() >= minPrice)
                        .collect(Collectors.toList());
            }

            if (maxPrice != null) {
                packages = packages.stream()
                        .filter(p -> p.getPrice() != null && p.getPrice() <= maxPrice)
                        .collect(Collectors.toList());
            }

            return ResponseEntity.ok(packages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch packages: " + e.getMessage());
        }
    }

    // ===========================
    // DTO classes
    // ===========================
    public static class CustomerDTO {
        public Long id;
        public String name;
        public String email;
        public String phoneNumber;

        public CustomerDTO(Long id, String name, String email, String phoneNumber) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.phoneNumber = phoneNumber;
        }
    }

    public static class OrganizerDTO {
        public Long id;
        public String name;
        public String email;
        public String phoneNumber;

        public OrganizerDTO(Long id, String name, String email, String phoneNumber) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.phoneNumber = phoneNumber;
        }
    }
}
