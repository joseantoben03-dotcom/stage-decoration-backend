package com.example.decoration.controller;

import com.example.decoration.dto.LoginRequest;
import com.example.decoration.entity.User;
import com.example.decoration.service.StageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://stage-decoration-app.vercel.app",
        "https://stage-decoration-git-b84d47-joseph-antony-benedict-js-projects.vercel.app",
        "https://stage-decoration-9tj5iuwi3-joseph-antony-benedict-js-projects.vercel.app"
})
public class AuthController {

    private final StageService service;

    public AuthController(StageService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User createdUser = service.register(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            // Catch any errors during registration
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = service.login(request.getEmail(), request.getPassword());

            if (user == null) {
                // Return 401 if login fails
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body("Invalid email or password");
            }

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            // Catch unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Login failed: " + e.getMessage());
        }
    }
}
