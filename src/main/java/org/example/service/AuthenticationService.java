package org.example.service;

import org.example.entity.User;
import org.example.model.dto.auth.LoginRequest;
import org.example.repository.UserRepository;
import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> authenticate(LoginRequest request) {
        User user = userRepository.findByUserName(request.getUserName());

        if (user != null && passwordEncoder.matches(request.getPassword(), user.getPassword())) { // add proper hash check in prod
            // 24 hours
            int TOKEN_EXPIRY = 24 * 60 * 60;
            String token = jwtUtil.generateToken(user.getUserName(), TOKEN_EXPIRY);
            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(true) // true in prod
                    .path("/")
                    .sameSite("None") // cross-domain
                    .maxAge(TOKEN_EXPIRY)
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(user); // send minimal info
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
    }

}
