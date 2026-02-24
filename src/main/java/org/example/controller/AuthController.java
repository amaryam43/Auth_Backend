package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.User;
import org.example.model.dto.auth.LoginRequest;
import org.example.repository.UserRepository;
import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("Login request received");
        log.info("finding user with username : {} and password : {}", request.getUserName(), request.getPassword());
        User user = userRepository.findByUserName(request.getUserName());
        log.info("User found, {}" ,user);

        if (user != null && user.getPassword().equals(request.getPassword())) { // add proper hash check in prod
            log.info("User password is correct");
            // 24 hours
            int TOKEN_EXPIRY = 24 * 60 * 60;

            log.info("Generating Token...");
            String token = jwtUtil.generateToken(user.getUserName(), TOKEN_EXPIRY);
            log.info("Token generated");

            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(true) // true in prod
                    .path("/")
                    .sameSite("None") // cross-domain
                    .maxAge(TOKEN_EXPIRY)
                    .build();
            log.info("Cookie generated");

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(user); // send minimal info
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("None")
                .maxAge(0) // immediately expire
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logged out");
    }
}