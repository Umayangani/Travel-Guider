package com.travelguider.backend.controller;

import com.travelguider.backend.entity.Role;
import com.travelguider.backend.entity.User;
import com.travelguider.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class AdminController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/create")
    public ResponseEntity<?> createAdmin(@RequestBody AdminRequest request) {
        if (userRepository.findByEmail(request.email).isPresent()) {
            return ResponseEntity.status(409).body(new ErrorResponse("Email already exists"));
        }
        User user = new User();
        user.setName(request.name);
        user.setEmail(request.email);
        user.setPassword(passwordEncoder.encode(request.password));
        if (request.dob != null && !request.dob.isEmpty()) {
            user.setDob(java.time.LocalDate.parse(request.dob));
        }
        user.setRole(Role.ADMIN);
        userRepository.save(user);
        return ResponseEntity.ok(new SuccessResponse("Admin created successfully"));
    }

    static class AdminRequest {
        public String name;
        public String email;
        public String password;
        public String dob;
    }
    static class ErrorResponse {
        public String message;
        public ErrorResponse(String message) { this.message = message; }
    }
    static class SuccessResponse {
        public String message;
        public SuccessResponse(String message) { this.message = message; }
    }
}
