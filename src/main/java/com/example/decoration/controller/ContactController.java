package com.example.decoration.controller;

import com.example.decoration.dto.ContactRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://stage-decoration-app.vercel.app",
        "https://stage-decoration-git-b84d47-joseph-antony-benedict-js-projects.vercel.app",
        "https://stage-decoration-9tj5iuwi3-joseph-antony-benedict-js-projects.vercel.app"
})
public class ContactController {

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/send-email")
    public ResponseEntity<?> sendEmail(@RequestBody ContactRequest request) {
        try {
            if (request.getFromEmail() == null || request.getFromName() == null || request.getMessage() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please fill in all required fields");
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("your-email@example.com"); // replace with your actual email
            message.setSubject("New Contact Inquiry from " + request.getFromName());
            message.setText(
                    "Name: " + request.getFromName() + "\n" +
                    "Email: " + request.getFromEmail() + "\n" +
                    "Mobile: " + (request.getFromMobile() != null ? request.getFromMobile() : "N/A") + "\n\n" +
                    "Message: " + request.getMessage()
            );

            mailSender.send(message);

            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending email: " + e.getMessage());
        }
    }
}
