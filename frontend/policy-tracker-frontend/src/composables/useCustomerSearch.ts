import type { Ref } from 'vue';

/**
 * Composable that encapsulates the smart-search heuristic for customers.
 *
 * It inspects the search query and dispatches the correct store action:
 *   - Empty string       → fetch all (reset)
 *   - Starts with "CST"  → search by customerId
 *   - Pure digits        → search by identityNumber + phoneNumber
 *   - Contains "@"       → search by email
 *   - Anything else      → search by firstName (plain-text fallback)
 *
 * @param searchQuery - reactive ref bound to the search input v-model
 * @param customerStore - the Pinia customer store instance (must expose `searchCustomer`)
 */
export function useCustomerSearch(
  searchQuery: Ref<string>,
  customerStore: { searchCustomer: (params: Record<string, string>) => Promise<void> },
) {
  const onSearch = (): void => {
    const query = searchQuery.value.trim();

    if (!query) {
      void customerStore.searchCustomer({});
      return;
    }

    if (query.toUpperCase().startsWith('CST')) {
      void customerStore.searchCustomer({ customerId: query });
      return;
    }

    if (/^\d+$/.test(query)) {
      void customerStore.searchCustomer({ identityNumber: query });
      void customerStore.searchCustomer({ phoneNumber: query });
      return;
    }

    if (query.includes('@')) {
      void customerStore.searchCustomer({ email: query });
      return;
    }

    void customerStore.searchCustomer({ firstName: query });
  };

  return { onSearch };
}
