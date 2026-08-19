package com.tunahancoban.policy_tracker.listener;

import com.tunahancoban.policy_tracker.mapper.CustomerMapper;
import com.tunahancoban.policy_tracker.model.DTO.events.CustomerCreatedEvent;
import com.tunahancoban.policy_tracker.model.DTO.events.CustomerDeletedEvent;
import com.tunahancoban.policy_tracker.model.DTO.events.CustomerUpdatedEvent;
import com.tunahancoban.policy_tracker.repository.CustomerElasticsearchRepository;
import com.tunahancoban.policy_tracker.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerElasticsearchSyncListener {

    private final CustomerRepository customerRepository;
    private final CustomerElasticsearchRepository customerElasticsearchRepository;
    private final CustomerMapper customerMapper;

    @EventListener
    public void onCustomerCreated(CustomerCreatedEvent event) {
        syncCustomer(event.customerId(), "CREATE");
    }

    @EventListener
    public void onCustomerUpdated(CustomerUpdatedEvent event) {
        syncCustomer(event.customerId(), "UPDATE");
    }

    @EventListener
    public void onCustomerDeleted(CustomerDeletedEvent event) {
        syncCustomer(event.customerId(), "DELETE");
    }

    private void syncCustomer(String customerId, String operation) {
        customerRepository.findByCustomerId(customerId).ifPresentOrElse(
                customer -> {
                    try {
                        customerElasticsearchRepository.save(customerMapper.toIndex(customer));
                        log.info("ES sync success [{}] - customerId: {}", operation, customerId);
                    } catch (Exception e) {
                        log.error("ES sync failed [{}] - customerId: {}", operation, customerId, e);
                        // TODO: outbox/retry
                    }
                },
                () -> log.warn("ES sync skipped [{}] - customer not found: {}", operation, customerId)
        );
    }
}