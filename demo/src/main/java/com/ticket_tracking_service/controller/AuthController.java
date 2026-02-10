package com.ticket_tracking_service.controller;


import com.ticket_tracking_service.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/session")
    public ResponseEntity<?> checkSession(HttpSession session) {

        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).body("NO_SESSION");
        }

        return ResponseEntity.ok(session.getAttribute("role"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            HttpSession session,
            @RequestParam String email,
            @RequestParam String password) {

        try {
            var user = authService.authenticate(email, password);

            if (user == null) {
                return ResponseEntity.status(401).body("INVALID");
            }

            session.setAttribute("userId", user.getUserId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());

            return ResponseEntity.ok(user.getRole());

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("ERROR");
        }
    }

    @PostMapping("/logout") public ResponseEntity<?> logout(HttpSession session)
    {
        if(session != null)
        {
            session.invalidate();
        }
        return ResponseEntity.ok("LOGGED_OUT"); }
}
