# Feature: Daily Summary & Target

This document outlines the tasks required to implement the daily summary display and daily target setting/tracking.

## Core Requirements

1.  Display total calories and protein consumed for the current (or selected) day.
2.  Allow users to view and set daily targets for calories and protein.
3.  Provide a visual representation (e.g., progress bar, chart) comparing consumed totals against targets.
4.  Display the logged entries for the selected day, potentially grouped by meal.

## Implementation To-Do List

-   [x] **Repository (`FoodRepository`):**
    -   [x] Add method `getEntriesForDate(date: LocalDate): Flow<List<LoggedEntry>>` (if not already present and verified). ✅
    -   [ ] Add method/logic to calculate daily totals (sum calories, sum protein) from entries for a specific date. *(Implemented in ViewModel)*
    -   [x] Add methods to save/retrieve daily targets (e.g., using `SharedPreferences`). ✅
-   [x] **ViewModel (`HomeViewModel`):**
    -   [x] Create `HomeViewModel` class. ✅
    -   [x] Inject `FoodRepository`. ✅
    -   [x] Define `UiState` for `HomeScreen` including necessary fields (date, entries, totals, targets, inputs, loading states). ✅
    -   [x] Implement logic to observe `selectedDate` changes. *(Handled by `ChangeDate` Intent)* ✅
    -   [x] On date change, fetch entries for that date from the repository. ✅
    -   [x] Calculate daily totals based on fetched entries. ✅
    -   [x] Fetch daily targets from the repository. ✅
    -   [x] Update the `UiState` Flow. ✅
    -   [x] Define `Intent`s for changing date, loading, opening/saving targets. ✅
    -   [x] Define `Effect`s for navigation and showing errors/success. ✅
-   [x] **UI (`HomeScreen`):**
    -   [x] Create `HomeScreen` Composable function. ✅
    -   [x] Connect to `HomeViewModel` using `hiltViewModel()` and collect `uiState`. ✅
    -   [x] Implement `DateSelector` Composable. ✅
    -   [x] Implement `DailySummaryDisplay` Composable. ✅
    -   [x] Implement `TargetProgressIndicator` Composable. ✅
    -   [x] Implement `DailyLogList` Composable (grouped by meal). ✅
        -   [ ] *(Future Enhancement)* Add click action to edit a logged item.
        -   [ ] *(Future Enhancement)* Add swipe action or button to delete a logged item.
    -   [x] Add button/icon to trigger `Intent.OpenTargetSettings`. ✅
    -   [x] Add FAB to navigate to `AddFoodScreen`. ✅
-   [x] **UI (`DailyTargetScreen` or Dialog):**
    -   [x] Create a simple screen Composable (`DailyTargetScreen`) for setting targets. ✅
    -   [x] Connect inputs and save button to `HomeViewModel`. ✅
-   [x] **Dependency Injection (Hilt):**
    -   [x] Provide `HomeViewModel`. ✅
    -   [x] Ensure `FoodRepository` and target storage mechanism are provided. ✅
-   [x] **Navigation:**
    -   [x] Update `MainActivity` / `NavHost` to include `HomeScreen` as the start destination. ✅
    -   [x] Add navigation action from `HomeScreen` FAB to `AddFoodScreen`. ✅
    -   [x] Add navigation action from `HomeScreen` settings button to `DailyTargetScreen`. ✅
-   [ ] **Testing:**
    -   [ ] Unit Test: `HomeViewModel` (date changes, data loading, calculations, target handling).
    -   [ ] Unit Test: `FoodRepository` (target saving/loading - SharedPreferences testing can be tricky).
    -   [ ] Instrumentation Test: `HomeScreen` (date selection, summary display, target display, list display, navigation).
    -   [ ] Instrumentation Test: `DailyTargetScreen`/Dialog. 