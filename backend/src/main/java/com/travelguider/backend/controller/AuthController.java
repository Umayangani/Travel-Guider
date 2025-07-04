package com.travelguider.backend.controller;

import com.travelguider.backend.dto.AuthRequest;
import com.travelguider.backend.entity.User;
import com.travelguider.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(new ErrorResponse("Invalid email or password"));
        }
        User user = userOpt.get();
        if (!passwordEncoder.matches(request.password, user.getPassword())) {
            return ResponseEntity.status(401).body(new ErrorResponse("Invalid email or password"));
        }
        // Return role for frontend
        return ResponseEntity.ok(new RoleResponse(user.getRole().name().toLowerCase()));
    }

    static class ErrorResponse {
        public String message;
        public ErrorResponse(String message) { this.message = message; }
    }
    static class RoleResponse {
        public String role;
        public RoleResponse(String role) { this.role = role; }
    }
}
