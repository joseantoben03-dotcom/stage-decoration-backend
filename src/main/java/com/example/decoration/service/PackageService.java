package com.example.decoration.service;

import com.example.decoration.entity.DecorationPackage;
import com.example.decoration.entity.User;
import com.example.decoration.repository.PackageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PackageService {

    private final PackageRepository packageRepo;

    public PackageService(PackageRepository packageRepo) {
        this.packageRepo = packageRepo;
    }

    public List<DecorationPackage> getAllPackages() {
        return packageRepo.findAll();
    }

    public List<DecorationPackage> getPackagesByOrganizer(Long organizerId) {
        return packageRepo.findByOrganizerId(organizerId);
    }

    public DecorationPackage addPackage(DecorationPackage pkg, User organizer) {
        // Automatically add organizer to the package
        pkg.addOrganizer(organizer);
        return packageRepo.save(pkg);
    }

    public void deletePackage(Long id) {
        packageRepo.deleteById(id);
    }
}
