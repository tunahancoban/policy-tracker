package com.tunahancoban.policy_tracker.model.indexes;

import com.tunahancoban.policy_tracker.model.enums.InstallmentOptions;
import com.tunahancoban.policy_tracker.model.enums.PolicyStatus;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Document(indexName = "policies")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PolicyIndex {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String policyId;

    @Field(type = FieldType.Keyword)
    private String customerId;

    @Field(type = FieldType.Keyword)
    private InstallmentOptions installment;

    @Field(type = FieldType.Keyword)
    private PolicyStatus isActive;

    @Field(type = FieldType.Keyword)
    private PolicyType type;

    @Field(type = FieldType.Date, format = DateFormat.date, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Field(type = FieldType.Date, format = DateFormat.date, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Field(type = FieldType.Scaled_Float, scalingFactor = 100)
    private BigDecimal premium;

    @Field(type = FieldType.Keyword)
    private String responsibleUserId;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String note;

    @Field(type = FieldType.Keyword)
    private String previousPolicyId;

    @Field(type = FieldType.Keyword)
    private String rootPolicyId;

    @Field(type = FieldType.Integer)
    private Integer renewalSequence;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime updatedAt;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime deletedAt;

    @Field(type = FieldType.Integer)
    private Set<Integer> notifiedThresholds;

    // ==========================================
    // 1. TRAFFIC AND CASCO
    // ==========================================

    @Field(type = FieldType.Text, analyzer = "standard")
    private String plateNumber;

    @Field(type = FieldType.Keyword)
    private String chassisNumber;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String vehicleBrand;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String vehicleModel;

    @Field(type = FieldType.Integer)
    private Integer modelYear;

    // ==========================================
    // 2.HOUSE AND DASK
    // ==========================================

    @Field(type = FieldType.Keyword)
    private String uavtCode; // 10 haneli UAVT adres kodu

    @Field(type = FieldType.Keyword)
    private String residenceType; // Ev Sahibi / Kiracı

    @Field(type = FieldType.Integer)
    private Integer grossSquareMeters;

    // ==========================================
    // 3. HEALTH
    // ==========================================

    @Field(type = FieldType.Keyword)
    private String identityNumber; // TCKN veya Pasaport No

    @Field(type = FieldType.Keyword)
    private String healthPlanType; // TSS / ÖSS

    @Field(type = FieldType.Keyword)
    private String coverageScope; // Yatarak / Ayakta+Yatarak
}