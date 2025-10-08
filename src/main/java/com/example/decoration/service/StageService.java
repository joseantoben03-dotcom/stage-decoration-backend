package com.example.decoration.service;

import com.example.decoration.entity.*;
import com.example.decoration.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StageService {

    private final UserRepository userRepo;
    private final PackageRepository packageRepo;
    private final BookingRepository bookingRepo;

    public StageService(UserRepository userRepo, PackageRepository packageRepo, BookingRepository bookingRepo) {
        this.userRepo = userRepo;
        this.packageRepo = packageRepo;
        this.bookingRepo = bookingRepo;
    }

    // ---------------- AUTH ----------------
    public User register(User user) {
        // TODO: hash password in production
        return userRepo.save(user);
    }

    public User login(String email, String password) {
        User user = userRepo.findByEmail(email);
        if (user == null) throw new RuntimeException("User not found");
        if (!user.getPassword().equals(password))
            throw new RuntimeException("Invalid password");
        return user;
    }

    // ---------------- PACKAGES ----------------
    public DecorationPackage addPackage(DecorationPackage pkg) {
        return packageRepo.save(pkg);
    }

    public List<DecorationPackage> getPackagesByOrganizer(Long organizerId) {
        return packageRepo.findByOrganizerId(organizerId);
    }

    // ---------------- CUSTOMERS ----------------
    public List<Booking> getCustomersByOrganizer(Long organizerId) {
        return bookingRepo.findByOrganizerId(organizerId);
    }
}
