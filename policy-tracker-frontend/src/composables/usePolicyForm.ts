// composables/usePolicyForm.ts

import { ref, computed } from 'vue';
import { useCustomerStore } from '../stores/customer';
import { useUserStore } from '../stores/user';
import { useCustomerSearch } from './useCustomerSearch';
import { useUserSearch } from './useUserSearch';
import {
  isValidDate,
  formatDateToSlash,
  getTodayFormatted,
  getNextYearFormatted,
} from '../utils/dateHelper';
import {
  policyTypeOptions,
  type PolicyForm,
  type Policy,
  type CreatePolicyRequest,
  type RenewPolicyRequest,
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

  const initialFormState = (): PolicyForm => ({
    customerId: '',
    type: '',
    premium: 0,
    startDate: '',
    endDate: '',
    note: '',
    installment: 1,
    responsibleUserId: '',
  });

  const form = ref<PolicyForm>(initialFormState());

  const customerOptions = computed<CustomerOption[]>(() =>
    customerStore.customerData.map((c) => {
      const namePart =
        `${c.firstName || ''} ${c.lastName || ''}`.trim() || c.email || 'İsimsiz Müşteri';
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
      const namePart = `${u.firstName || ''} ${u.lastName || ''}`.trim() || 'Acente Personeli';
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

  const { filterUserFn: _filterUserFn } = useUserSearch(() => userOptions.value);

  const filterUserFn = (val: string, update: (callback: () => void) => void): void => {
    _filterUserFn(val, update, (options) => {
      filteredUserOptions.value = options;
    });
  };

  const onModalShow = async (): Promise<void> => {
    if (!customerStore.customerData?.length) await customerStore.fetchCustomerData();
    if (!userStore.users?.length) await userStore.fetchUsers();

    customerSearchQuery.value = '';
    filteredCustomerOptions.value = [];
    filteredUserOptions.value = [];

    if (props.isRenewal && props.policyData) {
      _fillRenewalForm(props.policyData);
    } else {
      form.value = initialFormState();
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
      responsibleUserId: policy.responsibleUserId || '',
      startDate: start,
      endDate: end,
      note: policy.note ? `${policy.note} (Yenileme)` : 'Poliçe Yenileme',
    };

    const matchedCustomer = customerOptions.value.find((c) => c.customerId === policy.customerId);
    if (matchedCustomer) filteredCustomerOptions.value = [matchedCustomer];

    const matchedUser = userOptions.value.find((u) => u.userId === policy.responsibleUserId);
    if (matchedUser) filteredUserOptions.value = [matchedUser];
  };

  const buildCreatePayload = (): CreatePolicyRequest => ({
    customerId: form.value.customerId,
    type: form.value.type,
    startDate: form.value.startDate.replace(/\//g, '-'),
    endDate: form.value.endDate.replace(/\//g, '-'),
    premium: form.value.premium,
    installment: form.value.installment,
    responsibleUserId: form.value.responsibleUserId,
    note: form.value.note,
  });

  const buildRenewPayload = (policy: Policy): RenewPolicyRequest => ({
    previousPolicyId: policy.policyId,
    startDate: form.value.startDate.replace(/\//g, '-'),
    endDate: form.value.endDate.replace(/\//g, '-'),
    premium: form.value.premium,
    installment: form.value.installment,
    responsibleUserId: form.value.responsibleUserId,
    note: form.value.note,
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

  return {
    form,
    loading,
    filteredCustomerOptions,
    filteredUserOptions,
    customerStore,
    policyTypeOptions,
    isValidDate,
    onModalShow,
    filterCustomerFn,
    filterUserFn,
    buildSubmitPayload,
  };
}
