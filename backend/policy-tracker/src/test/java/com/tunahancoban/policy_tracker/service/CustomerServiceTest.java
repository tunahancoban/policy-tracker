package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.mapper.CustomerMapper;
import com.tunahancoban.policy_tracker.model.DTO.events.CustomerEvent;
import com.tunahancoban.policy_tracker.model.DTO.request.customer.CreateCustomerRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.customer.UpdateCustomerRequest;
import com.tunahancoban.policy_tracker.model.entity.Customer;
import com.tunahancoban.policy_tracker.model.exceptions.BusinessValidationException;
import com.tunahancoban.policy_tracker.repository.CustomerRepository;
import com.tunahancoban.policy_tracker.service.interfaces.IdGeneratorService;
import org.openapitools.jackson.nullable.JsonNullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerServiceImp Unit Tests")
class CustomerServiceTest {

    @Mock private CustomerRepository customerRepository;
    @Mock private IdGeneratorService idGeneratorService;
    @Mock private CustomerMapper customerMapper;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CustomerServiceImp customerService;

    private Customer sampleCustomer;
    private CreateCustomerRequest createRequest;

    @BeforeEach
    void setUp() {
        sampleCustomer = Customer.builder()
                .id("mongo-id-1")
                .customerId("CST-000001")
                .fullName("Ali Veli")
                .identityNumber("12345678901")
                .email("ali@example.com")
                .phoneNumber("05551234567")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createRequest = new CreateCustomerRequest();
        createRequest.setFullName("Ali Veli");
        createRequest.setIdentityNumber("12345678901");
        createRequest.setEmail("ali@example.com");
        createRequest.setPhoneNumber("05551234567");
    }

    // ─────────────────────────────────────────────
    // getCustomerByCustomerId
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("getCustomerByCustomerId")
    class GetCustomerById {

        @Test
        @DisplayName("Mevcut müşteri ID ile müşteri getirilmeli")
        void shouldReturnCustomerWhenFound() {
            when(customerRepository.findByCustomerId("CST-000001"))
                    .thenReturn(Optional.of(sampleCustomer));

            Customer result = customerService.getCustomerByCustomerId("CST-000001");

            assertThat(result).isNotNull();
            assertThat(result.getCustomerId()).isEqualTo("CST-000001");
            assertThat(result.getFullName()).isEqualTo("Ali Veli");
        }

        @Test
        @DisplayName("Bulunmayan ID için 404 fırlatılmalı")
        void shouldThrow404WhenNotFound() {
            when(customerRepository.findByCustomerId("GHOST"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerService.getCustomerByCustomerId("GHOST"))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Customer not found");
        }
    }

    // ─────────────────────────────────────────────
    // createCustomer
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("createCustomer")
    class CreateCustomer {

        @Test
        @DisplayName("Yeni müşteri başarıyla oluşturulmalı")
        void shouldCreateCustomerSuccessfully() {
            when(customerRepository.existsByIdentityNumber("12345678901")).thenReturn(false);
            when(customerMapper.toEntity(createRequest)).thenReturn(sampleCustomer);
            when(idGeneratorService.generateCustomerId()).thenReturn("CST-000001");
            when(customerRepository.save(any(Customer.class))).thenReturn(sampleCustomer);

            Customer result = customerService.createCustomer(createRequest);

            assertThat(result).isNotNull();
            assertThat(result.getCustomerId()).isEqualTo("CST-000001");
            verify(eventPublisher, times(1)).publishEvent(any(CustomerEvent.class));
        }

        @Test
        @DisplayName("Aynı TC kimlik numarası ile tekrar kayıt yapılmamalı")
        void shouldThrowConflictWhenIdentityNumberAlreadyExists() {
            when(customerRepository.existsByIdentityNumber("12345678901")).thenReturn(true);

            assertThatThrownBy(() -> customerService.createCustomer(createRequest))
                    .isInstanceOf(BusinessValidationException.class)
                    .hasMessageContaining("T.C. Kimlik");

            verify(customerRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Müşteri oluşturulurken createdAt ve updatedAt set edilmeli")
        void shouldSetTimestampsOnCreate() {
            Customer mutableCustomer = Customer.builder()
                    .fullName("Ali Veli")
                    .identityNumber("12345678901")
                    .build();

            when(customerRepository.existsByIdentityNumber("12345678901")).thenReturn(false);
            when(customerMapper.toEntity(createRequest)).thenReturn(mutableCustomer);
            when(idGeneratorService.generateCustomerId()).thenReturn("CST-000001");
            when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

            Customer result = customerService.createCustomer(createRequest);

            assertThat(result.getCreatedAt()).isNotNull();
            assertThat(result.getUpdatedAt()).isNotNull();
        }
    }

    // ─────────────────────────────────────────────
    // updateCustomer
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("updateCustomer")
    class UpdateCustomer {

        @Test
        @DisplayName("Mevcut müşteri başarıyla güncellenmeli (TC çakışması yoksa)")
        void shouldUpdateCustomerSuccessfully() {
            UpdateCustomerRequest updateRequest = new UpdateCustomerRequest();

            when(customerRepository.findByCustomerId("CST-000001"))
                    .thenReturn(Optional.of(sampleCustomer));
            when(customerRepository.save(any(Customer.class))).thenReturn(sampleCustomer);

            Customer result = customerService.updateCustomer("CST-000001", updateRequest);

            assertThat(result).isNotNull();
            verify(customerMapper, times(1)).updateEntityFromRequest(eq(updateRequest), eq(sampleCustomer));
            verify(eventPublisher, times(1)).publishEvent(any(CustomerEvent.class));
        }

        @Test
        @DisplayName("Başka müşterinin TC'si ile güncelleme yapılmamalı")
        void shouldThrowConflictWhenIdentityNumberTakenByAnotherCustomer() {
            UpdateCustomerRequest updateRequest = new UpdateCustomerRequest();
            updateRequest.setIdentityNumber(JsonNullable.of("99999999999"));

            when(customerRepository.findByCustomerId("CST-000001"))
                    .thenReturn(Optional.of(sampleCustomer));
            // TC numarası başka müşteriye ait (çakışma)
            when(customerRepository.existsByIdentityNumber("99999999999")).thenReturn(true);

            assertThatThrownBy(() -> customerService.updateCustomer("CST-000001", updateRequest))
                    .isInstanceOf(BusinessValidationException.class)
                    .hasMessageContaining("T.C. Kimlik");

            verify(customerRepository, never()).save(any());
        }

        @Test
        @DisplayName("Bulunmayan müşteri için update 404 fırlatmalı")
        void shouldThrow404WhenUpdatingNonExistentCustomer() {
            when(customerRepository.findByCustomerId("GHOST"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerService.updateCustomer("GHOST", new UpdateCustomerRequest()))
                    .isInstanceOf(ResponseStatusException.class);
        }

        @Test
        @DisplayName("Güncelleme sonrası updatedAt set edilmeli")
        void shouldSetUpdatedAtOnUpdate() {
            UpdateCustomerRequest updateRequest = new UpdateCustomerRequest();
            Customer mutableCustomer = Customer.builder()
                    .customerId("CST-000001")
                    .identityNumber("12345678901")
                    .build();

            when(customerRepository.findByCustomerId("CST-000001"))
                    .thenReturn(Optional.of(mutableCustomer));
            when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

            Customer result = customerService.updateCustomer("CST-000001", updateRequest);

            assertThat(result.getUpdatedAt()).isNotNull();
        }
    }

    // ─────────────────────────────────────────────
    // deleteCustomer
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("deleteCustomer")
    class DeleteCustomer {

        @Test
        @DisplayName("Müşteri soft-delete edilmeli")
        void shouldSoftDeleteCustomer() {
            when(customerRepository.findByCustomerId("CST-000001"))
                    .thenReturn(Optional.of(sampleCustomer));
            when(customerRepository.save(any(Customer.class))).thenReturn(sampleCustomer);

            customerService.deleteCustomer("CST-000001");

            assertThat(sampleCustomer.getIsActive()).isFalse();
            assertThat(sampleCustomer.getDeletedAt()).isNotNull();
            verify(eventPublisher, times(1)).publishEvent(any(CustomerEvent.class));
        }

        @Test
        @DisplayName("Bulunmayan müşteri için delete 404 fırlatmalı")
        void shouldThrow404WhenDeletingNonExistentCustomer() {
            when(customerRepository.findByCustomerId("GHOST"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerService.deleteCustomer("GHOST"))
                    .isInstanceOf(ResponseStatusException.class);

            verify(customerRepository, never()).save(any());
        }

        @Test
        @DisplayName("Delete event yayınlanmalı")
        void shouldPublishDeleteEventOnCustomerDelete() {
            when(customerRepository.findByCustomerId("CST-000001"))
                    .thenReturn(Optional.of(sampleCustomer));
            when(customerRepository.save(any(Customer.class))).thenReturn(sampleCustomer);

            customerService.deleteCustomer("CST-000001");

            verify(eventPublisher, times(1)).publishEvent(any(CustomerEvent.class));
        }
    }

    // ─────────────────────────────────────────────
    // existById
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("existById")
    class ExistById {

        @Test
        @DisplayName("Mevcut müşteri için true dönmeli")
        void shouldReturnTrueWhenCustomerExists() {
            when(customerRepository.existsByCustomerId("CST-000001")).thenReturn(true);
            assertThat(customerService.existById("CST-000001")).isTrue();
        }

        @Test
        @DisplayName("Mevcut olmayan müşteri için false dönmeli")
        void shouldReturnFalseWhenCustomerNotExists() {
            when(customerRepository.existsByCustomerId("GHOST")).thenReturn(false);
            assertThat(customerService.existById("GHOST")).isFalse();
        }
    }

    // ─────────────────────────────────────────────
    // getCustomerByParam
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("getCustomerByParam")
    class GetCustomerByParam {

        @Test
        @DisplayName("Müşteri listesi parametrelerle getirilmeli")
        void shouldReturnPageOfCustomers() {
            Page<Customer> page = new PageImpl<>(List.of(sampleCustomer));
            when(customerRepository.findAll(any(Example.class), any(Pageable.class))).thenReturn(page);

            Page<Customer> result = customerService.getCustomerByParam(
                    null, "Ali", null, null, null, true,
                    PageRequest.of(0, 10)
            );

            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().get(0).getFullName()).isEqualTo("Ali Veli");
        }

        @Test
        @DisplayName("Sonuç bulunamazsa boş sayfa dönmeli")
        void shouldReturnEmptyPageWhenNoMatch() {
            when(customerRepository.findAll(any(Example.class), any(Pageable.class)))
                    .thenReturn(Page.empty());

            Page<Customer> result = customerService.getCustomerByParam(
                    null, "bilinmeyen", null, null, null, null,
                    PageRequest.of(0, 10)
            );

            assertThat(result.getTotalElements()).isZero();
        }
    }
}
