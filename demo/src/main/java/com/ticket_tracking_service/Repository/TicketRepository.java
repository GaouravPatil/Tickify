package com.ticket_tracking_service.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ticket_tracking_service.model.Ticket;

import jakarta.transaction.Transactional;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query(value = """
            SELECT
              t.ticket_id,
              c.category_name,
              p.priority_name,
              s.status_name,
              t.created_at
            FROM ticket t
            JOIN category c ON t.category_id = c.category_id
            JOIN priority p ON t.priority_id = p.priority_id
            JOIN ticket_status s ON t.current_status = s.status_id
            WHERE t.user_id = :userId
            """, nativeQuery = true)
    List<Object[]> fetchUserTickets(@Param("userId") long userId);


    @Query(value = "SELECT status_id FROM ticket_status WHERE status_name='OPEN'", nativeQuery = true)
    Long findOpenStatusId();


    @Modifying
    @Transactional
    @Query(value = "INSERT INTO ticket_status (status_name) VALUES ('OPEN')", nativeQuery = true)
    void createOpenStatus();


    @Query(value = """
            SELECT t.ticket_id, t.title, t.description,
                   c.category_name, p.priority_name,
                   s.status_name, t.created_at
            FROM ticket t
            JOIN category c ON t.category_id = c.category_id
            JOIN priority p ON t.priority_id = p.priority_id
            JOIN ticket_status s ON t.current_status = s.status_id
            WHERE t.ticket_id = :ticketId
            """, nativeQuery = true)
    Object[] fetchTicketDetails(@Param("ticketId") long ticketId);


    @Query(value = """
            SELECT ts.status_name, h.updated_by, h.remarks, h.updated_at
            FROM ticket_status_history h
            JOIN ticket_status ts ON h.status_id = ts.status_id
            WHERE h.ticket_id = :ticketId
            ORDER BY h.updated_at DESC
            """, nativeQuery = true)
    List<Object[]> fetchTicketHistory(@Param("ticketId") long ticketId);

    @Query(value = """
            SELECT ticket_id
            FROM ticket
            WHERE current_status = (
                SELECT status_id FROM ticket_status WHERE status_name='OPEN'
            )
            AND created_at < NOW() - INTERVAL '2 minutes'
            """, nativeQuery = true)
    List<Object[]> findLongPendingTickets();

    @Query(value = """
            SELECT
                     COUNT(*) FILTER (WHERE current_status = 1),
                     COUNT(*) FILTER (WHERE current_status = 2),
                    COUNT(*) FILTER (WHERE current_status = 3)
                    FROM ticket
                    """, nativeQuery = true)
    Object getAdminTicketStats();


    @Modifying
    @Query(value = """
                UPDATE ticket
                SET current_status = :statusId
                WHERE ticket_id = :ticketId
            """, nativeQuery = true)
    void updateTicketStatus(@Param("ticketId") long ticketId,
                            @Param("statusId") long statusId);


    @Query(value = """
                SELECT c.category_name, COUNT(*)
                FROM ticket t
                JOIN category c ON t.category_id = c.category_id
                GROUP BY c.category_name
            """, nativeQuery = true)
    List<Object[]> categoryDistribution();

    @Query(value = """
        SELECT
            t.ticket_id,
            t.title,
            c.category_name,
            p.priority_name,
            s.status_name,
            t.created_at
        FROM ticket t
        JOIN category c ON t.category_id = c.category_id
        JOIN priority p ON t.priority_id = p.priority_id
        JOIN ticket_status s ON t.current_status = s.status_id
        ORDER BY t.created_at DESC
        """, nativeQuery = true)
    List<Object[]> fetchAllTicketsForAdmin();

    @Query(value = """
        SELECT ticket_id
        FROM ticket
        WHERE current_status = 2
        AND created_at < NOW() - INTERVAL '5 minutes'
        """, nativeQuery = true)
            List<Object[]> findTicketsReadyForAutoResolve();


    @Query(value = "SELECT status_id FROM ticket_status WHERE status_name='CLOSED'", nativeQuery = true)
    Long findClosedStatusId();

    @Query(value = """
        SELECT 
        COUNT(*) FILTER (WHERE current_status = 1),
        COUNT(*) FILTER (WHERE current_status = 2),
        COUNT(*) FILTER (WHERE current_status = 3)
        FROM ticket
        WHERE user_id = :userId
        """, nativeQuery = true)
            Object[] getUserTicketStats(long userId);

        @Query(value = """
        SELECT
           COUNT(*) FILTER (WHERE current_status = 1),
           COUNT(*) FILTER (WHERE current_status = 3)
        FROM ticket
        WHERE user_id = :userId
        """, nativeQuery = true)
            Object getUserStats(@Param("userId") long userId);







}
