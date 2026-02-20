package org.example.service;

import org.example.entity.Session;
import org.example.entity.User;
import org.example.model.dto.auth.LoginRequest;
import org.example.model.dto.auth.LoginResponse;
import org.example.repository.SessionRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionRepository sessionRepository;

    public ResponseEntity<?> login(LoginRequest request) {

        // user does not exist
        if (!userRepository.existsByUserName(request.getUserName())) {
            return ResponseEntity
                    .status(404)
                    .body("User not found");

        }
        // user exists
        User user = userRepository.findByUsername(request.getUserName());

        // check password
        if (!user.getPassword().equals(request.getPassword())) {
            return ResponseEntity
                    .status(404)
                    .body("Incorrect Password");
        }

        // user and password is fine
        // check if this user already has an active session
        long now = System.currentTimeMillis();

        List<Session> activeSessions =
                sessionRepository.findByUserIdAndExpiresAtGreaterThan(
                        user.getUserId(),
                        now
                );

        // If active session exists → delete it (force single session per user)
        if (!activeSessions.isEmpty()) {
            sessionRepository.deleteAll(activeSessions);
        }

        // Generate new session token
        String sessionToken = UUID.randomUUID().toString();
        long sessionExpiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000); // 24 hours

        // Store session
        Session session = Session.builder()
                .token(sessionToken)
                .userId(user.getUserId())
                .username(user.getUserName())
                .role(user.getRole())
                .expiresAt(sessionExpiresAt)
                .build();

        sessionRepository.save(session);

        LoginResponse loginResponse = LoginResponse.builder()
                .userName(user.getUserName())
                .role(user.getRole())
                .sessionExpiresAt(sessionExpiresAt)
                .build();

        ResponseCookie cookie = ResponseCookie.from("SESSION_ID", sessionToken)
                .httpOnly(true)
                .secure(true) // true in production (HTTPS)
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginResponse);

    }

    public Session validateSession(String token) {

        if (token == null) return null;

        Session session = sessionRepository.findById(token).orElse(null);
        if (session == null) return null;

        if (session.getExpiresAt() < System.currentTimeMillis()) {
            sessionRepository.deleteById(token);
            return null;
        }

        return session;
    }

    public void logout(String token) {
        sessionRepository.deleteById(token);
    }
}
