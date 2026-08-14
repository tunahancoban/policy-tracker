// composables/useUserSearch.ts

import type { Ref } from 'vue';

interface UserOption {
  userId: string;
  fullName: string;
  email: string;
}

// ─── 1. Backend search (used in UserManagementPage search bar) ───────────────

/**
 * Queries the backend dynamically.
 * - If the query contains '@' → search by email
 * - Otherwise             → search by firstName (backend handles partial match)
 * The store's `searchUser` action writes the result back to `users` ref.
 */
export function useUserBackendSearch(
  searchQuery: Ref<string>,
  userStore: { searchUser: (params: Record<string, string>) => Promise<void> },
) {
  const onSearch = async (): Promise<void> => {
    const query = searchQuery.value.trim();

    if (!query) {
      await userStore.searchUser({});
      return;
    }

    if (query.includes('@')) {
      await userStore.searchUser({ email: query });
      return;
    }

    // Split into first/last name if there's a space
    const parts = query.split(' ').filter(Boolean);
    if (parts.length >= 2) {
      await userStore.searchUser({ firstName: parts[0]!, lastName: parts[1]! });
      return;
    }

    // Single word: try firstName search
    await userStore.searchUser({ firstName: query });
  };

  return { onSearch };
}

// ─── 2. Local dropdown filter (used in usePolicyForm for q-select) ───────────

/**
 * Returns a filter function compatible with Quasar's q-select @filter event.
 * The caller owns the `filteredUserOptions` ref; this composable only computes
 * the filtered subset and calls back so Quasar can re-render the dropdown.
 */
export function useUserSearch(allOptions: () => UserOption[]) {
  /**
   * Quasar filter callback — must call `update` synchronously.
   * Minimum 2 characters required before filtering begins.
   */
  const filterUserFn = (
    val: string,
    update: (callback: () => void) => void,
    setFiltered: (options: UserOption[]) => void,
  ): void => {
    update(() => {
      const needle = val.trim().toLowerCase();

      if (needle.length < 2) {
        setFiltered([]);
        return;
      }

      setFiltered(
        allOptions().filter(
          (u) =>
            u.fullName.toLowerCase().includes(needle) ||
            String(u.userId).toLowerCase().includes(needle) ||
            u.email.toLowerCase().includes(needle),
        ),
      );
    });
  };

  return { filterUserFn };
}
