package com.ticket_tracking_service.service;

import com.ticket_tracking_service.Repository.TicketRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgentService {

    private final TicketRepository ticketRepository;
    private final TicketService ticketService;

    public AgentService(TicketRepository ticketRepository,
                        TicketService ticketService) {
        this.ticketRepository = ticketRepository;
        this.ticketService = ticketService;
    }

    @Transactional
    public void autoResolveTickets() {

        List<Object[]> candidates =
                ticketRepository.findTicketsReadyForAutoResolve();

        for(Object[] t : candidates){

            long ticketId = ((Number)t[0]).longValue();
            long adminId  = 1; // system admin / AI agent id

            System.out.println("AI AGENT → Auto resolving ticket: " + ticketId);



            ticketService.updateTicketStatus(ticketId,3,adminId);
        }
    }
}
