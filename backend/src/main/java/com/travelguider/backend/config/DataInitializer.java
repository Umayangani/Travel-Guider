package com.travelguider.backend.config;

import com.travelguider.backend.entity.Role;
import com.travelguider.backend.entity.User;
import com.travelguider.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create default admin user if no users exist
        if (userRepository.count() == 0) {
            createDefaultAdmin();
            createDefaultUser();
        }
    }

    private void createDefaultAdmin() {
        User admin = new User();
        admin.setName("Admin");
        admin.setEmail("admin@travelguider.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
        System.out.println("✅ Default admin created:");
        System.out.println("   Email: admin@travelguider.com");
        System.out.println("   Password: admin123");
    }

    private void createDefaultUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("user@travelguider.com");
        user.setPassword(passwordEncoder.encode("user123"));
        user.setRole(Role.USER);
        userRepository.save(user);
        System.out.println("✅ Default user created:");
        System.out.println("   Email: user@travelguider.com");
        System.out.println("   Password: user123");
    }
}
