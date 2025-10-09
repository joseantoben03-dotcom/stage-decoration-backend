package com.example.decoration.controller;

import com.example.decoration.entity.DecorationPackage;
import com.example.decoration.entity.User;
import com.example.decoration.entity.Role;
import com.example.decoration.repository.PackageRepository;
import com.example.decoration.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/packages")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://stage-decoration-app.vercel.app",
        "https://stage-decoration-git-b84d47-joseph-antony-benedict-js-projects.vercel.app",
        "https://stage-decoration-9tj5iuwi3-joseph-antony-benedict-js-projects.vercel.app"
})
public class PackageController {

    private final PackageRepository packageRepo;
    private final UserRepository userRepo;

    public PackageController(PackageRepository packageRepo, UserRepository userRepo) {
        this.packageRepo = packageRepo;
        this.userRepo = userRepo;
    }

    // Fetch all packages
    @GetMapping("/all")
    public List<DecorationPackage> getAllPackages(@RequestParam(required = false) Long organizerId) {
        if (organizerId != null) {
            return packageRepo.findByOrganizerId(organizerId);
        }
        return packageRepo.findAll();
    }

    // Fetch packages for a specific organizer
    @GetMapping
    public List<DecorationPackage> getPackages(@RequestParam(required = false) Long organizerId) {
        if (organizerId == null) {
            throw new RuntimeException("organizerId parameter is required");
        }
        return packageRepo.findByOrganizerId(organizerId);
    }

    // Filter packages by organizer and price range
    @GetMapping("/filter")
    public List<DecorationPackage> getFilteredPackages(
            @RequestParam(required = false) Long organizerId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        List<DecorationPackage> packages = packageRepo.findAll();

        if (organizerId != null) {
            packages = packages.stream()
                    .filter(p -> p.getOrganizers().stream()
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

        return packages;
    }

    // Add a package (organizer creates and is automatically added as an organizer)
    @PostMapping
    public DecorationPackage addPackage(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "image", required = false) MultipartFile imageFile
    ) throws IOException {

        System.out.println("=== DEBUG: ADD PACKAGE REQUEST ===");
        System.out.println("Received userId: " + userId);
        System.out.println("Name: " + name);
        System.out.println("Price: " + price);
        System.out.println("Description: " + description);

        // Validate required parameters
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Package name is required");
        }
        if (price == null) {
            throw new RuntimeException("Package price is required");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new RuntimeException("Package description is required");
        }
        if (userId == null) {
            throw new RuntimeException("User ID is required");
        }

        // Find and validate user
        User organizer = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        System.out.println("Found user: " + organizer.getName());
        System.out.println("User role: " + organizer.getRole());

        if (organizer.getRole() == null || organizer.getRole() != Role.ORGANIZER) {
            throw new RuntimeException("User is not an organizer. Role: " + organizer.getRole());
        }

        System.out.println("User role validation passed!");

        // Create package
        DecorationPackage pkg = new DecorationPackage();
        pkg.setTitle(name);
        pkg.setDescription(description);
        pkg.setPrice(price);
        
        // Add the creating organizer to the package
        pkg.addOrganizer(organizer);

        // Handle image upload
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path uploadPath = Paths.get("uploads");
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath);
            pkg.setImageUrl("/uploads/" + fileName);
            System.out.println("Image saved: " + fileName);
        }

        DecorationPackage savedPackage = packageRepo.save(pkg);
        System.out.println("Package saved with ID: " + savedPackage.getId());
        System.out.println("=== DEBUG: END ===");
        
        return savedPackage;
    }

    // Add an organizer to an existing package
    @PostMapping("/{packageId}/organizers/{organizerId}")
    public DecorationPackage addOrganizerToPackage(
            @PathVariable Long packageId,
            @PathVariable Long organizerId) {
        
        DecorationPackage pkg = packageRepo.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Package not found"));
        
        User organizer = userRepo.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));
        
        if (organizer.getRole() != Role.ORGANIZER) {
            throw new RuntimeException("User is not an organizer");
        }
        
        pkg.addOrganizer(organizer);
        return packageRepo.save(pkg);
    }

    // Remove an organizer from a package
    @DeleteMapping("/{packageId}/organizers/{organizerId}")
    public DecorationPackage removeOrganizerFromPackage(
            @PathVariable Long packageId,
            @PathVariable Long organizerId) {
        
        DecorationPackage pkg = packageRepo.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Package not found"));
        
        User organizer = userRepo.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));
        
        pkg.removeOrganizer(organizer);
        return packageRepo.save(pkg);
    }

    // Delete a package by ID
    @DeleteMapping("/{id}")
    public void deletePackage(@PathVariable Long id) {
        packageRepo.deleteById(id);
    }

    // Fetch all organizers for dropdowns
    @GetMapping("/organizers")
    public List<UserDTO> getOrganizers() {
        return userRepo.findAll().stream()
                .filter(u -> u.getRole() == Role.ORGANIZER)
                .map(u -> new UserDTO(u.getId(), u.getName(), u.getEmail()))
                .collect(Collectors.toList());
    }

    // DTO class for organizer info
    public static class UserDTO {
        public Long id;
        public String name;
        public String email;

        public UserDTO(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
    }
}