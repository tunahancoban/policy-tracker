package com.tunahancoban.policy_tracker.model.indexes;

import com.tunahancoban.policy_tracker.model.enums.InstallmentOptions;
import com.tunahancoban.policy_tracker.model.enums.InsuranceCompany;
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
    private Boolean isActive;

    @Field
    private InsuranceCompany company;

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

    @Field(type = FieldType.Keyword)
    private String plateNumber;

    @Field(type = FieldType.Keyword)
    private String chassisNumber;

    @Field(type = FieldType.Keyword)
    private String engineNumber;

    @Field(type = FieldType.Keyword)
    private String vehicleBrand;

    @Field(type = FieldType.Keyword)
    private String vehicleModel;

    @Field(type = FieldType.Integer)
    private Integer modelYear;

    @Field(type = FieldType.Double)
    private BigDecimal vehicleValue;

    @Field(type = FieldType.Keyword)
    private String cascoType;

    @Field(type = FieldType.Boolean)
    private Boolean hasReplacementCar;

    @Field(type = FieldType.Integer)
    private Integer replacementCarDays;

    @Field(type = FieldType.Boolean)
    private Boolean authorizedServiceOnly;

    @Field(type = FieldType.Boolean)
    private Boolean glassExemption;

    @Field(type = FieldType.Keyword)
    private String vehicleUsageType;

    @Field(type = FieldType.Integer)
    private Integer noClaimDiscountStep;

    @Field(type = FieldType.Boolean)
    private Boolean hasImm;

    @Field(type = FieldType.Double)
    private BigDecimal immLimit;

    // ==========================================
    // 2. HOUSE AND DASK
    // ==========================================

    @Field(type = FieldType.Keyword)
    private String uavtCode; // 10 haneli UAVT adres kodu

    @Field(type = FieldType.Keyword)
    private String residenceType; // Ev Sahibi / Kiracı

    @Field(type = FieldType.Integer)
    private Integer grossSquareMeters;

    @Field(type = FieldType.Double)
    private BigDecimal buildingCoverageLimit; // Bina teminat bedeli

    @Field(type = FieldType.Double)
    private BigDecimal contentsCoverageLimit; // Eşya teminat bedeli

    @Field(type = FieldType.Boolean)
    private Boolean theftCoverage; // Hırsızlık

    @Field(type = FieldType.Boolean)
    private Boolean waterDamageCoverage; // Dahili su/su baskını

    @Field(type = FieldType.Boolean)
    private Boolean glassBreakageCoverage; // Cam kırılması

    @Field(type = FieldType.Double)
    private BigDecimal thirdPartyLiabilityLimit; // Komşuluk sorumluluk limiti

    @Field(type = FieldType.Keyword)
    private String buildingConstructionType; // Betonarme, Yığma Kagir vb.

    @Field(type = FieldType.Integer)
    private Integer buildingConstructionYear;

    @Field(type = FieldType.Integer)
    private Integer totalFloorCount;

    @Field(type = FieldType.Integer)
    private Integer apartmentFloor;

    @Field(type = FieldType.Integer)
    private Integer earthquakeZone; // 1-5 arası deprem bölgesi

    // ==========================================
    // 3. HEALTH
    // ==========================================

    @Field(type = FieldType.Keyword)
    private String identityNumber; // TCKN veya Pasaport No

    @Field(type = FieldType.Keyword)
    private String healthPlanType; // TSS / ÖSS

    @Field(type = FieldType.Keyword)
    private String coverageScope;

    @Field(type = FieldType.Date, format = DateFormat.date, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @Field(type = FieldType.Keyword)
    private String gender; // Erkek / Kadın

    @Field(type = FieldType.Integer)
    private Integer outpatientLimitCount; // Yıllık ayakta muayene adedi (örn: 8, 10)

    @Field(type = FieldType.Keyword)
    private String networkTier; // Network A, Network B vb.

    @Field(type = FieldType.Boolean)
    private Boolean maternityCoverage; // Doğum teminatı
}