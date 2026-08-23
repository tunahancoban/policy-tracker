package com.tunahancoban.policy_tracker.listener;

import com.tunahancoban.policy_tracker.mapper.InstallmentMapper;
import com.tunahancoban.policy_tracker.model.DTO.events.InstallmentEvent;
import com.tunahancoban.policy_tracker.repository.InstallmentElasticsearchRepository;
import com.tunahancoban.policy_tracker.repository.InstallmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InstallmentElasticsearchSyncListener {

    private final InstallmentRepository installmentRepository;
    private final InstallmentElasticsearchRepository installmentElasticsearchRepository;
    private final InstallmentMapper installmentMapper;

    @EventListener
    public void onInstallmentEvent(InstallmentEvent event) {
        syncInstallment(event.id(), event.eventType().toString());
    }

    private void syncInstallment(String installmentId, String operation) {
        installmentRepository.findById(installmentId).ifPresentOrElse(
                installment -> {
                    try {
                        installmentElasticsearchRepository.save(installmentMapper.toIndex(installment));
                        log.info("ES sync success [{}] - installmentId: {}", operation, installmentId);
                    } catch (Exception e) {
                        log.error("ES sync failed [{}] - installmentId: {}", operation, installmentId, e);
                        // TODO: outbox/retry
                    }
                },
                () -> log.warn("ES sync skipped [{}] - installment not found: {}", operation, installmentId)
        );
    }
}