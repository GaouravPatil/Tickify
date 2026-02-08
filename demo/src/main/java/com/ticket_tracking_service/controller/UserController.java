package com.ticket_tracking_service.controller;
import com.ticket_tracking_service.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(
            HttpSession session,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password) {


        if (session == null || !"AGENT".equals(session.getAttribute("role"))) {
            return ResponseEntity.status(403).body("Access denied");
        }

        try {
            userService.createUser(username, email, password);
            return ResponseEntity.ok("User created successfully");

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error creating user: " + e.getMessage());
        }
    }
}

