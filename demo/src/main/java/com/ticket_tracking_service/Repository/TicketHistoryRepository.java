package com.ticket_tracking_service.Repository;

import com.ticket_tracking_service.model.TicketStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketHistoryRepository
        extends JpaRepository<TicketStatusHistory, Long> {
}

