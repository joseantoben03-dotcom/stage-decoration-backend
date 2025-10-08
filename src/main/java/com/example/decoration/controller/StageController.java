package com.example.decoration.controller;

import com.example.decoration.entity.Booking;
import com.example.decoration.entity.DecorationPackage;
import com.example.decoration.service.StageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stages")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://stage-decoration-app.vercel.app",
        "https://stage-decoration-git-b84d47-joseph-antony-benedict-js-projects.vercel.app",
        "https://stage-decoration-9tj5iuwi3-joseph-antony-benedict-js-projects.vercel.app"
})
public class StageController {

    private final StageService service;

    public StageController(StageService service) {
        this.service = service;
    }   // ------------------- Packages -------------------
    @PostMapping("/packages")
    public ResponseEntity<?> addPackage(@RequestBody DecorationPackage pkg) {
        try {
            DecorationPackage savedPkg = service.addPackage(pkg);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPkg);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add package: " + e.getMessage());
        }
    }

    @GetMapping("/packages")
    public ResponseEntity<?> getPackages(@RequestParam Long organizerId) {
        try {
            List<DecorationPackage> packages = service.getPackagesByOrganizer(organizerId);
            return ResponseEntity.ok(packages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch packages: " + e.getMessage());
        }
    }
 
    // ------------------- Customers -------------------
    @GetMapping("/customers")
    public ResponseEntity<?> getCustomers(@RequestParam Long organizerId) {
        try {
            List<Booking> customers = service.getCustomersByOrganizer(organizerId);
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch customers: " + e.getMessage());
        }
    }
}
