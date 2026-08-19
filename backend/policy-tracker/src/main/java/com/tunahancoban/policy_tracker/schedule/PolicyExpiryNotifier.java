package com.tunahancoban.policy_tracker.schedule;

import com.tunahancoban.policy_tracker.model.entity.Notification;
import com.tunahancoban.policy_tracker.model.entity.Policy;
import com.tunahancoban.policy_tracker.model.enums.NotificationTypes;
import com.tunahancoban.policy_tracker.repository.PolicyRepository;
import com.tunahancoban.policy_tracker.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PolicyExpiryNotifier {

    private final PolicyRepository policyRepository;
    private final NotificationService notificationService;

    private static final List<Integer> THRESHOLDS = List.of(30, 15, 7, 3, 1);

    //@Scheduled(cron = "30 * * * * *") // every day at 10:33
    @Scheduled(fixedRate = 10000)
    public void checkExpiringPolicies() {
        LocalDate today = LocalDate.now();

        for (Integer threshold : THRESHOLDS) {
            LocalDate targetDate = today.plusDays(threshold);

            List<Policy> policies = policyRepository
                    .findByEndDateAndNotifiedThresholdsNotContaining(targetDate, threshold);

            if (policies.isEmpty()) {
                continue;
            }

            log.info("Threshold {} için {} adet poliçe bulundu", threshold, policies.size());

            for (Policy policy : policies) {
                createNotification(policy, threshold);
                policy.getNotifiedThresholds().add(threshold);
                policyRepository.save(policy);
            }
        }
    }

    private void createNotification(Policy policy, int threshold) {
        String title = threshold == 1
                ? "Poliçe Süresi Doluyor"
                : "Poliçe Süresi Yaklaşıyor";

        String message = policy.getPolicyId() + " numaralı poliçenizin süresi "
                + threshold + " gün sonra doluyor.";

        Notification notification = Notification.builder()
                .notificationType(NotificationTypes.POLICY_EXPIRING)
                .policyId(policy.getPolicyId())
                .title(title)
                .userId(policy.getResponsibleUserId())
                .message(message)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationService.send(notification);
    }
}