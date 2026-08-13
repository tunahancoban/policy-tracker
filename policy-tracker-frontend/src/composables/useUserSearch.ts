// composables/useUserSearch.ts
// Responsible ONLY for filtering a local list of user options by a search term.
// Extracted from NewPolicyModal to satisfy Single Responsibility Principle.

interface UserOption {
  userId: string;
  fullName: string;
  email: string;
}

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
