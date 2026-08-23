package com.tunahancoban.policy_tracker.listener;

import com.tunahancoban.policy_tracker.mapper.PolicyMapper;
import com.tunahancoban.policy_tracker.model.DTO.events.PolicyEvent;
import com.tunahancoban.policy_tracker.repository.PolicyElasticsearchRepository;
import com.tunahancoban.policy_tracker.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PolicyElasticsearchSyncListener {

    private final PolicyRepository policyRepository;
    private final PolicyElasticsearchRepository policyElasticsearchRepository;
    private final PolicyMapper policyMapper;

    @EventListener
    public void onPolicyEvent(PolicyEvent event) {
        syncPolicy(event.policyId(), event.eventType().toString());
    }

    private void syncPolicy(String policyId, String operation) {
        policyRepository.getPolicyByPolicyId(policyId).ifPresentOrElse(
                policy -> {
                    try {
                        policyElasticsearchRepository.save(policyMapper.toDocument(policy));
                        log.info("ES sync success [{}] - policyId: {}", operation, policyId);
                    } catch (Exception e) {
                        log.error("ES sync failed [{}] - policyId: {}", operation, policyId, e);
                        // TODO: outbox/retry
                    }
                },
                () -> log.warn("ES sync skipped [{}] - policy not found: {}", operation, policyId)
        );
    }
}