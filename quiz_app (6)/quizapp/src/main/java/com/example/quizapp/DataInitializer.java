package com.example.quizapp;

import com.example.quizapp.model.User;
import com.example.quizapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // Создаём admin, только если его ещё нет
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setRoles(Set.of("ROLE_ADMIN", "ROLE_USER"));

            userRepository.save(admin);
            System.out.println("Создан пользователь: admin (пароль: password)");
        }

        // Создаём обычного user, только если его ещё нет
        if (!userRepository.existsByUsername("user")) {
            User regularUser = new User();
            regularUser.setUsername("user");
            regularUser.setPassword(passwordEncoder.encode("password"));
            regularUser.setRoles(Set.of("ROLE_USER"));

            userRepository.save(regularUser);
            System.out.println("Создан пользователь: user (пароль: password)");
        }
    }
}
