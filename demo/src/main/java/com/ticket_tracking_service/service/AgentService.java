package com.ticket_tracking_service.service;

import com.ticket_tracking_service.Repository.TicketRepository;
import com.ticket_tracking_service.service.TicketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

            System.out.println("AI AGENT → Auto resolving ticket: " + ticketId);

            ticketService.resolveByAI(ticketId);
        }
    }
}
