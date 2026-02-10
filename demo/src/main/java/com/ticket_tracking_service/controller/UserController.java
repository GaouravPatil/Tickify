package com.ticket_tracking_service.controller;

import com.ticket_tracking_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(
            HttpServletRequest request,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role) {

        HttpSession session = request.getSession(false);

        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            return ResponseEntity.status(403).body("Access denied");
        }

        try {
            userService.createUser(username, email, password, role);
            return ResponseEntity.ok("User created successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error creating user: " + e.getMessage());
        }
    }
}
