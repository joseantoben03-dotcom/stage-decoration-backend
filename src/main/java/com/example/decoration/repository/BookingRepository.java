package com.example.decoration.repository;

import com.example.decoration.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    // Find bookings by customer ID
    List<Booking> findByCustomerId(Long customerId);
    List<Booking> findByCustomer_Id(Long customerId);
    
    // Find bookings by organizer ID (direct relationship)
    List<Booking> findByOrganizerId(Long organizerId);
    List<Booking> findByOrganizer_Id(Long organizerId);
    
    // Find specific booking by customer and package (any organizer)
    Optional<Booking> findByCustomer_IdAndDecorationPackage_Id(Long customerId, Long packageId);
    
    // Find specific booking by customer, package, and specific organizer
    Optional<Booking> findByCustomer_IdAndDecorationPackage_IdAndOrganizer_Id(
            Long customerId, Long packageId, Long organizerId);
    
    // Check if booking exists for customer, package, and organizer
    boolean existsByCustomer_IdAndDecorationPackage_IdAndOrganizer_Id(
            Long customerId, Long packageId, Long organizerId);
}