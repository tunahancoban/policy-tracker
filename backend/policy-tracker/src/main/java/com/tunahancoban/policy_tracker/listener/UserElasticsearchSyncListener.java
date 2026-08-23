package com.tunahancoban.policy_tracker.listener;

import com.tunahancoban.policy_tracker.mapper.UserMapper;
import com.tunahancoban.policy_tracker.model.DTO.events.UserEvent;
import com.tunahancoban.policy_tracker.repository.UserElasticsearchRepository;
import com.tunahancoban.policy_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserElasticsearchSyncListener {

    private final UserRepository userRepository;
    private final UserElasticsearchRepository userElasticsearchRepository;
    private final UserMapper userMapper;

    @EventListener
    public void onUserCreated(UserEvent event) {
        syncUser(event.id(), event.eventType().toString());
    }
    private void syncUser(String userId, String operation) {
        userRepository.findById(userId).ifPresentOrElse(
                user -> {
                    try {
                        userElasticsearchRepository.save(userMapper.toIndex(user));
                        log.info("ES sync success [{}] - userId: {}", operation, userId);
                    } catch (Exception e) {
                        log.error("ES sync failed [{}] - userId: {}", operation, userId, e);
                        // TODO: outbox/retry
                    }
                },
                () -> log.warn("ES sync skipped [{}] - user not found: {}", operation, userId)
        );
    }
}