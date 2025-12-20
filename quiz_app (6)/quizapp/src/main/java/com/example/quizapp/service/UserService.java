package com.example.quizapp.service;

import com.example.quizapp.model.User;
import com.example.quizapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(String username, String password) {
        if (userRepository.existsByUsername(username))
            throw new IllegalArgumentException("Username already exists");

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singleton("ROLE_USER"));

        return userRepository.save(user);
    }
}
