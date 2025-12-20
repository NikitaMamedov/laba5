package com.example.quizapp.controller;

import com.example.quizapp.dto.TokenPair;
import com.example.quizapp.dto.UserDto;
import com.example.quizapp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public TokenPair login(@RequestBody UserDto dto) {
        return authService.login(dto.getUsername(), dto.getPassword());
    }

    @PostMapping("/refresh")
    public TokenPair refresh(@RequestBody Map<String, String> request) {
        return authService.refresh(request.get("refreshToken"));
    }
}