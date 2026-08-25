package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.model.entity.Notification;
import com.tunahancoban.policy_tracker.model.entity.User;
import com.tunahancoban.policy_tracker.model.enums.Role;
import com.tunahancoban.policy_tracker.repository.NotificationRepository;
import com.tunahancoban.policy_tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationServiceImp Unit Tests")
class NotificationServiceTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private SimpMessagingTemplate messagingTemplate;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private NotificationServiceImp notificationService;

    private Notification sampleNotification;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id("user-1")
                .fullName("Test User")
                .email("test@example.com")
                .password("hashed")
                .role(Role.ROLE_USER)
                .build();

        sampleNotification = Notification.builder()
                .id("notif-1")
                .userId("user-1")
                .title("Poliçe Uyarısı")
                .message("Poliçeniz 7 gün içinde sona erecek.")
                .read(false)
                .build();
    }

    // ─────────────────────────────────────────────
    // getNotifications
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("getNotifications")
    class GetNotifications {

        @Test
        @DisplayName("Kullanıcıya ait bildirimler sayfalı olarak getirilmeli")
        void shouldReturnPageOfNotificationsForUser() {
            Page<Notification> page = new PageImpl<>(List.of(sampleNotification));
            when(notificationRepository.findNotificationsByUserId(eq("user-1"), any()))
                    .thenReturn(page);

            Page<Notification> result = notificationService.getNotifications("user-1", PageRequest.of(0, 10));

            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().get(0).getTitle()).isEqualTo("Poliçe Uyarısı");
        }
    }

    // ─────────────────────────────────────────────
    // send
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("send")
    class Send {

        @Test
        @DisplayName("Bildirim kaydedilmeli ve WebSocket üzerinden gönderilmeli")
        void shouldSaveAndSendViaWebSocket() {
            when(notificationRepository.save(sampleNotification)).thenReturn(sampleNotification);
            when(userRepository.findById("user-1")).thenReturn(Optional.of(sampleUser));

            Notification result = notificationService.send(sampleNotification);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("notif-1");

            verify(messagingTemplate, times(1)).convertAndSendToUser(
                    eq("test@example.com"),
                    eq("/queue/notifications"),
                    eq(sampleNotification)
            );
        }

        @Test
        @DisplayName("Kullanıcı bulunamazsa WebSocket gönderimi yapılmamalı ama exception fırlatılmamalı")
        void shouldNotSendWebSocketWhenUserNotFound() {
            when(notificationRepository.save(sampleNotification)).thenReturn(sampleNotification);
            when(userRepository.findById("user-1")).thenReturn(Optional.empty());

            // Exception fırlatmamalı, sadece log yazmalı
            assertThatNoException().isThrownBy(() -> notificationService.send(sampleNotification));

            verify(messagingTemplate, never()).convertAndSendToUser(any(), any(), any());
        }

        @Test
        @DisplayName("Bildirim kaydedilmeli ama kullanıcı yoksa gönderim atlanmalı")
        void shouldSaveNotificationEvenIfUserMissing() {
            when(notificationRepository.save(sampleNotification)).thenReturn(sampleNotification);
            when(userRepository.findById("user-1")).thenReturn(Optional.empty());

            Notification result = notificationService.send(sampleNotification);

            assertThat(result).isNotNull();
            verify(notificationRepository, times(1)).save(sampleNotification);
        }
    }

    // ─────────────────────────────────────────────
    // markAsRead
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("markAsRead")
    class MarkAsRead {

        @Test
        @DisplayName("Mevcut bildirim okundu olarak işaretlenmeli")
        void shouldMarkNotificationAsRead() {
            when(notificationRepository.findById("notif-1"))
                    .thenReturn(Optional.of(sampleNotification));

            notificationService.markAsRead("notif-1");

            assertThat(sampleNotification.isRead()).isTrue();
            verify(notificationRepository, times(1)).save(sampleNotification);
        }

        @Test
        @DisplayName("Bulunmayan bildirim için hiçbir şey yapılmamalı")
        void shouldDoNothingWhenNotificationNotFound() {
            when(notificationRepository.findById("ghost"))
                    .thenReturn(Optional.empty());

            assertThatNoException().isThrownBy(() -> notificationService.markAsRead("ghost"));
            verify(notificationRepository, never()).save(any());
        }
    }

    // ─────────────────────────────────────────────
    // getUnreadCount
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("getUnreadCount")
    class GetUnreadCount {

        @Test
        @DisplayName("Okunmamış bildirim sayısı doğru döndürülmeli")
        void shouldReturnCorrectUnreadCount() {
            when(notificationRepository.countByUserIdAndReadIsFalse("user-1")).thenReturn(5L);

            Long count = notificationService.getUnreadCount("user-1");

            assertThat(count).isEqualTo(5L);
        }

        @Test
        @DisplayName("Okunmamış bildirim yoksa 0 dönmeli")
        void shouldReturnZeroWhenNoUnread() {
            when(notificationRepository.countByUserIdAndReadIsFalse("user-1")).thenReturn(0L);

            Long count = notificationService.getUnreadCount("user-1");

            assertThat(count).isZero();
        }
    }
}
