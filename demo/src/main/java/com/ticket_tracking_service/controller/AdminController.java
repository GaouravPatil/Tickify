package com.ticket_tracking_service.controller;

import com.ticket_tracking_service.service.TicketService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final TicketService ticketService;

    public AdminController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PutMapping("/ticket/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable long id,
                                          @RequestBody Map<String,Long> body,
                                          HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            return ResponseEntity.status(403).build();
        }

        Long statusId = body.get("statusId");

        ticketService.updateTicketStatus(
                id,
                statusId,
                (Long)session.getAttribute("userId")
        );

        return ResponseEntity.ok("Updated");
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            return ResponseEntity.status(403).body("Access denied");
        }

        Map<String,Object> response = new HashMap<>();

        response.put("stats", ticketService.getAdminStats());
        response.put("tickets", ticketService.getAdminTickets());
        response.put("categoryChart", ticketService.getCategoryChart());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/ticket/{id}/solution")
    public ResponseEntity<?> addSolution(@PathVariable long id,
                                         @RequestBody Map<String,String> body,
                                         HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            return ResponseEntity.status(403).build();
        }

        String remarks = body.get("remarks");

        if(remarks == null || remarks.isBlank()){
            return ResponseEntity.badRequest().body("Remarks required");
        }

        ticketService.addSolution(
                id,
                remarks,
                (Long) session.getAttribute("userId")
        );

        return ResponseEntity.ok("Solution Added");
    }
}
