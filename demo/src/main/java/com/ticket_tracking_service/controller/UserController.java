package com.ticket_tracking_service.controller;

import com.ticket_tracking_service.service.TicketService;
import com.ticket_tracking_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final TicketService ticketService;


    public UserController(UserService userService, TicketService ticketService) {
        this.userService = userService;
        this.ticketService = ticketService;
    }

    @CacheEvict(value = "userStats", key = "#userId")
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


    @GetMapping("/dashboard")
    public ResponseEntity<?> userDashboard(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null || !"USER".equals(session.getAttribute("role"))) {
            return ResponseEntity.status(403).build();
        }

        long userId = (Long) session.getAttribute("userId");

        Map<String, Object> response = new HashMap<>();
        response.put("stats", ticketService.getUserStats(userId));
        response.put("tickets", ticketService.getUserTickets(userId));

        return ResponseEntity.ok(response);
    }


    //Redis Cache Annotation
    @CacheEvict(value = "userStats", key = "#userId")
    @PutMapping("/ticket/{id}/resolve")
    public ResponseEntity<?> resolveTicket(@PathVariable long id,
                                           HttpServletRequest request){

        HttpSession session = request.getSession(false);

        if(session == null || !"USER".equals(session.getAttribute("role"))){
            return ResponseEntity.status(403).build();
        }

        long userId = (Long) session.getAttribute("userId");

        ticketService.userResolveTicket(id,userId);

        return ResponseEntity.ok("Resolved");
    }







}
