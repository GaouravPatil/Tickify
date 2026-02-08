package com.ticket_tracking_service.service;

import com.ticket_tracking_service.model.User;
import com.ticket_tracking_service.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User authenticate(String email, String password) throws Exception {

        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElse(null);


        if (user == null) {
            return null;
        }



        if (!password.equals(user.getPassword())) {
            return null;
        }
        return user;
    }

    private String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = md.digest(password.getBytes());

        StringBuilder sb = new StringBuilder();
        for (byte b : hashedBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

