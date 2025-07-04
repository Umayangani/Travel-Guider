package com.travelguider.backend.controller;

import com.travelguider.backend.dto.AuthRequest;
import com.travelguider.backend.entity.User;
import com.travelguider.backend.entity.Role;
import com.travelguider.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.email).isPresent()) {
            return ResponseEntity.status(409).body(new ErrorResponse("Email already exists"));
        }
        User user = new User();
        user.setName(request.name);
        user.setEmail(request.email);
        user.setPassword(passwordEncoder.encode(request.password));
        if (request.dob != null && !request.dob.isEmpty()) {
            user.setDob(LocalDate.parse(request.dob));
        }
        user.setRole(Role.USER);
        userRepository.save(user);
        return ResponseEntity.ok(new SuccessResponse("User registered successfully"));
    }

    static class RegisterRequest {
        public String name;
        public String email;
        public String password;
        public String dob;
    }
    static class SuccessResponse {
        public String message;
        public SuccessResponse(String message) { this.message = message; }
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