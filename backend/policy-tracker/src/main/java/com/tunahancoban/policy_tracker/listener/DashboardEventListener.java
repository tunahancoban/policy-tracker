package com.tunahancoban.policy_tracker.listener;

import com.tunahancoban.policy_tracker.model.DTO.events.PolicyCreatedEvent;
import com.tunahancoban.policy_tracker.model.DTO.events.PolicyDeletedEvent;
import com.tunahancoban.policy_tracker.model.DTO.events.PolicyUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardEventListener {
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void onPolicyCreated(PolicyCreatedEvent event) {
        log.info("PolicyCreatedEvent caught for policyId: {}. Broadcasting to WebSocket.", event.policyId());
        messagingTemplate.convertAndSend("/topic/dashboard-summary", "REFRESH_DASHBOARD");
    }

    @EventListener
    public void onPolicyUpdated(PolicyUpdatedEvent event) {
        log.info("PolicyUpdatedEvent caught for policyId: {}. Broadcasting to WebSocket.", event.policyId());
        messagingTemplate.convertAndSend("/topic/dashboard-summary", "REFRESH_DASHBOARD");
    }

    @EventListener
    public void onPolicyDeleted(PolicyDeletedEvent event) {
        log.info("PolicyDeletedEvent caught for policyId: {}. Broadcasting to WebSocket.", event.policyId());
        messagingTemplate.convertAndSend("/topic/dashboard-summary", "REFRESH_DASHBOARD");
    }

}