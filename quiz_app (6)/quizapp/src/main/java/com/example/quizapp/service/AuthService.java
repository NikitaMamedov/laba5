package com.example.quizapp.service;

import com.example.quizapp.dto.TokenPair;
import com.example.quizapp.model.User;
import com.example.quizapp.model.UserSession;
import com.example.quizapp.model.SessionStatus;
import com.example.quizapp.repository.UserRepository;
import com.example.quizapp.repository.UserSessionRepository;
import com.example.quizapp.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final UserSessionRepository sessionRepo;
    private final JwtTokenProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService; // MyUserDetailsService

    @Transactional
    public TokenPair login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String access = jwtProvider.generateAccessToken(userDetails.getUsername());
        String refresh = jwtProvider.generateRefreshToken(userDetails.getUsername());

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserSession session = new UserSession();
        session.setUser(user);
        session.setRefreshToken(refresh);
        session.setStatus(SessionStatus.ACTIVE);
        session.setCreatedAt(Instant.now());
        session.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));
        sessionRepo.save(session);

        return new TokenPair(access, refresh);
    }

    @Transactional
    public TokenPair refresh(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken, "refresh")) {
            throw new IllegalStateException("Invalid or expired refresh token");
        }

        UserSession session = sessionRepo.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalStateException("Session not found"));

        if (session.getStatus() != SessionStatus.ACTIVE || Instant.now().isAfter(session.getExpiresAt())) {
            session.setStatus(SessionStatus.REVOKED);
            sessionRepo.save(session);
            throw new IllegalStateException("Session expired or revoked");
        }

        // Revoke old session
        session.setStatus(SessionStatus.REVOKED);
        sessionRepo.save(session);

        User user = session.getUser();
        String newAccess = jwtProvider.generateAccessToken(user.getUsername());
        String newRefresh = jwtProvider.generateRefreshToken(user.getUsername());

        UserSession newSession = new UserSession();
        newSession.setUser(user);
        newSession.setRefreshToken(newRefresh);
        newSession.setStatus(SessionStatus.ACTIVE);
        newSession.setCreatedAt(Instant.now());
        newSession.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));
        sessionRepo.save(newSession);

        return new TokenPair(newAccess, newRefresh);
    }
}