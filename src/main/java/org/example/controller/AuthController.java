package org.example.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.example.entity.User;
import org.example.model.dto.auth.LoginRequest;
import org.example.model.dto.auth.LoginResponse;
import org.example.repository.UserRepository;
import org.example.service.AuthenticationService;
import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {

        if (authenticationService.authenticate(loginRequest)) {

            int tokenExpirySeconds = 24 * 60 * 60; // 24h
            String token = jwtUtil.generateToken(loginRequest.getUserName(), tokenExpirySeconds);

            User user = userRepository.findByUserName(loginRequest.getUserName());

            long sessionExpiresAt = System.currentTimeMillis() + (tokenExpirySeconds * 1000L);

            LoginResponse loginResponse = LoginResponse.builder()
                    .userName(user.getUserName())
                    .role(user.getRole())
                    .sessionExpiresAt(sessionExpiresAt)
                    .build();

            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(true) // HTTPS only
                    .path("/")
                    .maxAge(tokenExpirySeconds)
                    .sameSite("None") // required for cross-domain
                    .build();

            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(loginResponse);
        } else {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Login failed");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)  // deletes cookie
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok("Logged out successfully");
    }

}
