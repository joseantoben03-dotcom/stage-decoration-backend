package com.example.decoration.controller;

import com.example.decoration.entity.DecorationPackage;
import com.example.decoration.entity.User;
import com.example.decoration.entity.Role;
import com.example.decoration.repository.PackageRepository;
import com.example.decoration.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/packages")
@CrossOrigin(origins = "*") // You can specify your frontend URLs
public class PackageController {

    private final PackageRepository packageRepo;
    private final UserRepository userRepo;

    @Value("${app.backend.url:http://localhost:8080}")
    private String backendUrl; // Used to generate absolute image URLs

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public PackageController(PackageRepository packageRepo, UserRepository userRepo) {
        this.packageRepo = packageRepo;
        this.userRepo = userRepo;
    }

    // =========================
    // DTOs
    // =========================
    public static class DecorationPackageDTO {
        public Long id;
        public String title;
        public String description;
        public Double price;
        public String imageUrl;

        public DecorationPackageDTO(DecorationPackage pkg, String backendUrl) {
            this.id = pkg.getId();
            this.title = pkg.getTitle();
            this.description = pkg.getDescription();
            this.price = pkg.getPrice();
            // Make image URL absolute
            this.imageUrl = pkg.getImageUrl() != null
                    ? backendUrl + (pkg.getImageUrl().startsWith("/") ? pkg.getImageUrl() : "/" + pkg.getImageUrl())
                    : null;
        }
    }

    // =========================
    // GET Endpoints
    // =========================
    @GetMapping("/all")
    public List<DecorationPackageDTO> getAllPackages() {
        return packageRepo.findAll().stream()
                .map(p -> new DecorationPackageDTO(p, backendUrl))
                .toList();
    }

    @GetMapping
    public List<DecorationPackageDTO> getPackagesByOrganizer(@RequestParam Long organizerId) {
        return packageRepo.findByOrganizerId(organizerId).stream()
                .map(p -> new DecorationPackageDTO(p, backendUrl))
                .toList();
    }

    // =========================
    // POST Endpoint
    // =========================
    @PostMapping
    public DecorationPackageDTO addPackage(
            @RequestParam String name,
            @RequestParam Double price,
            @RequestParam String description,
            @RequestParam Long userId,
            @RequestParam(value = "image", required = false) MultipartFile imageFile
    ) throws IOException {

        User organizer = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (organizer.getRole() != Role.ORGANIZER)
            throw new RuntimeException("User is not an organizer");

        DecorationPackage pkg = new DecorationPackage();
        pkg.setTitle(name);
        pkg.setDescription(description);
        pkg.setPrice(price);
        pkg.addOrganizer(organizer);

        // Handle image upload
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath);

            pkg.setImageUrl("/" + uploadDir + "/" + fileName); // relative URL
        }

        DecorationPackage saved = packageRepo.save(pkg);
        return new DecorationPackageDTO(saved, backendUrl);
    }

    // =========================
    // DELETE Endpoint
    // =========================
    @DeleteMapping("/{id}")
    public void deletePackage(@PathVariable Long id) {
        packageRepo.deleteById(id);
    }
}
