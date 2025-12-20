package com.example.quizapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "user_sessions")
@Getter
@Setter
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;

    private Instant createdAt;
    private Instant expiresAt;
}