package com.ticket_tracking_service.scheduler;

import com.ticket_tracking_service.service.AgentService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class AIAgentScheduler {

    private final AgentService agentService;

    public AIAgentScheduler(AgentService agentService){
        this.agentService = agentService;
    }

    @Scheduled(fixedRate = 30000) // every 30 sec
    public void runAgent(){

        System.out.println("AI AGENT → scanning tickets...");

        agentService.autoResolveTickets();
        
    }
}
