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

    // ===============================
    // DTOs
    // ===============================
    public static class OrganizerDTO {
        public Long id;
        public String name;
        public String email;

        public OrganizerDTO(User u) {
            this.id = u.getId();
            this.name = u.getName();
            this.email = u.getEmail();
        }
    }

    public static class DecorationPackageDTO {
        public Long id;
        public String title;
        public String description;
        public Double price;
        public String imageUrl;
        public List<OrganizerDTO> organizers;

        public DecorationPackageDTO(DecorationPackage pkg) {
            this.id = pkg.getId();
            this.title = pkg.getTitle();
            this.description = pkg.getDescription();
            this.price = pkg.getPrice();
            this.imageUrl = pkg.getImageUrl();
            this.organizers = pkg.getOrganizers().stream()
                    .map(OrganizerDTO::new)
                    .toList();
        }
    }

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

    // ===============================
    // GET Endpoints
    // ===============================

    // Fetch all packages (optionally filter by organizer)
    @GetMapping("/all")
    public List<DecorationPackageDTO> getAllPackages(@RequestParam(required = false) Long organizerId) {
        List<DecorationPackage> packages;
        if (organizerId != null) {
            packages = packageRepo.findByOrganizerId(organizerId);
        } else {
            packages = packageRepo.findAll();
        }
        return packages.stream().map(DecorationPackageDTO::new).toList();
    }

    // Fetch packages for a specific organizer
    @GetMapping
    public List<DecorationPackageDTO> getPackages(@RequestParam Long organizerId) {
        return packageRepo.findByOrganizerId(organizerId)
                .stream()
                .map(DecorationPackageDTO::new)
                .toList();
    }

    // Filter packages by organizer and price range
    @GetMapping("/filter")
    public List<DecorationPackageDTO> getFilteredPackages(
            @RequestParam(required = false) Long organizerId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        List<DecorationPackage> packages = packageRepo.findAll();

        if (organizerId != null) {
            packages = packages.stream()
                    .filter(p -> p.getOrganizers().stream()
                            .anyMatch(org -> org.getId().equals(organizerId)))
                    .toList();
        }

        if (minPrice != null) {
            packages = packages.stream()
                    .filter(p -> p.getPrice() != null && p.getPrice() >= minPrice)
                    .toList();
        }

        if (maxPrice != null) {
            packages = packages.stream()
                    .filter(p -> p.getPrice() != null && p.getPrice() <= maxPrice)
                    .toList();
        }

        return packages.stream().map(DecorationPackageDTO::new).toList();
    }

    // Fetch all organizers for dropdowns
    @GetMapping("/organizers")
    public List<UserDTO> getOrganizers() {
        return userRepo.findAll().stream()
                .filter(u -> u.getRole() == Role.ORGANIZER)
                .map(u -> new UserDTO(u.getId(), u.getName(), u.getEmail()))
                .toList();
    }

    // ===============================
    // POST Endpoints
    // ===============================

    // Add a package
    @PostMapping
    public DecorationPackageDTO addPackage(
            @RequestParam("name") String name,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "image", required = false) MultipartFile imageFile
    ) throws IOException {

        User organizer = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (organizer.getRole() != Role.ORGANIZER) {
            throw new RuntimeException("User is not an organizer. Role: " + organizer.getRole());
        }

        DecorationPackage pkg = new DecorationPackage();
        pkg.setTitle(name);
        pkg.setDescription(description);
        pkg.setPrice(price);
        pkg.addOrganizer(organizer);

        // Handle image upload
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path uploadPath = Paths.get("uploads");
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath);
            pkg.setImageUrl("/uploads/" + fileName);
        }

        DecorationPackage savedPackage = packageRepo.save(pkg);
        return new DecorationPackageDTO(savedPackage);
    }

    // Add an organizer to an existing package
    @PostMapping("/{packageId}/organizers/{organizerId}")
    public DecorationPackageDTO addOrganizerToPackage(
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
        return new DecorationPackageDTO(packageRepo.save(pkg));
    }

    // ===============================
    // DELETE Endpoints
    // ===============================

    // Remove an organizer from a package
    @DeleteMapping("/{packageId}/organizers/{organizerId}")
    public DecorationPackageDTO removeOrganizerFromPackage(
            @PathVariable Long packageId,
            @PathVariable Long organizerId) {

        DecorationPackage pkg = packageRepo.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Package not found"));

        User organizer = userRepo.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));

        pkg.removeOrganizer(organizer);
        return new DecorationPackageDTO(packageRepo.save(pkg));
    }

    // Delete a package by ID
    @DeleteMapping("/{id}")
    public void deletePackage(@PathVariable Long id) {
        packageRepo.deleteById(id);
    }
}
