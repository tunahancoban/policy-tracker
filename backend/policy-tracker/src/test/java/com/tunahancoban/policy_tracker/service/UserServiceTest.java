package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.mapper.UserMapper;
import com.tunahancoban.policy_tracker.model.DTO.events.UserEvent;
import com.tunahancoban.policy_tracker.model.DTO.request.auth.RegisterRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.user.UpdateUserRequest;
import com.tunahancoban.policy_tracker.model.entity.User;
import com.tunahancoban.policy_tracker.model.enums.Role;
import com.tunahancoban.policy_tracker.model.exceptions.BusinessValidationException;
import com.tunahancoban.policy_tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImp Unit Tests")
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserMapper userMapper;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserServiceImp userService;

    private User sampleUser;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id("user-1")
                .fullName("Admin User")
                .email("admin@example.com")
                .password("hashed_password")
                .role(Role.ROLE_ADMIN)
                .isActive(true)
                .build();

        registerRequest = new RegisterRequest();
        registerRequest.setFullName("Admin User");
        registerRequest.setEmail("admin@example.com");
        registerRequest.setPassword("plaintext123");
        registerRequest.setRole(Role.ROLE_ADMIN);
    }

    // ─────────────────────────────────────────────
    // createUser
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("createUser")
    class CreateUser {

        @Test
        @DisplayName("Yeni kullanıcı başarıyla oluşturulmalı ve şifre hashlenmeli")
        void shouldCreateUserWithHashedPassword() {
            when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
            when(passwordEncoder.encode("plaintext123")).thenReturn("hashed_password");
            when(userMapper.toEntity(registerRequest)).thenReturn(sampleUser);
            when(userRepository.save(any(User.class))).thenReturn(sampleUser);

            User result = userService.createUser(registerRequest);

            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo("admin@example.com");
            assertThat(result.getPassword()).isEqualTo("hashed_password");

            verify(passwordEncoder, times(1)).encode("plaintext123");
            verify(eventPublisher, times(1)).publishEvent(any(UserEvent.class));
        }

        @Test
        @DisplayName("Var olan e-posta ile kullanıcı oluşturulmamalı")
        void shouldThrowConflictWhenEmailAlreadyExists() {
            when(userRepository.existsByEmail("admin@example.com")).thenReturn(true);

            assertThatThrownBy(() -> userService.createUser(registerRequest))
                    .isInstanceOf(BusinessValidationException.class)
                    .hasMessageContaining("email");

            verify(userRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
        }
    }

    // ─────────────────────────────────────────────
    // deleteUser
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("deleteUser")
    class DeleteUser {

        @Test
        @DisplayName("Kullanıcı soft-delete edilmeli ve event yayınlanmalı")
        void shouldSoftDeleteUser() {
            when(userRepository.findById("user-1")).thenReturn(Optional.of(sampleUser));
            when(userRepository.save(any(User.class))).thenReturn(sampleUser);

            userService.deleteUser("user-1");

            assertThat(sampleUser.getIsActive()).isFalse();
            assertThat(sampleUser.getDeletedAt()).isNotNull();
            verify(eventPublisher, times(1)).publishEvent(any(UserEvent.class));
        }

        @Test
        @DisplayName("Bulunmayan kullanıcı için 404 fırlatmalı")
        void shouldThrow404WhenUserNotFound() {
            when(userRepository.findById("ghost")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.deleteUser("ghost"))
                    .isInstanceOf(ResponseStatusException.class);

            verify(userRepository, never()).save(any());
        }
    }

    // ─────────────────────────────────────────────
    // updateUser
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("updateUser")
    class UpdateUser {

        @Test
        @DisplayName("Kullanıcı başarıyla güncellenmeli")
        void shouldUpdateUserSuccessfully() {
            UpdateUserRequest updateRequest = new UpdateUserRequest();
            updateRequest.setEmail(JsonNullable.of("newemail@example.com"));
            updateRequest.setRole(JsonNullable.undefined());
            updateRequest.setPassword(JsonNullable.undefined());

            when(userRepository.findById("user-1")).thenReturn(Optional.of(sampleUser));
            when(userRepository.existsByEmailAndIdNot("newemail@example.com", "user-1")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(sampleUser);

            User result = userService.updateUser("user-1", updateRequest);

            assertThat(result).isNotNull();
            verify(userMapper, times(1)).updateEntityFromRequest(eq(updateRequest), eq(sampleUser));
            verify(eventPublisher, times(1)).publishEvent(any(UserEvent.class));
        }

        @Test
        @DisplayName("Alınmış e-posta ile güncelleme yapılmamalı")
        void shouldThrowConflictWhenEmailTaken() {
            UpdateUserRequest updateRequest = new UpdateUserRequest();
            updateRequest.setEmail(JsonNullable.of("taken@example.com"));
            updateRequest.setRole(JsonNullable.undefined());
            updateRequest.setPassword(JsonNullable.undefined());

            when(userRepository.findById("user-1")).thenReturn(Optional.of(sampleUser));
            when(userRepository.existsByEmailAndIdNot("taken@example.com", "user-1")).thenReturn(true);

            assertThatThrownBy(() -> userService.updateUser("user-1", updateRequest))
                    .isInstanceOf(BusinessValidationException.class)
                    .hasMessageContaining("email");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Boş e-posta ile güncelleme yapılmamalı")
        void shouldThrow400WhenEmailIsBlank() {
            UpdateUserRequest updateRequest = new UpdateUserRequest();
            updateRequest.setEmail(JsonNullable.of(""));
            updateRequest.setRole(JsonNullable.undefined());
            updateRequest.setPassword(JsonNullable.undefined());

            when(userRepository.findById("user-1")).thenReturn(Optional.of(sampleUser));

            assertThatThrownBy(() -> userService.updateUser("user-1", updateRequest))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Email cannot be empty");
        }

        @Test
        @DisplayName("Şifre güncellenmek istendiğinde hashlenmeli")
        void shouldHashPasswordOnUpdate() {
            UpdateUserRequest updateRequest = new UpdateUserRequest();
            updateRequest.setEmail(JsonNullable.undefined());
            updateRequest.setRole(JsonNullable.undefined());
            updateRequest.setPassword(JsonNullable.of("newPass123"));

            when(userRepository.findById("user-1")).thenReturn(Optional.of(sampleUser));
            when(passwordEncoder.encode("newPass123")).thenReturn("new_hashed");
            when(userRepository.save(any(User.class))).thenReturn(sampleUser);

            userService.updateUser("user-1", updateRequest);

            assertThat(sampleUser.getPassword()).isEqualTo("new_hashed");
            verify(passwordEncoder, times(1)).encode("newPass123");
        }

        @Test
        @DisplayName("Null role ile güncelleme yapılmamalı")
        void shouldThrow400WhenRoleIsNull() {
            UpdateUserRequest updateRequest = new UpdateUserRequest();
            updateRequest.setEmail(JsonNullable.undefined());
            updateRequest.setRole(JsonNullable.of(null));
            updateRequest.setPassword(JsonNullable.undefined());

            when(userRepository.findById("user-1")).thenReturn(Optional.of(sampleUser));

            assertThatThrownBy(() -> userService.updateUser("user-1", updateRequest))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Role cannot be null");
        }

        @Test
        @DisplayName("Bulunmayan kullanıcı için update 404 fırlatmalı")
        void shouldThrow404WhenUserNotFoundOnUpdate() {
            when(userRepository.findById("ghost")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUser("ghost", new UpdateUserRequest()))
                    .isInstanceOf(ResponseStatusException.class);
        }
    }

    // ─────────────────────────────────────────────
    // getUserByEmail
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("getUserByEmail")
    class GetUserByEmail {

        @Test
        @DisplayName("E-posta ile kullanıcı getirilmeli")
        void shouldReturnUserByEmail() {
            when(userRepository.findByEmail("admin@example.com"))
                    .thenReturn(Optional.of(sampleUser));

            User result = userService.getUserByEmail("admin@example.com");

            assertThat(result.getEmail()).isEqualTo("admin@example.com");
        }

        @Test
        @DisplayName("Bulunamayan e-posta için 404 fırlatmalı")
        void shouldThrow404WhenEmailNotFound() {
            when(userRepository.findByEmail("ghost@example.com"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserByEmail("ghost@example.com"))
                    .isInstanceOf(ResponseStatusException.class);
        }
    }
}
