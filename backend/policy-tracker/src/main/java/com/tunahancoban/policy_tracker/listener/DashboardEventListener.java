package com.tunahancoban.policy_tracker.listener;

import com.tunahancoban.policy_tracker.model.DTO.events.PolicyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardEventListener {
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void onPolicyEvent(PolicyEvent event){

        log.info("PolicyEvent caught for policyId: {}. Broadcasting to WebSocket.", event.policyId());
        messagingTemplate.convertAndSend("/topic/dashboard-summary", "REFRESH_DASHBOARD");

    }
}