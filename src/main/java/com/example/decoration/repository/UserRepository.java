package com.example.decoration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.decoration.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find a user by email
    User findByEmail(String email);

    // Optional: Check if email exists
    boolean existsByEmail(String email);

    // Optional: Check if phone number exists
    boolean existsByPhoneNumber(String phoneNumber);
}
