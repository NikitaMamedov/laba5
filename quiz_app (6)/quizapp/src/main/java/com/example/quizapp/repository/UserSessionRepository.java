package com.example.quizapp.repository;

import com.example.quizapp.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByRefreshToken(String refreshToken);
}
