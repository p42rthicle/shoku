# Feature: Full Log History (Raw View)

This document outlines the tasks required to implement a screen displaying all logged food entries chronologically.

## Core Requirements

1.  Display a continuous, scrollable list of *all* `LoggedEntry` items ever recorded.
2.  Order the list chronologically (e.g., most recent first or oldest first).
3.  Display key details for each entry (e.g., date, meal, food name, calories, protein).

## Implementation To-Do List

-   [x] **Repository (`FoodRepository`):**
    -   [x] Add method `getAllLoggedEntries(): Flow<List<LoggedEntry>>`. ✅
        -   [x] Adding a corresponding query `getAllLoggedEntries(): Flow<List<LoggedEntryEntity>>` to `LoggedEntryDao`, ordered by date (and possibly ID). ✅
        -   [x] Calling the DAO method and mapping `LoggedEntryEntity` to `LoggedEntry` in the repository implementation. ✅
-   [ ] **ViewModel (`AllEntriesViewModel`):**
    -   [x] Create `AllEntriesViewModel` class. ✅
    -   [x] Inject `FoodRepository`. ✅
    -   [x] Define `UiState` for `AllEntriesScreen`. ✅
    -   [x] Fetch `allEntries` from the repository and update `UiState`. ✅
    -   [x] *(Optional)* Define Intents/Effects if any actions (like deleting) are needed directly from this screen in the future. ✅
-   [x] **UI (`AllEntriesScreen`):**
    -   [x] Create `AllEntriesScreen` Composable function. ✅
    -   [x] Connect to `AllEntriesViewModel`. ✅
    -   [x] Display a `LazyColumn` of `allEntries`. ✅
    -   [x] Create `FullLogItemRow` Composable to display date, meal, name, calories, protein for each item. ✅
    -   [x] Handle loading and error states. ✅
    -   [x] Add appropriate `TopAppBar` (e.g., title "All Entries", back navigation). ✅
-   [x] **Dependency Injection (Hilt):**
    -   [x] Provide `AllEntriesViewModel`. ✅
-   [ ] **Navigation:**
    -   [x] Add `AllEntries` destination to `Screen.kt`. ✅
    -   [x] Add navigation from somewhere (e.g., History screen, Settings) to `AllEntriesScreen`. ✅
    -   [x] Add navigation from `HistoryScreen` (on day selection) to the detailed day view (this might navigate back to `HomeScreen` with the selected date as an argument). *(Handled in HistoryScreen nav call)* ✅
-   [ ] **Testing:**
    -   [ ] Unit Test: `AllEntriesViewModel`.
    -   [ ] Unit Test: `FoodRepository`/DAO `getAllLoggedEntries` method.
    -   [ ] Instrumentation Test: `AllEntriesScreen`. 