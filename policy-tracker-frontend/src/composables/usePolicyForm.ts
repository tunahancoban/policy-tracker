// composables/usePolicyForm.ts

import { ref, computed, watch, nextTick } from 'vue';
import { useCustomerStore } from '../stores/customer';
import { useUserStore } from '../stores/user';
import { useCustomerSearch } from './useCustomerSearch';
import { useUserBackendSearch } from './useUserSearch';
import {
  isValidDate,
  formatDateToSlash,
  getTodayFormatted,
  getNextYearFormatted,
} from '../utils/dateHelper';
import {
  policyTypeOptions,
  insuranceCompanyOptions,
  type PolicyForm,
  type Policy,
  type CreatePolicyRequest,
  type RenewPolicyRequest,
  type TypeSpecificFields,
  getInitialTypeFields,
  fillTypeFieldsFromPolicy,
} from '../types/policy.types';

export interface CustomerOption {
  customerId: string;
  fullName: string;
  identityNumber: string;
}

export interface UserOption {
  userId: string;
  fullName: string;
  email: string;
}

export function usePolicyForm(
  props: Readonly<{
    isRenewal?: boolean;
    policyData?: Policy | null;
  }>,
) {
  const customerStore = useCustomerStore();
  const userStore = useUserStore();

  const loading = ref(false);
  const filteredCustomerOptions = ref<CustomerOption[]>([]);
  const filteredUserOptions = ref<UserOption[]>([]);
  const filteredInsuranceCompanyOptions = ref([...insuranceCompanyOptions]);

  const typeSpecificFields = ref<TypeSpecificFields>({});

  const suppressTypeWatch = ref(false);

  const withLoading = async <T>(fn: () => Promise<T>): Promise<T | undefined> => {
    loading.value = true;
    try {
      return await fn();
    } finally {
      loading.value = false;
    }
  };

  const initialFormState = (): PolicyForm => ({
    customerId: '',
    type: '',
    premium: 0,
    startDate: '',
    endDate: '',
    note: '',
    installment: 1,
    responsibleUserId: '',
    isActive: 'ACTIVE',
    company: '',
  });

  const form = ref<PolicyForm>(initialFormState());

  // When policy type changes in create mode, reset type-specific fields.
  watch(() => form.value.type, (newType) => {
    if (suppressTypeWatch.value) return;
    typeSpecificFields.value = getInitialTypeFields(newType);
  });

  const customerOptions = computed<CustomerOption[]>(() =>
    customerStore.customerData.map((c) => {
      const namePart =
        `${c.fullName || ''}`.trim() || c.email || 'İsimsiz Müşteri';
      const identityPart = c.identityNumber ? `(TC: ${c.identityNumber})` : `(ID: ${c.customerId})`;
      return {
        customerId: c.customerId,
        identityNumber: c.identityNumber || '',
        fullName: `${namePart} ${identityPart}`,
      };
    }),
  );

  const userOptions = computed<UserOption[]>(() => {
    const data = Array.isArray(userStore.users) ? userStore.users : [];
    return data.map((u) => {
      const namePart = `${u.fullName || ''}`.trim() || 'Acente Personeli';
      const infoPart = u.email ? `(${u.email})` : `(ID: ${u.id})`;
      return {
        userId: u.id,
        email: u.email || '',
        fullName: `${namePart} ${infoPart}`,
      };
    });
  });

  const customerSearchQuery = ref('');
  const { onSearch: triggerCustomerSearch } = useCustomerSearch(customerSearchQuery, customerStore);

  const filterCustomerFn = async (
    val: string,
    update: (callback: () => void) => void,
  ): Promise<void> => {
    const needle = val.trim();

    if (needle.length < 2) {
      update(() => {
        filteredCustomerOptions.value = [];
      });
      return;
    }

    customerSearchQuery.value = needle;
    await triggerCustomerSearch();

    update(() => {
      filteredCustomerOptions.value = customerOptions.value;
    });
  };

  const userSearchQuery = ref('');
  const { onSearch: triggerUserSearch } = useUserBackendSearch(userSearchQuery, userStore);

  const filterUserFn = async (
    val: string,
    update: (callback: () => void) => void,
  ): Promise<void> => {
    const needle = val.trim();

    if (needle.length < 2) {
      update(() => {
        filteredUserOptions.value = [];
      });
      return;
    }

    userSearchQuery.value = needle;
    await triggerUserSearch();

    update(() => {
      filteredUserOptions.value = userOptions.value;
    });
  };

  const onModalShow = async (): Promise<void> => {
    if (!customerStore.customerData?.length) await customerStore.fetchCustomerData();
    if (!userStore.users?.length) await userStore.fetchUsers();

    customerSearchQuery.value = '';
    filteredCustomerOptions.value = [];
    filteredUserOptions.value = [];

    if (props.isRenewal && props.policyData) {
  
      suppressTypeWatch.value = true;
      _fillRenewalForm(props.policyData);

      await nextTick();
      typeSpecificFields.value = fillTypeFieldsFromPolicy(props.policyData);

      suppressTypeWatch.value = false;
    } else {
      form.value = initialFormState();
      typeSpecificFields.value = {};
    }
  };

  const _fillRenewalForm = (policy: Policy): void => {
    const start = policy.endDate ? formatDateToSlash(policy.endDate) : getTodayFormatted();
    const end = getNextYearFormatted(start);

    form.value = {
      customerId: policy.customerId || '',
      type: policy.type || '',
      premium: policy.premium ?? 0,
      installment: policy.installment ?? 1,
      responsibleUserId:
      userStore.users.find((u) => u.id === policy.responsibleUserId)?.fullName || '',
      startDate: start,
      endDate: end,
      note: policy.note ? `${policy.note} (Yenileme)` : 'Poliçe Yenileme',
      isActive: policy.isActive || '',
      company: policy.company || '',
    };

    const matchedCustomer = customerOptions.value.find((c) => c.customerId === policy.customerId);
    if (matchedCustomer) filteredCustomerOptions.value = [matchedCustomer];

    const matchedUser = userOptions.value.find((u) => u.userId === policy.responsibleUserId);
    if (matchedUser) filteredUserOptions.value = [matchedUser];
  };
const buildTypeFieldsPayload = (): Partial<TypeSpecificFields> =>
  Object.fromEntries(
    Object.entries(typeSpecificFields.value).filter(
      ([, v]) => v !== null && v !== undefined,
    ),
  );

const buildCreatePayload = (): CreatePolicyRequest => {
  return {
    customerId: form.value.customerId,
    type: form.value.type,
    startDate: form.value.startDate,
    endDate: form.value.endDate,
    premium: form.value.premium,
    installment: form.value.installment,
    responsibleUserId: form.value.responsibleUserId,
    note: form.value.note,
    isActive: form.value.isActive,
    company: form.value.company,
    ...buildTypeFieldsPayload(),
  } as CreatePolicyRequest;
};

  const buildRenewPayload = (policy: Policy): RenewPolicyRequest => ({
    previousPolicyId: policy.policyId,
    startDate: form.value.startDate.replace(/\//g, '-'),
    endDate: form.value.endDate.replace(/\//g, '-'),
    premium: form.value.premium,
    installment: form.value.installment,
    responsibleUserId: form.value.responsibleUserId,
    note: form.value.note,

    ...buildTypeFieldsPayload(),
  });

  type SubmitResult =
    | { mode: 'created'; payload: CreatePolicyRequest }
    | { mode: 'renewed'; payload: RenewPolicyRequest }
    | null;

  const buildSubmitPayload = (): SubmitResult => {
    if (props.isRenewal && props.policyData) {
      return { mode: 'renewed', payload: buildRenewPayload(props.policyData) };
    }
    return { mode: 'created', payload: buildCreatePayload() };
  };

  const submitWithLoading = (fn: () => Promise<void>): Promise<void | undefined> => withLoading(fn);

  const filterInsuranceCompanyFn = (
    val: string,
    update: (callback: () => void) => void,
  ): void => {
    const needle = val.trim().toLowerCase();
    update(() => {
      filteredInsuranceCompanyOptions.value = needle.length === 0
        ? insuranceCompanyOptions
        : insuranceCompanyOptions.filter((opt) =>
            opt.label.toLowerCase().includes(needle),
          );
    });
  };

  return {
    form,
    loading,
    typeSpecificFields,
    suppressTypeWatch,
    filteredCustomerOptions,
    filteredUserOptions,
    filteredInsuranceCompanyOptions,
    customerOptions,
    userOptions,
    customerStore,
    policyTypeOptions,
    insuranceCompanyOptions,
    isValidDate,
    onModalShow,
    filterCustomerFn,
    filterUserFn,
    filterInsuranceCompanyFn,
    buildSubmitPayload,
    submitWithLoading,
    getInitialTypeFields,
    fillTypeFieldsFromPolicy,
  };
}