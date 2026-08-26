import { formatDate } from '../utils/dateHelper';

// ─── Base Policy ─────────────────────────────────────────────────────────────

export interface CreatePolicyRequest {
  type: string;
  startDate: string;
  endDate: string;
  premium: number;
  customerId: string;
  isActive: string;
  responsibleUserId: string;
  note: string;
  installment: number;
  company: string;
  // Type-specific fields (optional — sent only when relevant)
  // TRAFIK / KASKO
  plateNumber?: string;
  chassisNumber?: string;
  engineNumber?: string;
  vehicleUsageType?: string;
  noClaimDiscountStep?: number;
  hasImm?: boolean;
  immLimit?: number;
  // KASKO-only
  vehicleBrand?: string;
  vehicleModel?: string;
  modelYear?: number;
  vehicleValue?: number;
  cascoType?: string;
  hasReplacementCar?: boolean;
  replacementCarDays?: number;
  authorizedServiceOnly?: boolean;
  glassExemption?: boolean;
  // DASK / KONUT
  uavtCode?: string;
  grossSquareMeters?: number;
  buildingConstructionType?: string;
  buildingConstructionYear?: number;
  totalFloorCount?: number;
  apartmentFloor?: number;
  earthquakeZone?: number;
  // KONUT-only
  residenceType?: string;
  buildingCoverageLimit?: number;
  contentsCoverageLimit?: number;
  theftCoverage?: boolean;
  waterDamageCoverage?: boolean;
  glassBreakageCoverage?: boolean;
  thirdPartyLiabilityLimit?: number;
  // SAGLIK
  identityNumber?: string;
  birthDate?: string;
  gender?: string;
  healthPlanType?: string;
  coverageScope?: string;
  outpatientLimitCount?: number;
  networkTier?: string;
  maternityCoverage?: boolean;
}

export interface UpdatePolicyRequest {
  type?: string;
  startDate?: string;
  endDate?: string;
  premium?: number;
  isActive?: string;
  responsibleUserId?: string;
  note?: string;
  company?: string;
  // type-specific (same optional fields as create)
  plateNumber?: string;
  chassisNumber?: string;
  engineNumber?: string;
  vehicleUsageType?: string;
  noClaimDiscountStep?: number;
  hasImm?: boolean;
  immLimit?: number;
  vehicleBrand?: string;
  vehicleModel?: string;
  modelYear?: number;
  vehicleValue?: number;
  cascoType?: string;
  hasReplacementCar?: boolean;
  replacementCarDays?: number;
  authorizedServiceOnly?: boolean;
  glassExemption?: boolean;
  uavtCode?: string;
  grossSquareMeters?: number;
  buildingConstructionType?: string;
  buildingConstructionYear?: number;
  totalFloorCount?: number;
  apartmentFloor?: number;
  earthquakeZone?: number;
  residenceType?: string;
  buildingCoverageLimit?: number;
  contentsCoverageLimit?: number;
  theftCoverage?: boolean;
  waterDamageCoverage?: boolean;
  glassBreakageCoverage?: boolean;
  thirdPartyLiabilityLimit?: number;
  identityNumber?: string;
  birthDate?: string;
  gender?: string;
  healthPlanType?: string;
  coverageScope?: string;
  outpatientLimitCount?: number;
  networkTier?: string;
  maternityCoverage?: boolean;
}

export interface RenewPolicyRequest {
  previousPolicyId: string;
  startDate: string;
  endDate: string;
  premium: number;
  installment: number;
  responsibleUserId: string;
  note?: string;
}

// ─── Policy Entity ────────────────────────────────────────────────────────────

export interface Policy {
  id: string;
  policyId: string;
  customerId: string;
  note: string;
  installment: number;
  type: string;
  startDate: string;
  endDate: string;
  premium: number;
  isActive: string;
  company: string;
  previousPolicyId: string;
  rootPolicyId: string;
  renewalSequence: number;
  responsibleUserId: string;
  createdAt: string;
  updatedAt: string;
  deletedAt: string | null;
  notifiedThresholds: number[];
  // Subtype fields (returned by backend)
  plateNumber?: string;
  chassisNumber?: string;
  engineNumber?: string;
  vehicleUsageType?: string;
  noClaimDiscountStep?: number;
  hasImm?: boolean;
  immLimit?: number;
  vehicleBrand?: string;
  vehicleModel?: string;
  modelYear?: number;
  vehicleValue?: number;
  cascoType?: string;
  hasReplacementCar?: boolean;
  replacementCarDays?: number;
  authorizedServiceOnly?: boolean;
  glassExemption?: boolean;
  uavtCode?: string;
  grossSquareMeters?: number;
  buildingConstructionType?: string;
  buildingConstructionYear?: number;
  totalFloorCount?: number;
  apartmentFloor?: number;
  earthquakeZone?: number;
  residenceType?: string;
  buildingCoverageLimit?: number;
  contentsCoverageLimit?: number;
  theftCoverage?: boolean;
  waterDamageCoverage?: boolean;
  glassBreakageCoverage?: boolean;
  thirdPartyLiabilityLimit?: number;
  identityNumber?: string;
  birthDate?: string;
  gender?: string;
  healthPlanType?: string;
  coverageScope?: string;
  outpatientLimitCount?: number;
  networkTier?: string;
  maternityCoverage?: boolean;
}

// ─── PolicyForm (base fields shown in modal always) ──────────────────────────

export interface PolicyForm {
  customerId: string;
  type: string;
  note: string;
  installment: number;
  premium: number;
  startDate: string;
  endDate: string;
  responsibleUserId: string;
  isActive: string;
  company: string;
}

// ─── Type-Specific Field Forms ────────────────────────────────────────────────

export interface TrafficFields {
  plateNumber: string;
  chassisNumber: string;
  engineNumber: string;
  vehicleUsageType: string;
  noClaimDiscountStep: number | null;
  hasImm: boolean;
  immLimit: number | null;
}

export interface CascoFields {
  plateNumber: string;
  chassisNumber: string;
  vehicleBrand: string;
  vehicleModel: string;
  modelYear: number | null;
  vehicleValue: number | null;
  cascoType: string;
  hasReplacementCar: boolean;
  replacementCarDays: number | null;
  authorizedServiceOnly: boolean;
  glassExemption: boolean;
}

export interface DaskFields {
  uavtCode: string;
  grossSquareMeters: number | null;
  buildingConstructionType: string;
  buildingConstructionYear: number | null;
  totalFloorCount: number | null;
  apartmentFloor: number | null;
  earthquakeZone: number | null;
}

export interface HouseFields {
  uavtCode: string;
  residenceType: string;
  buildingCoverageLimit: number | null;
  contentsCoverageLimit: number | null;
  theftCoverage: boolean;
  waterDamageCoverage: boolean;
  glassBreakageCoverage: boolean;
  thirdPartyLiabilityLimit: number | null;
}

export interface HealthFields {
  identityNumber: string;
  birthDate: string;
  gender: string;
  healthPlanType: string;
  coverageScope: string;
  outpatientLimitCount: number | null;
  networkTier: string;
  maternityCoverage: boolean;
}

export type TypeSpecificFields =
  | TrafficFields
  | CascoFields
  | DaskFields
  | HouseFields
  | HealthFields
  | Record<string, never>;

export const initialTrafficFields = (): TrafficFields => ({
  plateNumber: '',
  chassisNumber: '',
  engineNumber: '',
  vehicleUsageType: '',
  noClaimDiscountStep: null,
  hasImm: false,
  immLimit: null,
});

export const initialCascoFields = (): CascoFields => ({
  plateNumber: '',
  chassisNumber: '',
  vehicleBrand: '',
  vehicleModel: '',
  modelYear: null,
  vehicleValue: null,
  cascoType: '',
  hasReplacementCar: false,
  replacementCarDays: null,
  authorizedServiceOnly: false,
  glassExemption: false,
});

export const initialDaskFields = (): DaskFields => ({
  uavtCode: '',
  grossSquareMeters: null,
  buildingConstructionType: '',
  buildingConstructionYear: null,
  totalFloorCount: null,
  apartmentFloor: null,
  earthquakeZone: null,
});

export const initialHouseFields = (): HouseFields => ({
  uavtCode: '',
  residenceType: '',
  buildingCoverageLimit: null,
  contentsCoverageLimit: null,
  theftCoverage: false,
  waterDamageCoverage: false,
  glassBreakageCoverage: false,
  thirdPartyLiabilityLimit: null,
});

export const initialHealthFields = (): HealthFields => ({
  identityNumber: '',
  birthDate: '',
  gender: '',
  healthPlanType: '',
  coverageScope: '',
  outpatientLimitCount: null,
  networkTier: '',
  maternityCoverage: false,
});

export function getInitialTypeFields(type: string): TypeSpecificFields {
  switch (type) {
    case 'TRAFIK': return initialTrafficFields();
    case 'KASKO':  return initialCascoFields();
    case 'DASK':   return initialDaskFields();
    case 'KONUT':  return initialHouseFields();
    case 'SAGLIK': return initialHealthFields();
    default:       return {};
  }
}

export function fillTypeFieldsFromPolicy(policy: Policy): TypeSpecificFields {
  switch (policy.type) {
    case 'TRAFIK': {
      const fields: TrafficFields = {
        plateNumber: policy.plateNumber ?? '',
        chassisNumber: policy.chassisNumber ?? '',
        engineNumber: policy.engineNumber ?? '',
        vehicleUsageType: policy.vehicleUsageType ?? '',
        noClaimDiscountStep: policy.noClaimDiscountStep ?? null,
        hasImm: policy.hasImm ?? false,
        immLimit: policy.immLimit ?? null,
      };
      return fields;
    }
    case 'KASKO': {
      const fields: CascoFields = {
        plateNumber: policy.plateNumber ?? '',
        chassisNumber: policy.chassisNumber ?? '',
        vehicleBrand: policy.vehicleBrand ?? '',
        vehicleModel: policy.vehicleModel ?? '',
        modelYear: policy.modelYear ?? null,
        vehicleValue: policy.vehicleValue ?? null,
        cascoType: policy.cascoType ?? '',
        hasReplacementCar: policy.hasReplacementCar ?? false,
        replacementCarDays: policy.replacementCarDays ?? null,
        authorizedServiceOnly: policy.authorizedServiceOnly ?? false,
        glassExemption: policy.glassExemption ?? false,
      };
      return fields;
    }
    case 'DASK': {
      const fields: DaskFields = {
        uavtCode: policy.uavtCode ?? '',
        grossSquareMeters: policy.grossSquareMeters ?? null,
        buildingConstructionType: policy.buildingConstructionType ?? '',
        buildingConstructionYear: policy.buildingConstructionYear ?? null,
        totalFloorCount: policy.totalFloorCount ?? null,
        apartmentFloor: policy.apartmentFloor ?? null,
        earthquakeZone: policy.earthquakeZone ?? null,
      };
      return fields;
    }
    case 'KONUT': {
      const fields: HouseFields = {
        uavtCode: policy.uavtCode ?? '',
        residenceType: policy.residenceType ?? '',
        buildingCoverageLimit: policy.buildingCoverageLimit ?? null,
        contentsCoverageLimit: policy.contentsCoverageLimit ?? null,
        theftCoverage: policy.theftCoverage ?? false,
        waterDamageCoverage: policy.waterDamageCoverage ?? false,
        glassBreakageCoverage: policy.glassBreakageCoverage ?? false,
        thirdPartyLiabilityLimit: policy.thirdPartyLiabilityLimit ?? null,
      };
      return fields;
    }
    case 'SAGLIK': {
      const fields: HealthFields = {
        identityNumber: policy.identityNumber ?? '',
        birthDate: policy.birthDate ?? '',
        gender: policy.gender ?? '',
        healthPlanType: policy.healthPlanType ?? '',
        coverageScope: policy.coverageScope ?? '',
        outpatientLimitCount: policy.outpatientLimitCount ?? null,
        networkTier: policy.networkTier ?? '',
        maternityCoverage: policy.maternityCoverage ?? false,
      };
      return fields;
    }
    default: return {};
  }
}

// ─── Table / Filter Helpers ───────────────────────────────────────────────────

export const SORT_FIELD_MAP: Record<string, string> = {
  policyId: 'policyId',
  startDate: 'startDate',
  endDate: 'endDate',
  remainingDays: 'endDate',
  premium: 'premium',
  installment: 'installment',
};

export const policyColumns = [
  { name: 'policyId', label: 'Poliçe No', field: 'policyId', align: 'left' as const, sortable: true },
  { name: 'type', label: 'Poliçe Türü', field: 'type', align: 'left' as const },
  { name: 'customerId', label: 'Müşteri', field: 'customerId', align: 'left' as const, sortable: false },
  {
    name: 'startDate',
    label: 'Başlangıç Tarihi',
    field: (row: Policy) => activeOptions.find((opt) => opt.value === row.startDate)?.label || formatDate(row.startDate),
    align: 'left' as const,
    sortable: true,
  },
  {
    name: 'endDate',
    label: 'Bitiş Tarihi ',
    field: (row: Policy) => activeOptions.find((opt) => opt.value === row.endDate)?.label || formatDate(row.endDate),
    align: 'left' as const,
    sortable: true,
  },
  { name: 'remainingDays', label: 'Kalan Gün', field: 'endDate', align: 'center' as const, sortable: true },
  {
    name: 'isActive',
    label: 'Aktif/Pasif',
    field: (row: Policy) => activeOptions.find((opt) => opt.value === row.isActive)?.label || row.isActive,
    align: 'center' as const,
  },
  { name: 'installment', label: 'Ödeme Sayısı', field: (row: Policy) => `${row.installment} `, align: 'center' as const, sortable: true },
  { name: 'premium', label: 'Prim Tutarı', field: (row: Policy) => `${row.premium} TL`, align: 'left' as const, sortable: true },
  { name: 'actions', label: 'İşlemler', field: 'actions', align: 'center' as const },
];

export const activeOptions = [
  { value: 'ACTIVE', label: 'Aktif' },
  { value: 'PASSIVE', label: 'Pasif' },
];

export const policyTypeOptions = [
  { label: 'Trafik Sigortası', value: 'TRAFIK' },
  { label: 'Kasko Sigortası', value: 'KASKO' },
  { label: 'DASK (Afet Sigortası)', value: 'DASK' },
  { label: 'Konut Sigortası', value: 'KONUT' },
  { label: 'Sağlık Sigortası', value: 'SAGLIK' },
];

export const insuranceCompanyOptions = [
  // ── Elementer Sigorta ───────────────────────────────────────────────────────
  { label: 'Ak Sigorta', value: 'AkSigorta' },
  { label: 'Allianz Sigorta', value: 'AllianzSigorta' },
  { label: 'Anadolu Sigorta', value: 'AnadoluSigorta' },
  { label: 'AXA Sigorta', value: 'AxaSigorta' },
  { label: 'Bupa Acıbadem Sigorta', value: 'BupaAcibademSigorta' },
  { label: 'Corpus Sigorta', value: 'CorpusSigorta' },
  { label: 'Doğa Sigorta', value: 'DogaSigorta' },
  { label: 'Emtia Sigorta', value: 'EmtiaSigorta' },
  { label: 'Eureko Sigorta', value: 'EurekoSigorta' },
  { label: 'Generali Sigorta', value: 'GeneraliSigorta' },
  { label: 'Groupama Sigorta', value: 'GroupamaSigorta' },
  { label: 'Güneş Sigorta', value: 'GunesSigorta' },
  { label: 'HDI Sigorta', value: 'HdiSigorta' },
  { label: 'Hepıyı Sigorta', value: 'HepiyiSigorta' },
  { label: 'Koru Sigorta', value: 'KoruSigorta' },
  { label: 'Magdeburger Sigorta', value: 'MagdeburgerSigorta' },
  { label: 'Mapfre Sigorta', value: 'MapfreSigorta' },
  { label: 'Neova Sigorta', value: 'NeovaSigorta' },
  { label: 'Orient Sigorta', value: 'OrientSigorta' },
  { label: 'Quick Sigorta', value: 'QuickSigorta' },
  { label: 'Ray Sigorta', value: 'RaySigorta' },
  { label: 'Sompo Sigorta', value: 'SompoSigorta' },
  { label: 'Türk Nippon Sigorta', value: 'TurkNipponSigorta' },
  { label: 'Türkiye Sigorta', value: 'TurkiyeSigorta' },
  { label: 'Unico Sigorta', value: 'UnicoSigorta' },
  { label: 'Zurich Sigorta', value: 'ZurichSigorta' },
  // ── Hayat & Emeklilik ───────────────────────────────────────────────────────
  { label: 'AgeSA Hayat ve Emeklilik', value: 'AgeSAHayatVeEmeklilik' },
  { label: 'Allianz Hayat ve Emeklilik', value: 'AllianzHayatVeEmeklilik' },
  { label: 'Anadolu Hayat Emeklilik', value: 'AnadoluHayatEmeklilik' },
  { label: 'Aviva SA', value: 'AvivaSA' },
  { label: 'AXA Hayat ve Emeklilik', value: 'AxaHayatVeEmeklilik' },
  { label: 'BNP Paribas Cardif Emeklilik', value: 'BnpParibasCardifEmeklilik' },
  { label: 'Cigna Sağlık Hayat ve Emeklilik', value: 'CignaSaglikHayatVeEmeklilik' },
  { label: 'Fiba Emeklilik', value: 'FibaEmeklilik' },
  { label: 'Garanti BBVA Emeklilik', value: 'GarantiBBVAEmeklilik' },
  { label: 'Katılım Emeklilik', value: 'KatilimEmeklilik' },
  { label: 'MetLife Emeklilik', value: 'MetLifeEmeklilik' },
  { label: 'NN Hayat ve Emeklilik', value: 'NNHayatVeEmeklilik' },
  { label: 'Türkiye Hayat Emeklilik', value: 'TurkiyeHayatEmeklilik' },
  { label: 'Vakıf Emeklilik', value: 'VakifEmeklilik' },
  // ── Diğer ──────────────────────────────────────────────────────────────────
  { label: 'DASK', value: 'Dask' },
  { label: 'Diğer', value: 'Diger' },
];

export const vehicleUsageTypeOptions = [
  { label: 'Özel', value: 'OZEL' },
  { label: 'Ticari', value: 'TICARI' },
  { label: 'Kiralık', value: 'KIRALIK' },
];

export const cascoTypeOptions = [
  { label: 'Tam Kasko', value: 'TAM_KASKO' },
  { label: 'Mini Kasko', value: 'MINI_KASKO' },
];

export const buildingConstructionTypeOptions = [
  { label: 'Betonarme', value: 'BETONARME' },
  { label: 'Yığma Kagir', value: 'YIGMA_KAGIR' },
  { label: 'Ahşap', value: 'AHSAP' },
  { label: 'Çelik', value: 'CELIK' },
];

export const residenceTypeOptions = [
  { label: 'Ev Sahibi', value: 'EV_SAHIBI' },
  { label: 'Kiracı', value: 'KIRACI' },
];

export const genderOptions = [
  { label: 'Erkek', value: 'ERKEK' },
  { label: 'Kadın', value: 'KADIN' },
];

export const healthPlanTypeOptions = [
  { label: 'TSS (Tamamlayıcı)', value: 'TSS' },
  { label: 'ÖSS (Özel)', value: 'OSS' },
];

export const coverageScopeOptions = [
  { label: 'Sadece Yatarak', value: 'YATARAK' },
  { label: 'Yatarak + Ayakta', value: 'YATARAK_AYAKTA' },
];

export const networkTierOptions = [
  { label: 'Network A', value: 'NETWORK_A' },
  { label: 'Network B', value: 'NETWORK_B' },
  { label: 'Network C', value: 'NETWORK_C' },
];
