package com.ticket_tracking_service.service;

import com.ticket_tracking_service.Repository.UserRepository;
import com.ticket_tracking_service.model.User;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(String username,
                           String email,
                           String password,
                           String role) throws Exception {

        if(userRepository.existsByUsernameOrEmail(username, email)){
            throw new RuntimeException("User already exists");
        }

        String hashedPassword = hashPassword(password);

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(hashedPassword);   // ⚠️ use passwordHash column
        user.setRole(role);                     // ADMIN / AGENT / USER
        user.isActive(true);

        userRepository.save(user);
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

