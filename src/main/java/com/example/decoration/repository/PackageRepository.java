package com.example.decoration.repository;

import com.example.decoration.entity.DecorationPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PackageRepository extends JpaRepository<DecorationPackage, Long> {
    
    // Find packages by any of their organizers
    @Query("SELECT DISTINCT p FROM DecorationPackage p JOIN p.organizers o WHERE o.id = :organizerId")
    List<DecorationPackage> findByOrganizerId(@Param("organizerId") Long organizerId);
}