package com.ticket_tracking_service.service;

import com.ticket_tracking_service.Repository.TicketRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketMonitorService {

    private final TicketRepository ticketRepository;

    public TicketMonitorService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Scheduled(fixedRate = 60000) // every 60 seconds
    public void checkPendingTickets() {

        List<Object[]> pending =
                ticketRepository.findLongPendingTickets();

        for (Object[] t : pending) {

            Long ticketId = ((Number)t[0]).longValue();

            System.out.println(
                    "ADMIN ALERT → Ticket pending too long: " + ticketId
            );

            // later:
            // sendEmail()
            // sendWebhook()
            // pushNotification()
        }
    }
}

