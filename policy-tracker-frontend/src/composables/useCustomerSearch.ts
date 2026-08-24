import type { Ref } from 'vue';

export function useCustomerSearch(
  searchQuery: Ref<string>,
  customerStore: { searchCustomer: (params: Record<string, string>) => Promise<void> },
) {
  const onSearch = async (): Promise<void> => {
    const query = searchQuery.value.trim();

    if (!query) {
      await customerStore.searchCustomer({});
      return;
    }

    if (query.toUpperCase().startsWith('CST')) {
      await customerStore.searchCustomer({ customerId: query });
      return;
    }

    if (/^\d+$/.test(query)) {
      const params = query.length === 11 ? { identityNumber: query } : { phoneNumber: query };
      await customerStore.searchCustomer(params);
      return;
    }

    if (query.includes('@')) {
      await customerStore.searchCustomer({ email: query });
      return;
    }

    await customerStore.searchCustomer({ fullName: query });
  };

  return { onSearch };
}
