package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.model.DTO.events.InstallmentEvent;
import com.tunahancoban.policy_tracker.model.entity.Installment;
import com.tunahancoban.policy_tracker.model.entity.Policy;
import com.tunahancoban.policy_tracker.model.enums.PaymentStatus;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;
import com.tunahancoban.policy_tracker.repository.InstallmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InstallmentServiceImp Unit Tests")
class InstallmentServiceTest {

    @Mock private InstallmentRepository installmentRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private InstallmentServiceImp installmentService;

    private Policy samplePolicy;

    @BeforeEach
    void setUp() {
        // Policy @SuperBuilder kullandığından builder() + toBuilder() çalışır
        samplePolicy = Policy.builder()
                .policyId("TRF202608001")
                .customerId("CST-000001")
                .type(PolicyType.TRAFIK)
                .premium(new BigDecimal("1200.00"))
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2027, 1, 1))
                .build();
    }

    // ─────────────────────────────────────────────
    // createInstallment
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("createInstallment")
    class CreateInstallment {

        @Test
        @DisplayName("1 taksit oluşturulduğunda toplam prim tam olarak bir taksitte olmalı")
        void shouldCreateSingleInstallmentWithFullPremium() {
            ArgumentCaptor<List<Installment>> captor = ArgumentCaptor.forClass(List.class);

            installmentService.createInstallment(samplePolicy, 1);

            verify(installmentRepository, times(1)).saveAll(captor.capture());
            List<Installment> saved = captor.getValue();

            assertThat(saved).hasSize(1);
            assertThat(saved.get(0).getAmount()).isEqualByComparingTo(new BigDecimal("1200.00"));
            assertThat(saved.get(0).getInstallmentNo()).isEqualTo(1);
            assertThat(saved.get(0).getStatus()).isEqualTo(PaymentStatus.UNPAID);
            assertThat(saved.get(0).getPolicyId()).isEqualTo("TRF202608001");
        }

        @Test
        @DisplayName("3 taksit oluşturulduğunda toplam tutar doğru bölünmeli")
        void shouldCreateThreeInstallmentsWithCorrectAmounts() {
            ArgumentCaptor<List<Installment>> captor = ArgumentCaptor.forClass(List.class);

            installmentService.createInstallment(samplePolicy, 3);

            verify(installmentRepository, times(1)).saveAll(captor.capture());
            List<Installment> saved = captor.getValue();

            assertThat(saved).hasSize(3);

            BigDecimal total = saved.stream()
                    .map(Installment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            assertThat(total).isEqualByComparingTo(new BigDecimal("1200.00"));

            assertThat(saved.get(0).getInstallmentNo()).isEqualTo(1);
            assertThat(saved.get(1).getInstallmentNo()).isEqualTo(2);
            assertThat(saved.get(2).getInstallmentNo()).isEqualTo(3);
        }

        @Test
        @DisplayName("6 taksit oluşturulduğunda due date'ler aylık artmalı")
        void shouldSetDueDatesMonthlyForSixInstallments() {
            ArgumentCaptor<List<Installment>> captor = ArgumentCaptor.forClass(List.class);
            Policy policy600 = Policy.builder()
                    .policyId("TRF202608001")
                    .customerId("CST-000001")
                    .type(PolicyType.TRAFIK)
                    .premium(new BigDecimal("600.00"))
                    .startDate(LocalDate.of(2026, 1, 1))
                    .endDate(LocalDate.of(2027, 1, 1))
                    .build();

            installmentService.createInstallment(policy600, 6);

            verify(installmentRepository).saveAll(captor.capture());
            List<Installment> saved = captor.getValue();

            assertThat(saved).hasSize(6);
            for (int i = 0; i < 6; i++) {
                assertThat(saved.get(i).getDueDate())
                        .isEqualTo(LocalDate.of(2026, 1, 1).plusMonths(i));
            }
        }

        @Test
        @DisplayName("Taksit oluşturulduğunda her biri için event yayınlanmalı")
        void shouldPublishEventForEachInstallment() {
            installmentService.createInstallment(samplePolicy, 3);

            verify(eventPublisher, times(3)).publishEvent(any(InstallmentEvent.class));
        }

        @Test
        @DisplayName("Bölünemeyen prim kalıntısı ilk taksitte olmalı")
        void shouldAddRemainderToFirstInstallment() {
            // 1000 / 3 = 333.33 → kalıntı 0.01 ilk taksitte olmalı
            Policy policy1000 = Policy.builder()
                    .policyId("TRF202608001")
                    .customerId("CST-000001")
                    .type(PolicyType.TRAFIK)
                    .premium(new BigDecimal("1000.00"))
                    .startDate(LocalDate.of(2026, 1, 1))
                    .endDate(LocalDate.of(2027, 1, 1))
                    .build();
            ArgumentCaptor<List<Installment>> captor = ArgumentCaptor.forClass(List.class);

            installmentService.createInstallment(policy1000, 3);

            verify(installmentRepository).saveAll(captor.capture());
            List<Installment> saved = captor.getValue();

            BigDecimal total = saved.stream()
                    .map(Installment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            assertThat(total).isEqualByComparingTo(new BigDecimal("1000.00"));

            assertThat(saved.get(0).getAmount())
                    .isGreaterThanOrEqualTo(saved.get(1).getAmount());
        }

        @Test
        @DisplayName("Tüm taksitler UNPAID durumunda oluşturulmalı")
        void shouldCreateAllInstallmentsAsUnpaid() {
            ArgumentCaptor<List<Installment>> captor = ArgumentCaptor.forClass(List.class);

            installmentService.createInstallment(samplePolicy, 3);

            verify(installmentRepository).saveAll(captor.capture());
            captor.getValue().forEach(inst ->
                    assertThat(inst.getStatus()).isEqualTo(PaymentStatus.UNPAID));
        }

        @Test
        @DisplayName("Taksitler doğru customerId ile oluşturulmalı")
        void shouldCreateInstallmentsWithCorrectCustomerId() {
            ArgumentCaptor<List<Installment>> captor = ArgumentCaptor.forClass(List.class);

            installmentService.createInstallment(samplePolicy, 1);

            verify(installmentRepository).saveAll(captor.capture());
            assertThat(captor.getValue().get(0).getCustomerId()).isEqualTo("CST-000001");
        }
    }

    // ─────────────────────────────────────────────
    // updateInstallment
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("updateInstallment")
    class UpdateInstallment {

        @Test
        @DisplayName("Taksit durumu başarıyla güncellenmeli")
        void shouldUpdateInstallmentStatus() {
            Installment installment = Installment.builder()
                    .id("inst-1")
                    .policyId("TRF202608001")
                    .status(PaymentStatus.UNPAID)
                    .build();

            when(installmentRepository.findById("inst-1")).thenReturn(Optional.of(installment));
            when(installmentRepository.save(any(Installment.class))).thenReturn(installment);

            Installment result = installmentService.updateInstallment("inst-1", PaymentStatus.PAID);

            assertThat(result.getStatus()).isEqualTo(PaymentStatus.PAID);
            verify(eventPublisher, times(1)).publishEvent(any(InstallmentEvent.class));
        }

        @Test
        @DisplayName("UNPAID -> CANCELLED güncelleme çalışmalı")
        void shouldUpdateStatusToCancelled() {
            Installment installment = Installment.builder()
                    .id("inst-2")
                    .policyId("TRF202608001")
                    .status(PaymentStatus.UNPAID)
                    .build();

            when(installmentRepository.findById("inst-2")).thenReturn(Optional.of(installment));
            when(installmentRepository.save(any(Installment.class))).thenReturn(installment);

            Installment result = installmentService.updateInstallment("inst-2", PaymentStatus.CANCELLED);

            assertThat(result.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
        }

        @Test
        @DisplayName("Bulunmayan taksit için 404 fırlatmalı")
        void shouldThrow404WhenInstallmentNotFound() {
            when(installmentRepository.findById("ghost")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> installmentService.updateInstallment("ghost", PaymentStatus.PAID))
                    .isInstanceOf(ResponseStatusException.class);
        }
    }

    // ─────────────────────────────────────────────
    // deleteInstallment
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("deleteInstallment")
    class DeleteInstallment {

        @Test
        @DisplayName("UNPAID taksitler CANCELLED yapılmalı, PAID olanlar değişmemeli")
        void shouldCancelUnpaidInstallments() {
            Installment unpaid1 = Installment.builder().id("i1").status(PaymentStatus.UNPAID).build();
            Installment paid1 = Installment.builder().id("i2").status(PaymentStatus.PAID).build();

            when(installmentRepository.findAllByPolicyId("TRF202608001"))
                    .thenReturn(List.of(unpaid1, paid1));

            installmentService.deleteInstallment("TRF202608001");

            assertThat(unpaid1.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
            assertThat(paid1.getStatus()).isEqualTo(PaymentStatus.PAID);
            verify(installmentRepository, times(1)).saveAll(anyList());
        }

        @Test
        @DisplayName("Taksit bulunamazsa 404 fırlatılmalı")
        void shouldThrow404WhenNoInstallmentsFound() {
            when(installmentRepository.findAllByPolicyId("GHOST-001"))
                    .thenReturn(List.of());

            assertThatThrownBy(() -> installmentService.deleteInstallment("GHOST-001"))
                    .isInstanceOf(ResponseStatusException.class);

            verify(installmentRepository, never()).saveAll(any());
        }

        @Test
        @DisplayName("Her taksit için event yayınlanmalı")
        void shouldPublishEventForEachInstallmentOnDelete() {
            List<Installment> installments = List.of(
                    Installment.builder().id("i1").status(PaymentStatus.UNPAID).build(),
                    Installment.builder().id("i2").status(PaymentStatus.UNPAID).build()
            );
            when(installmentRepository.findAllByPolicyId("TRF202608001")).thenReturn(installments);

            installmentService.deleteInstallment("TRF202608001");

            verify(eventPublisher, times(2)).publishEvent(any(InstallmentEvent.class));
        }

        @Test
        @DisplayName("Tüm taksitler CANCELLED ise de event yayınlanmalı")
        void shouldPublishEventsEvenWhenAllAlreadyCancelled() {
            List<Installment> installments = List.of(
                    Installment.builder().id("i1").status(PaymentStatus.CANCELLED).build()
            );
            when(installmentRepository.findAllByPolicyId("TRF202608001")).thenReturn(installments);

            installmentService.deleteInstallment("TRF202608001");

            verify(eventPublisher, times(1)).publishEvent(any(InstallmentEvent.class));
        }
    }

    // ─────────────────────────────────────────────
    // getInstallment
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("getInstallment")
    class GetInstallment {

        @Test
        @DisplayName("policyId ile taksitler sayfalı getirilmeli")
        void shouldReturnPageOfInstallmentsByPolicyId() {
            Installment inst = Installment.builder()
                    .policyId("TRF202608001")
                    .customerId("CST-000001")
                    .installmentNo(1)
                    .amount(new BigDecimal("400.00"))
                    .status(PaymentStatus.UNPAID)
                    .build();

            Page<Installment> page = new PageImpl<>(List.of(inst));
            when(installmentRepository.findAll(any(Example.class), any(Pageable.class))).thenReturn(page);

            Page<Installment> result = installmentService.getInstallment(
                    "CST-000001", "TRF202608001", PageRequest.of(0, 10)
            );

            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().get(0).getPolicyId()).isEqualTo("TRF202608001");
        }

        @Test
        @DisplayName("Sonuç bulunamazsa boş sayfa dönmeli")
        void shouldReturnEmptyPageWhenNoInstallments() {
            when(installmentRepository.findAll(any(Example.class), any(Pageable.class)))
                    .thenReturn(Page.empty());

            Page<Installment> result = installmentService.getInstallment(
                    null, "GHOST", PageRequest.of(0, 10)
            );

            assertThat(result.getTotalElements()).isZero();
        }
    }
}
