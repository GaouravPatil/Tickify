package com.ticket_tracking_service.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ticket_tracking_service.Repository.TicketRepository;
import com.ticket_tracking_service.Repository.TicketHistoryRepository;
import com.ticket_tracking_service.model.Ticket;
import com.ticket_tracking_service.model.TicketStatusHistory;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketService {

    private final TelegramNotifierService telegramNotifierService;



    private final TicketRepository ticketRepository;
    private final TicketHistoryRepository historyRepository;

    public TicketService(TicketRepository ticketRepository,
                         TicketHistoryRepository historyRepository,
                         TelegramNotifierService telegramNotifierService) {

        this.ticketRepository = ticketRepository;
        this.historyRepository = historyRepository;
        this.telegramNotifierService = telegramNotifierService;
    }



    /* ================= USER METHODS ================= */

    public List<Object[]> getUserTickets(long userId) {
        return ticketRepository.fetchUserTickets(userId);
    }

    public Map<String, Object> getTicketDetails(long ticketId) {

        Object[] ticket = ticketRepository.fetchTicketDetails(ticketId);

        if (ticket == null) {
            throw new RuntimeException("Ticket not found");
        }

        List<Object[]> history = ticketRepository.fetchTicketHistory(ticketId);

        Map<String, Object> result = new HashMap<>();
        result.put("ticket", ticket);
        result.put("history", history);

        return result;
    }

    /* ================= CREATE TICKET ================= */

    @Transactional
    public long createTicket(long userId,
                             long categoryId,
                             long priorityId,
                             String title,
                             String description) {

        Ticket ticket = new Ticket();
        ticket.setUserId(userId);
        ticket.setCategoryId(categoryId);
        ticket.setPriorityId(priorityId);
        ticket.setTitle(title);
        ticket.setDescription(description);

        Long openStatusId = ticketRepository.findOpenStatusId();

        if (openStatusId == null) {
            ticketRepository.createOpenStatus();
            openStatusId = ticketRepository.findOpenStatusId();
        }

        ticket.setCurrentStatus(openStatusId);

        Ticket savedTicket = ticketRepository.save(ticket);

        TicketStatusHistory history = new TicketStatusHistory();
        history.setTicketId(savedTicket.getTicketId());
        history.setStatusId(openStatusId);
        history.setUpdatedBy(userId);
        history.setRemarks("Ticket created");

        historyRepository.save(history);

        return savedTicket.getTicketId();
    }

    /* ================= ADMIN DASHBOARD ================= */
    @Cacheable("adminStats")
    public Map<String, Long> getAdminStats() {
    
        Object[] r = (Object[]) ticketRepository.getAdminTicketStats();

        Map<String, Long> map = new HashMap<>();

        long open = ((Number) r[0]).longValue();
        long progress = ((Number) r[1]).longValue();
        long closed = ((Number) r[2]).longValue();

        map.put("open", open);
        map.put("progress", progress);
        map.put("closed", closed);
        map.put("total", open + progress + closed);

        return map;
    }


    @Cacheable("admin-dashboard")
    public List<Object[]> getAdminTickets(){
        return ticketRepository.fetchAllTicketsForAdmin();
    }

    public List<Object[]> getCategoryChart(){
        return ticketRepository.categoryDistribution();
    }

    /* ================= ADMIN ACTIONS ================= */
    @CacheEvict(value={"userStats","adminStats","adminTickets"}, allEntries=true)
    @Transactional
    public void updateTicketStatus(long ticketId,
                                   long statusId,
                                   long updatedBy) {

        ticketRepository.updateTicketStatus(ticketId, statusId);

        TicketStatusHistory history = new TicketStatusHistory();
        history.setTicketId(ticketId);
        history.setStatusId(statusId);
        history.setUpdatedBy(updatedBy);
        history.setRemarks("Status updated by admin");

        historyRepository.save(history);
    }


    @Transactional
    public void addSolution(long ticketId,
                            String remarks,
                            long adminId){

        TicketStatusHistory history = new TicketStatusHistory();
        history.setTicketId(ticketId);
        history.setUpdatedBy(adminId);
        history.setRemarks(remarks);

        historyRepository.save(history);
    }

    @CacheEvict(value={"userStats","adminStats","adminTickets"}, allEntries=true)
    @Transactional
    public void resolveByAI(long ticketId) {

        Long closedStatusId = ticketRepository.findClosedStatusId();

        ticketRepository.updateTicketStatus(ticketId, closedStatusId);

        TicketStatusHistory history = new TicketStatusHistory();
        history.setTicketId(ticketId);
        history.setStatusId(closedStatusId);
        history.setRemarks("Resolved automatically by AI");

        historyRepository.save(history);

        telegramNotifierService.send(
                "🤖 AI resolved ticket #" + ticketId
        );
    }
    @CacheEvict(value={"userStats","user-dashboard"}, key="#userId")
    @Transactional
    public void resolveByUser(long ticketId, long userId) {

        Long closedStatusId = ticketRepository.findClosedStatusId();

        ticketRepository.updateTicketStatus(ticketId, closedStatusId);

        TicketStatusHistory history = new TicketStatusHistory();
        history.setTicketId(ticketId);
        history.setStatusId(closedStatusId);
        history.setUpdatedBy(userId);
        history.setRemarks("Resolved by user");

        historyRepository.save(history);
    }
    // User dashboard showing
    //Redis Cache Annotation
    @Cacheable(value = "userStats", key = "#userId")
    public Map<String, Long> getUserStats(long userId){

        Object[] r = (Object[]) ticketRepository.getUserStats(userId);

        Map<String, Long> map = new HashMap<>();

        long open = ((Number) r[0]).longValue();
        long closed = ((Number) r[1]).longValue();

        map.put("open", open);
        map.put("closed", closed);
        map.put("total", open + closed);

        return map;
    }


    @Cacheable(value = "user-dashboard", key = "#userId")
    @Transactional
    public void userResolveTicket(long ticketId,long userId){

        Long closedStatusId = ticketRepository.findClosedStatusId();

        ticketRepository.updateTicketStatus(ticketId,closedStatusId);

        TicketStatusHistory history = new TicketStatusHistory();
        history.setTicketId(ticketId);
        history.setStatusId(closedStatusId);
        history.setUpdatedBy(userId);
        history.setRemarks("Resolved by user");

        historyRepository.save(history);

        ticketRepository.deleteById(ticketId);
    }








}




