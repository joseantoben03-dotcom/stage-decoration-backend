package com.example.decoration.controller;

import com.example.decoration.dto.BookingRequest;
import com.example.decoration.entity.Booking;
import com.example.decoration.entity.DecorationPackage;
import com.example.decoration.entity.User;
import com.example.decoration.repository.BookingRepository;
import com.example.decoration.repository.PackageRepository;
import com.example.decoration.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://stage-decoration-app.vercel.app",
        "https://stage-decoration-git-b84d47-joseph-antony-benedict-js-projects.vercel.app",
        "https://stage-decoration-9tj5iuwi3-joseph-antony-benedict-js-projects.vercel.app"
})
public class BookingController {

    private final BookingRepository bookingRepo;
    private final UserRepository userRepo;
    private final PackageRepository packageRepo;

    public BookingController(BookingRepository bookingRepo, UserRepository userRepo, PackageRepository packageRepo) {
        this.bookingRepo = bookingRepo;
        this.userRepo = userRepo;
        this.packageRepo = packageRepo;
    }

    // ===========================
    // CREATE BOOKING
    // ===========================
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest bookingRequest) {
        try {
            User customer = userRepo.findById(bookingRequest.getCustomerId()).orElse(null);
            if (customer == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer not found");

            DecorationPackage pkg = packageRepo.findById(bookingRequest.getPackageId()).orElse(null);
            if (pkg == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Package not found");

            User organizer = userRepo.findById(bookingRequest.getOrganizerId()).orElse(null);
            if (organizer == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Organizer not found");

            boolean isOrganizerOfPackage = pkg.getOrganizers().stream()
                    .anyMatch(org -> org.getId().equals(bookingRequest.getOrganizerId()));

            if (!isOrganizerOfPackage) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Selected organizer is not managing this package");
            }

            boolean alreadyBooked = bookingRepo.existsByCustomer_IdAndDecorationPackage_IdAndOrganizer_Id(
                    bookingRequest.getCustomerId(),
                    bookingRequest.getPackageId(),
                    bookingRequest.getOrganizerId()
            );

            if (alreadyBooked) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Package already booked with this organizer");
            }

            Booking booking = new Booking();
            booking.setCustomer(customer);
            booking.setDecorationPackage(pkg);
            booking.setOrganizer(organizer);
            booking.setContactNumber(bookingRequest.getContactNumber());
            booking.setLocation(bookingRequest.getLocation());
            booking.setDay(bookingRequest.getDay());
            booking.setTime(bookingRequest.getTime());
            booking.setBookingTime(LocalDateTime.now());

            Booking savedBooking = bookingRepo.save(booking);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBooking);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create booking: " + e.getMessage());
        }
    }

    // ===========================
    // CANCEL BOOKING
    // ===========================
    @DeleteMapping
    public ResponseEntity<?> cancelBooking(@RequestParam Long customerId,
                                           @RequestParam Long packageId,
                                           @RequestParam(required = false) Long organizerId) {
        try {
            Booking booking;

            if (organizerId != null) {
                booking = bookingRepo.findByCustomer_IdAndDecorationPackage_IdAndOrganizer_Id(
                        customerId, packageId, organizerId).orElse(null);
            } else {
                booking = bookingRepo.findByCustomer_IdAndDecorationPackage_Id(customerId, packageId).orElse(null);
            }

            if (booking == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Booking not found");

            bookingRepo.delete(booking);
            return ResponseEntity.ok("Booking canceled successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to cancel booking: " + e.getMessage());
        }
    }

    // ===========================
    // GET BOOKINGS BY ORGANIZER
    // ===========================
    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<?> getBookingsByOrganizer(@PathVariable Long organizerId) {
        try {
            List<Booking> bookings = bookingRepo.findByOrganizer_Id(organizerId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch bookings: " + e.getMessage());
        }
    }

    // ===========================
    // GET BOOKINGS BY CUSTOMER
    // ===========================
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getBookingsByCustomer(@PathVariable Long customerId) {
        try {
            List<Booking> bookings = bookingRepo.findByCustomer_Id(customerId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch bookings: " + e.getMessage());
        }
    }
}
