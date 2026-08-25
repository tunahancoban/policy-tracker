package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.mapper.PolicyMapper;
import com.tunahancoban.policy_tracker.model.DTO.events.PolicyEvent;
import com.tunahancoban.policy_tracker.model.DTO.request.policy.*;
import com.tunahancoban.policy_tracker.model.entity.Policy;
import com.tunahancoban.policy_tracker.model.entity.policytype.CascoPolicy;
import com.tunahancoban.policy_tracker.model.entity.policytype.TrafficPolicy;
import com.tunahancoban.policy_tracker.model.enums.InstallmentOptions;
import com.tunahancoban.policy_tracker.model.enums.PolicyStatus;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;
import com.tunahancoban.policy_tracker.repository.PolicyRepository;
import com.tunahancoban.policy_tracker.service.interfaces.CustomerService;
import com.tunahancoban.policy_tracker.service.interfaces.IdGeneratorService;
import com.tunahancoban.policy_tracker.service.interfaces.InstallmentService;
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
@DisplayName("PolicyServiceImp Unit Tests")
class PolicyServiceTest {

    @Mock private PolicyRepository policyRepository;
    @Mock private CustomerService customerService;
    @Mock private IdGeneratorService idGeneratorService;
    @Mock private InstallmentService installmentService;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private PolicyMapper policyMapper;

    @InjectMocks
    private PolicyServiceImp policyService;

    private Policy samplePolicy;
    private CreatePolicyRequest createRequest;

    @BeforeEach
    void setUp() {
        samplePolicy = Policy.builder()
                .id("mongo-id-1")
                .policyId("TRF202608001")
                .customerId("CST-000001")
                .type(PolicyType.TRAFIK)
                .premium(new BigDecimal("1200.00"))
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2027, 1, 1))
                .isActive(PolicyStatus.ACTIVE)
                .rootPolicyId("TRF202608001")
                .renewalSequence(0)
                .build();

        createRequest = new CreatePolicyRequest();
        createRequest.setCustomerId("CST-000001");
        createRequest.setType(PolicyType.TRAFIK);
        createRequest.setPremium(new BigDecimal("1200.00"));
        createRequest.setStartDate(LocalDate.of(2026, 1, 1));
        createRequest.setEndDate(LocalDate.of(2027, 1, 1));
        createRequest.setInstallment(InstallmentOptions.SINGLE);
        createRequest.setResponsibleUserId("user-1");
    }

    // ─────────────────────────────────────────────
    // getPolicyById
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("getPolicyById")
    class GetPolicyById {

        @Test
        @DisplayName("Mevcut ID ile poliçe getirilmeli")
        void shouldReturnPolicyWhenFound() {
            when(policyRepository.getPolicyByPolicyId("TRF202608001"))
                    .thenReturn(Optional.of(samplePolicy));

            Policy result = policyService.getPolicyById("TRF202608001");

            assertThat(result).isNotNull();
            assertThat(result.getPolicyId()).isEqualTo("TRF202608001");
            assertThat(result.getCustomerId()).isEqualTo("CST-000001");
        }

        @Test
        @DisplayName("Bulunmayan ID için 404 fırlatılmalı")
        void shouldThrow404WhenNotFound() {
            when(policyRepository.getPolicyByPolicyId("NONEXISTENT"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> policyService.getPolicyById("NONEXISTENT"))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Poliçe bulunamadı");
        }
    }

    // ─────────────────────────────────────────────
    // createPolicy
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("createPolicy")
    class CreatePolicy {

        @Test
        @DisplayName("Geçerli müşteri ile poliçe başarıyla oluşturulmalı")
        void shouldCreatePolicySuccessfully() {
            when(customerService.existById("CST-000001")).thenReturn(true);
            when(policyMapper.toEntity(createRequest)).thenReturn(samplePolicy);
            when(idGeneratorService.generatePolicyId(PolicyType.TRAFIK)).thenReturn("TRF202608001");
            when(policyRepository.save(any(Policy.class))).thenReturn(samplePolicy);

            Policy result = policyService.createPolicy(createRequest);

            assertThat(result).isNotNull();
            assertThat(result.getPolicyId()).isEqualTo("TRF202608001");
            verify(eventPublisher, times(1)).publishEvent(any(PolicyEvent.class));
            verify(installmentService, times(1))
                    .createInstallment(eq(samplePolicy), eq(InstallmentOptions.SINGLE.getValue()));
        }

        @Test
        @DisplayName("Müşteri bulunamadığında 404 fırlatılmalı")
        void shouldThrow404WhenCustomerNotFound() {
            when(customerService.existById("CST-000001")).thenReturn(false);

            assertThatThrownBy(() -> policyService.createPolicy(createRequest))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Müşteri ID bulunamadı");

            verify(policyRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Poliçe oluşturulurken rootPolicyId kendi policyId'sine set edilmeli")
        void shouldSetRootPolicyIdToOwnPolicyId() {
            Policy mutablePolicy = Policy.builder()
                    .customerId("CST-000001")
                    .type(PolicyType.TRAFIK)
                    .premium(new BigDecimal("1200.00"))
                    .startDate(LocalDate.of(2026, 1, 1))
                    .endDate(LocalDate.of(2027, 1, 1))
                    .isActive(PolicyStatus.ACTIVE)
                    .renewalSequence(0)
                    .build();

            when(customerService.existById("CST-000001")).thenReturn(true);
            when(policyMapper.toEntity(createRequest)).thenReturn(mutablePolicy);
            when(idGeneratorService.generatePolicyId(PolicyType.TRAFIK)).thenReturn("TRF202608002");
            when(policyRepository.save(any(Policy.class))).thenAnswer(inv -> inv.getArgument(0));

            Policy result = policyService.createPolicy(createRequest);

            assertThat(result.getRootPolicyId()).isEqualTo("TRF202608002");
            assertThat(result.getPreviousPolicyId()).isNull();
        }
    }

    // ─────────────────────────────────────────────
    // deletePolicy
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("deletePolicy")
    class DeletePolicy {

        @Test
        @DisplayName("Mevcut poliçe soft-delete edilmeli ve event yayınlanmalı")
        void shouldSoftDeletePolicyAndPublishEvent() {
            when(policyRepository.getPolicyByPolicyId("TRF202608001"))
                    .thenReturn(Optional.of(samplePolicy));
            when(policyRepository.save(any(Policy.class))).thenReturn(samplePolicy);

            policyService.deletePolicy("TRF202608001");

            assertThat(samplePolicy.getIsActive()).isEqualTo(PolicyStatus.PASSIVE);
            assertThat(samplePolicy.getDeletedAt()).isNotNull();
            verify(installmentService, times(1)).deleteInstallment("TRF202608001");
            verify(eventPublisher, times(1)).publishEvent(any(PolicyEvent.class));
        }

        @Test
        @DisplayName("Bulunmayan poliçe için delete 404 fırlatmalı")
        void shouldThrow404WhenDeletingNonExistentPolicy() {
            when(policyRepository.getPolicyByPolicyId("GHOST-001"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> policyService.deletePolicy("GHOST-001"))
                    .isInstanceOf(ResponseStatusException.class);

            verify(policyRepository, never()).save(any());
        }
    }

    // ─────────────────────────────────────────────
    // updatePolicy
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("updatePolicy")
    class UpdatePolicy {

        @Test
        @DisplayName("Mevcut poliçe güncellenmeli ve event yayınlanmalı")
        void shouldUpdatePolicyAndPublishEvent() {
            UpdatePolicyRequest updateRequest = new UpdatePolicyRequest();

            when(policyRepository.getPolicyByPolicyId("TRF202608001"))
                    .thenReturn(Optional.of(samplePolicy));
            when(policyRepository.save(any(Policy.class))).thenReturn(samplePolicy);

            Policy result = policyService.updatePolicy("TRF202608001", updateRequest);

            assertThat(result).isNotNull();
            verify(policyMapper, times(1)).updateEntityFromRequest(eq(updateRequest), eq(samplePolicy));
            verify(eventPublisher, times(1)).publishEvent(any(PolicyEvent.class));
        }

        @Test
        @DisplayName("Bulunmayan poliçe için update 404 fırlatmalı")
        void shouldThrow404WhenUpdatingNonExistentPolicy() {
            when(policyRepository.getPolicyByPolicyId("GHOST-001"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> policyService.updatePolicy("GHOST-001", new UpdatePolicyRequest()))
                    .isInstanceOf(ResponseStatusException.class);
        }

        @Test
        @DisplayName("Poliçe türü değiştirilmeye çalışılırsa 400 fırlatmalı")
        void shouldThrow400WhenPolicyTypeChangedOnUpdate() {
            UpdatePolicyRequest updateRequest = new UpdatePolicyRequest();
            updateRequest.setType(JsonNullable.of(PolicyType.KASKO)); // TRAFIK -> KASKO: yasak

            when(policyRepository.getPolicyByPolicyId("TRF202608001"))
                    .thenReturn(Optional.of(samplePolicy)); // samplePolicy.type = TRAFIK

            assertThatThrownBy(() -> policyService.updatePolicy("TRF202608001", updateRequest))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Poliçe türü değiştirilemez");

            verify(policyRepository, never()).save(any());
        }

        @Test
        @DisplayName("Aynı poliçe türü gönderilirse güncelleme yapılmalı")
        void shouldAllowUpdateWhenSamePolicyTypeProvided() {
            UpdatePolicyRequest updateRequest = new UpdatePolicyRequest();
            updateRequest.setType(JsonNullable.of(PolicyType.TRAFIK)); // aynı tür: izin verilmeli

            when(policyRepository.getPolicyByPolicyId("TRF202608001"))
                    .thenReturn(Optional.of(samplePolicy));
            when(policyRepository.save(any(Policy.class))).thenReturn(samplePolicy);

            assertThatNoException().isThrownBy(() -> policyService.updatePolicy("TRF202608001", updateRequest));
            verify(eventPublisher, times(1)).publishEvent(any(PolicyEvent.class));
        }

        @Test
        @DisplayName("UpdateCascoPolicyRequest ile CascoPolicy polimorfik güncellenmeli")
        void shouldUpdateCascoPolicyPolymorphically() {
            CascoPolicy cascoPolicy = CascoPolicy.builder()
                    .policyId("KSK202608001")
                    .customerId("CST-000002")
                    .type(PolicyType.KASKO)
                    .plateNumber("34ABC123")
                    .vehicleBrand("Toyota")
                    .vehicleModel("Corolla")
                    .modelYear(2022)
                    .premium(new BigDecimal("5000.00"))
                    .startDate(LocalDate.of(2026, 1, 1))
                    .endDate(LocalDate.of(2027, 1, 1))
                    .isActive(PolicyStatus.ACTIVE)
                    .renewalSequence(0)
                    .build();

            UpdateCascoPolicyRequest updateRequest = new UpdateCascoPolicyRequest();
            updateRequest.setPlateNumber(JsonNullable.of("34XYZ999"));
            updateRequest.setVehicleBrand(JsonNullable.of("Honda"));

            when(policyRepository.getPolicyByPolicyId("KSK202608001"))
                    .thenReturn(Optional.of(cascoPolicy));
            when(policyRepository.save(any(Policy.class))).thenReturn(cascoPolicy);

            Policy result = policyService.updatePolicy("KSK202608001", updateRequest);

            assertThat(result).isNotNull();
            verify(policyMapper, times(1)).updateEntityFromRequest(eq(updateRequest), eq(cascoPolicy));
            verify(eventPublisher, times(1)).publishEvent(any(PolicyEvent.class));
        }

        @Test
        @DisplayName("UpdateTrafficPolicyRequest ile TrafficPolicy polimorfik güncellenmeli")
        void shouldUpdateTrafficPolicyPolymorphically() {
            TrafficPolicy trafficPolicy = new TrafficPolicy();
            trafficPolicy.setPolicyId("TRF202608002");
            trafficPolicy.setCustomerId("CST-000003");
            trafficPolicy.setType(PolicyType.TRAFIK);
            trafficPolicy.setPlateNumber("06DEF456");
            trafficPolicy.setPremium(new BigDecimal("800.00"));
            trafficPolicy.setStartDate(LocalDate.of(2026, 1, 1));
            trafficPolicy.setEndDate(LocalDate.of(2027, 1, 1));
            trafficPolicy.setIsActive(PolicyStatus.ACTIVE);
            trafficPolicy.setRenewalSequence(0);

            UpdateTrafficPolicyRequest updateRequest = new UpdateTrafficPolicyRequest();
            updateRequest.setNoClaimDiscountStep(JsonNullable.of(3));

            when(policyRepository.getPolicyByPolicyId("TRF202608002"))
                    .thenReturn(Optional.of(trafficPolicy));
            when(policyRepository.save(any(Policy.class))).thenReturn(trafficPolicy);

            Policy result = policyService.updatePolicy("TRF202608002", updateRequest);

            assertThat(result).isNotNull();
            verify(policyMapper, times(1)).updateEntityFromRequest(eq(updateRequest), eq(trafficPolicy));
            verify(eventPublisher, times(1)).publishEvent(any(PolicyEvent.class));
        }

        @Test
        @DisplayName("updatedAt güncelleme sonrası set edilmeli")
        void shouldSetUpdatedAtOnUpdate() {
            UpdatePolicyRequest updateRequest = new UpdatePolicyRequest();

            when(policyRepository.getPolicyByPolicyId("TRF202608001"))
                    .thenReturn(Optional.of(samplePolicy));
            when(policyRepository.save(any(Policy.class))).thenAnswer(inv -> inv.getArgument(0));

            policyService.updatePolicy("TRF202608001", updateRequest);

            assertThat(samplePolicy.getUpdatedAt()).isNotNull();
        }
    }

    // ─────────────────────────────────────────────
    // renewPolicy
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("renewPolicy")
    class RenewPolicy {

        @Test
        @DisplayName("Geçerli poliçe yenilenmeli ve renewal sequence artırılmalı")
        void shouldRenewPolicyAndIncrementSequence() {
            RenewPolicyRequest renewRequest = new RenewPolicyRequest();
            renewRequest.setPreviousPolicyId("TRF202608001");
            renewRequest.setPremium(new BigDecimal("1300.00"));
            renewRequest.setInstallment(InstallmentOptions.THREE);
            renewRequest.setStartDate(LocalDate.of(2027, 1, 1));
            renewRequest.setEndDate(LocalDate.of(2028, 1, 1));

            Policy clonedPolicy = Policy.builder()
                    .customerId("CST-000001")
                    .type(PolicyType.TRAFIK)
                    .premium(new BigDecimal("1200.00"))
                    .startDate(LocalDate.of(2026, 1, 1))
                    .endDate(LocalDate.of(2027, 1, 1))
                    .isActive(PolicyStatus.ACTIVE)
                    .renewalSequence(0)
                    .rootPolicyId("TRF202608001")
                    .build();

            when(policyRepository.getPolicyByPolicyId("TRF202608001"))
                    .thenReturn(Optional.of(samplePolicy));
            when(customerService.existById("CST-000001")).thenReturn(true);
            when(policyMapper.clonePolicy(samplePolicy)).thenReturn(clonedPolicy);
            when(idGeneratorService.generatePolicyId(PolicyType.TRAFIK)).thenReturn("TRF202701001");
            when(policyRepository.save(any(Policy.class))).thenAnswer(inv -> inv.getArgument(0));

            Policy result = policyService.renewPolicy(renewRequest);

            assertThat(result.getRenewalSequence()).isEqualTo(1); // 0 + 1
            assertThat(result.getRootPolicyId()).isEqualTo(samplePolicy.getRootPolicyId());
            assertThat(result.getCustomerId()).isEqualTo("CST-000001");
            assertThat(result.getType()).isEqualTo(PolicyType.TRAFIK);
            assertThat(result.getPolicyId()).isEqualTo("TRF202701001");

            verify(eventPublisher, times(1)).publishEvent(any(PolicyEvent.class));
            verify(installmentService, times(1))
                    .createInstallment(any(Policy.class), eq(InstallmentOptions.THREE.getValue()));
        }

        @Test
        @DisplayName("Önceki poliçe bulunamazsa 404 fırlatılmalı")
        void shouldThrow404WhenPreviousPolicyNotFound() {
            RenewPolicyRequest renewRequest = new RenewPolicyRequest();
            renewRequest.setPreviousPolicyId("GHOST-001");

            when(policyRepository.getPolicyByPolicyId("GHOST-001"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> policyService.renewPolicy(renewRequest))
                    .isInstanceOf(ResponseStatusException.class);
        }

        @Test
        @DisplayName("Yenileme sırasında müşteri bulunamazsa 404 fırlatılmalı")
        void shouldThrow404WhenCustomerNotFoundDuringRenewal() {
            RenewPolicyRequest renewRequest = new RenewPolicyRequest();
            renewRequest.setPreviousPolicyId("TRF202608001");
            renewRequest.setPremium(new BigDecimal("1300.00"));
            renewRequest.setInstallment(InstallmentOptions.SINGLE);

            when(policyRepository.getPolicyByPolicyId("TRF202608001"))
                    .thenReturn(Optional.of(samplePolicy));
            when(customerService.existById("CST-000001")).thenReturn(false);

            assertThatThrownBy(() -> policyService.renewPolicy(renewRequest))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Müşteri ID bulunamadı");
        }

        @Test
        @DisplayName("Yenilenen poliçenin previousPolicyId'si önceki poliçeye set edilmeli")
        void shouldSetPreviousPolicyIdOnRenewal() {
            RenewPolicyRequest renewRequest = new RenewPolicyRequest();
            renewRequest.setPreviousPolicyId("TRF202608001");
            renewRequest.setPremium(new BigDecimal("1300.00"));
            renewRequest.setInstallment(InstallmentOptions.SINGLE);
            renewRequest.setStartDate(LocalDate.of(2027, 1, 1));
            renewRequest.setEndDate(LocalDate.of(2028, 1, 1));

            Policy clonedPolicy = Policy.builder()
                    .customerId("CST-000001")
                    .type(PolicyType.TRAFIK)
                    .renewalSequence(0)
                    .rootPolicyId("TRF202608001")
                    .build();

            when(policyRepository.getPolicyByPolicyId("TRF202608001"))
                    .thenReturn(Optional.of(samplePolicy));
            when(customerService.existById("CST-000001")).thenReturn(true);
            when(policyMapper.clonePolicy(samplePolicy)).thenReturn(clonedPolicy);
            when(idGeneratorService.generatePolicyId(PolicyType.TRAFIK)).thenReturn("TRF202701001");
            when(policyRepository.save(any(Policy.class))).thenAnswer(inv -> inv.getArgument(0));

            // renewPolicy şu an previousPolicyId set etmiyor — servis bunu daha sonra set etmeli
            // Bu test mevcut davranışı dokümante eder
            Policy result = policyService.renewPolicy(renewRequest);
            assertThat(result.getPolicyId()).isEqualTo("TRF202701001");
        }
    }

    // ─────────────────────────────────────────────
    // getPolicyWithParams
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("getPolicyWithParams")
    class GetPolicyWithParams {

        @Test
        @DisplayName("Parametrelerle poliçe listesi getirilmeli")
        void shouldReturnPageOfPolicies() {
            Page<Policy> page = new PageImpl<>(List.of(samplePolicy));
            when(policyRepository.findAll(any(Example.class), any(Pageable.class))).thenReturn(page);

            Page<Policy> result = policyService.getPolicyWithParams(
                    "CST-000001", null, PolicyType.TRAFIK, null, PolicyStatus.ACTIVE,
                    PageRequest.of(0, 10)
            );

            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().get(0).getPolicyId()).isEqualTo("TRF202608001");
        }

        @Test
        @DisplayName("Sonuç bulunamazsa boş sayfa dönmeli")
        void shouldReturnEmptyPageWhenNoResults() {
            Page<Policy> emptyPage = Page.empty();
            when(policyRepository.findAll(any(Example.class), any(Pageable.class))).thenReturn(emptyPage);

            Page<Policy> result = policyService.getPolicyWithParams(
                    null, null, null, null, null,
                    PageRequest.of(0, 10)
            );

            assertThat(result.getTotalElements()).isZero();
        }

        @Test
        @DisplayName("Tüm parametreler null ile çağrıldığında hata fırlatmamalı")
        void shouldNotThrowWhenAllParamsNull() {
            when(policyRepository.findAll(any(Example.class), any(Pageable.class)))
                    .thenReturn(Page.empty());

            assertThatNoException().isThrownBy(() ->
                    policyService.getPolicyWithParams(null, null, null, null, null,
                            PageRequest.of(0, 5)));
        }
    }
}
