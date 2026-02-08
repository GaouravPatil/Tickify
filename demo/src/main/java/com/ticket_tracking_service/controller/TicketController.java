package com.ticket_tracking_service.controller;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ticket_tracking_service.service.TicketService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private static final Logger logger = Logger.getLogger(TicketController.class.getName());

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/user")
    public ResponseEntity<?> fetchUserTickets(@RequestParam long userId) {

        try {
            return ResponseEntity.ok(ticketService.getUserTickets(userId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error fetching tickets");
        }
    }
    @GetMapping("/details")
    public ResponseEntity<?> getTicketDetails(@RequestParam long ticketId) {

        try {
            return ResponseEntity.ok(ticketService.getTicketDetails(ticketId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error loading ticket details");
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTicket(
            HttpSession session,
            @RequestParam long categoryId,
            @RequestParam long priorityId,
            @RequestParam String title,
            @RequestParam String description) {

        logger.log(Level.INFO, "Session is null: {0}", session == null);

        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).body("User not logged in");
        }

        long userId = (Long) session.getAttribute("userId");

        try {
            long ticketId = ticketService.createTicket(
                    userId,
                    categoryId,
                    priorityId,
                    title,
                    description
            );

            return ResponseEntity.ok("Ticket created successfully! Ticket ID: " + ticketId);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating ticket", e);
            return ResponseEntity.internalServerError()
                    .body("Ticket creation failed.");
        }
    }
}
