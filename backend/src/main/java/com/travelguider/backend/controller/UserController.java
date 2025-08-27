package com.travelguider.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.travelguider.backend.entity.User;
import com.travelguider.backend.repository.UserRepository;
import com.travelguider.backend.dto.UserProfileUpdateDTO;
import com.travelguider.backend.dto.PasswordChangeDTO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @GetMapping("/test-connection")
    public ResponseEntity<?> testConnection() {
        try {
            long userCount = userRepository.count();
            return ResponseEntity.ok(Map.of(
                "message", "Database connection successful",
                "userCount", userCount
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Database connection failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/test-avatar-access")
    public ResponseEntity<?> testAvatarAccess(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        
        try {
            String uploadsPath = System.getProperty("user.dir") + File.separator + uploadDir + File.separator + "avatars";
            File uploadDirectory = new File(uploadsPath);
            
            return ResponseEntity.ok(Map.of(
                "uploadsPath", uploadsPath,
                "directoryExists", uploadDirectory.exists(),
                "isDirectory", uploadDirectory.isDirectory(),
                "canRead", uploadDirectory.canRead(),
                "files", uploadDirectory.exists() ? uploadDirectory.list() : new String[0]
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error checking uploads directory: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        // Try to find user by email (username is email in most setups)
        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        return ResponseEntity.ok(Map.of("name", user.getName()));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        
        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        
        System.out.println("Getting profile for user: " + user.getEmail() + ", avatar: " + user.getAvatar());
        
        return ResponseEntity.ok(Map.of(
            "name", user.getName() != null ? user.getName() : "",
            "email", user.getEmail() != null ? user.getEmail() : "",
            "dob", user.getDob() != null ? user.getDob().toString() : "",
            "address", user.getAddress() != null ? user.getAddress() : "",
            "avatar", user.getAvatar() != null ? user.getAvatar() : ""
        ));
    }
    
    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> updateData) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        
        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        
        try {
            // Update user fields
            if (updateData.containsKey("name")) {
                user.setName((String) updateData.get("name"));
            }
            if (updateData.containsKey("dob") && updateData.get("dob") != null) {
                String dobString = (String) updateData.get("dob");
                if (!dobString.trim().isEmpty()) {
                    LocalDate dob = LocalDate.parse(dobString);
                    user.setDob(dob);
                }
            }
            if (updateData.containsKey("address")) {
                user.setAddress((String) updateData.get("address"));
            }
            if (updateData.containsKey("avatar")) {
                user.setAvatar((String) updateData.get("avatar"));
            }
            
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to update profile: " + e.getMessage()));
        }
    }
    
    @PostMapping("/upload-avatar")
    public ResponseEntity<?> uploadAvatar(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("avatar") MultipartFile file) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        
        if (file.isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "File is empty"));
        }
        
        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.status(400).body(Map.of("error", "Only image files are allowed"));
        }
        
        // Validate file size (5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.status(400).body(Map.of("error", "File size must be less than 5MB"));
        }
        
        try {
            // Create upload directory if it doesn't exist
            String uploadsPath = System.getProperty("user.dir") + File.separator + uploadDir + File.separator + "avatars";
            File uploadDir = new File(uploadsPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            
            // Save file
            File destinationFile = new File(uploadDir, uniqueFilename);
            file.transferTo(destinationFile);
            
            // Update user avatar in database
            User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
            if (user != null) {
                String avatarUrl = "http://localhost:8090/uploads/avatars/" + uniqueFilename;
                System.out.println("Setting user avatar to: " + avatarUrl);
                user.setAvatar(avatarUrl);
                userRepository.save(user);
                System.out.println("Avatar saved to database for user: " + user.getEmail());
                
                // Return the avatar URL
                return ResponseEntity.ok(Map.of("avatarUrl", avatarUrl));
            } else {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to upload file: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Unexpected error: " + e.getMessage()));
        }
    }
    
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PasswordChangeDTO passwordDTO) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        
        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        
        // Verify current password
        if (!passwordEncoder.matches(passwordDTO.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.status(400).body(Map.of("error", "Current password is incorrect"));
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        
        try {
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to change password"));
        }
    }
}