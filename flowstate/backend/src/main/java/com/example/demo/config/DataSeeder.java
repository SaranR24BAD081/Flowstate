package com.example.demo.config;

import com.example.demo.entity.FlowUser;
import com.example.demo.repository.FlowUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Seeds one demo user per role on first start (skipped when the users already exist). */
@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private final FlowUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(FlowUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seed("admin", "admin@flowstate.io", "Alex Admin", FlowUser.UserRole.PLATFORM_ADMIN);
        seed("coach", "coach@flowstate.io", "Casey Coach", FlowUser.UserRole.FLOW_COACH);
        seed("maya", "maya@flowstate.io", "Maya Chen", FlowUser.UserRole.PRACTITIONER);
    }

    private void seed(String username, String email, String fullName, FlowUser.UserRole role) {
        if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) return;
        FlowUser u = new FlowUser();
        u.setUsername(username);
        u.setEmail(email);
        u.setFullName(fullName);
        u.setRole(role);
        u.setPasswordHash(passwordEncoder.encode("Password123!"));
        u.setIsActive(true);
        userRepository.save(u);
    }
}
