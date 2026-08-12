package com.tunahancoban.policy_tracker.schedule;

import com.tunahancoban.policy_tracker.model.entity.Installment;
import com.tunahancoban.policy_tracker.model.entity.Notification;
import com.tunahancoban.policy_tracker.model.enums.NotificationTypes;
import com.tunahancoban.policy_tracker.model.enums.PaymentStatus;
import com.tunahancoban.policy_tracker.repository.InstallmentRepository;
import com.tunahancoban.policy_tracker.repository.NotificationRepository;
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
public class InstallmentDueNotifier {

    private final InstallmentRepository installmentRepository;
    private final NotificationRepository notificationRepository;

    private static final List<Integer> THRESHOLDS = List.of(3, 0, -1);
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 8 * * *") // EVERYDAY
    public void checkInstallmentReminders() {
        LocalDate today = LocalDate.now();

        for (Integer threshold : THRESHOLDS) {
            LocalDate targetDueDate = today.plusDays(threshold);

            List<Installment> targets = installmentRepository
                    .findByDueDateAndStatusAndNotifiedThresholdsNotContaining(
                            targetDueDate, PaymentStatus.UNPAID, threshold
                    );

            if (targets.isEmpty()) {
                continue;
            }

            log.info("Threshold {} için {} adet taksit bulundu", threshold, targets.size());

            for (Installment installment : targets) {
                createNotification(installment, threshold);
                installment.getNotifiedThresholds().add(threshold);
                installment.setUpdatedAt(LocalDateTime.now());
                installmentRepository.save(installment);
            }
        }
    }

    private void createNotification(Installment installment, int threshold) {
        String title;
        String message;

        if (threshold > 0) {
            title = "Taksit Ödeme Hatırlatması";
            message = installment.getInstallmentNo() + ". taksitiniz ("
                    + installment.getAmount() + " TL) " + threshold + " gün sonra vadesi doluyor.";
        } else if (threshold == 0) {
            title = "Taksit Vade Günü";
            message = installment.getInstallmentNo() + ". taksitinizin ("
                    + installment.getAmount() + " TL) vade günü bugün.";
        } else {
            title = "Gecikmiş Taksit";
            message = installment.getInstallmentNo() + ". taksitiniz ("
                    + installment.getAmount() + " TL) gecikti, lütfen ödeme yapın.";
        }

        Notification notification = Notification.builder()
                .notificationType(threshold < 0 ? NotificationTypes.INSTALLMENT_OVERDUE : NotificationTypes.INSTALLMENT_OVERDUE)
                .policyId(installment.getPolicyId())
                .installmentId(installment.getId())
                .title(title)
                .message(message)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

       notificationService.send(notification);
    }
}