package com.smartlab.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.smartlab.demo.model.Admin;
import com.smartlab.demo.repository.AdminRepository;

@Configuration
public class AdminDataInitializer {

    @Bean
    CommandLineRunner initializeAdmin(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.username:}") String username,
            @Value("${app.admin.password:}") String password) {

        return args -> {

            if (username.isBlank() || password.isBlank()) {
                throw new IllegalStateException(
                    "Set app.admin.username and app.admin.password before starting the application."
                );
            }

            if (!adminRepository.existsByUsername(username)) {

                Admin admin = new Admin(
                    username,
                    passwordEncoder.encode(password),
                    "ADMIN"
                );

                adminRepository.save(admin);

                System.out.println("Initial admin account created.");
            }
        };
    }
}