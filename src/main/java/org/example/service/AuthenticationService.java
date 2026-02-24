package org.example.service;

import org.example.entity.User;
import org.example.model.dto.auth.LoginRequest;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    public boolean authenticate(LoginRequest request) {
        // user does not exist
        if (!userRepository.existsByUserName(request.getUserName())) {
            return false;
        }
        // user exists
        User user = userRepository.findByUserName(request.getUserName());
        // check password
        return user.getPassword().equals(request.getPassword());
    }

}
