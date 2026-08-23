package com.tunahancoban.policy_tracker.listener;

import com.tunahancoban.policy_tracker.mapper.CustomerMapper;
import com.tunahancoban.policy_tracker.model.DTO.events.CustomerEvent;
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
    public void onCustomerEvent(CustomerEvent event){ syncCustomer(event.customerId(), event.eventType().toString());}


    private void syncCustomer(String customerId, String operation) {
        customerRepository.findByCustomerId(customerId).ifPresentOrElse(
                customer -> {
                    try {
                        customerElasticsearchRepository.save(customerMapper.toIndex(customer));
                        log.info("ES sync success [{}] - customerId: {}", operation, customerId);
                    } catch (Exception e) {
                        log.error("ES sync failed [{}] - customerId: {}", operation, customerId, e);
                    }
                },
                () -> log.warn("ES sync skipped [{}] - customer not found: {}", operation, customerId)
        );
    }
}