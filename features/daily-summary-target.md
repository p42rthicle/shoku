# Feature: Daily Summary & Target

This document outlines the tasks required to implement the daily summary display and daily target setting/tracking.

## Core Requirements

1.  Display total calories and protein consumed for the current (or selected) day.
2.  Allow users to view and set daily targets for calories and protein.
3.  Provide a visual representation (e.g., progress bar, chart) comparing consumed totals against targets.
4.  Display the logged entries for the selected day, potentially grouped by meal.

## Implementation To-Do List

-   [ ] **Repository (`FoodRepository`):**
    -   [ ] Add method `getEntriesForDate(date: LocalDate): Flow<List<LoggedEntry>>` (if not already present and verified).
    -   [ ] Add method/logic to calculate daily totals (sum calories, sum protein) from entries for a specific date. This could be done in the repository or potentially the ViewModel based on the raw entry list.
    -   [ ] Add methods to save/retrieve daily targets (e.g., using `SharedPreferences` or a dedicated `DailyTarget` entity/DAO/table).
-   [ ] **ViewModel (`HomeViewModel`):**
    -   [ ] Create `HomeViewModel` class.
    -   [ ] Inject `FoodRepository`.
    -   [ ] Define `UiState` for `HomeScreen` including:
        -   `selectedDate: LocalDate`
        -   `dailyEntries: List<LoggedEntry>`
        -   `totalCalories: Double`
        -   `totalProtein: Double`
        -   `calorieTarget: Double`
        -   `proteinTarget: Double`
        -   `isLoading: Boolean`
        -   `error: String?`
    -   [ ] Implement logic to observe `selectedDate` changes.
    -   [ ] On date change, fetch entries for that date from the repository.
    -   [ ] Calculate daily totals based on fetched entries.
    -   [ ] Fetch daily targets from the repository.
    -   [ ] Update the `UiState` Flow.
    -   [ ] Define `Intent`s for changing the selected date (`ChangeDate`), opening target settings (`OpenTargetSettings`), potentially retrying load (`RetryLoad`).
    -   [ ] Define `Effect`s for navigation (`NavigateToTargetSettings`) or showing errors (`ShowError`).
-   [ ] **UI (`HomeScreen`):**
    -   [x] Create `HomeScreen` Composable function. ✅
    -   [x] Connect to `HomeViewModel` using `hiltViewModel()` and collect `uiState`. ✅
    -   [x] Implement `DateSelector` Composable (e.g., scrollable row with dates, arrows to change day). ✅
    -   [x] Implement `DailySummaryDisplay` Composable showing total calories/protein. ✅
    -   [x] Implement `TargetProgressIndicator` Composable (e.g., progress bars or Pie chart) showing consumed vs. target. ✅
    -   [x] Implement `DailyLogList` Composable displaying `dailyEntries` (potentially grouped by meal). ✅
    -   [x] Add button/icon to trigger `Intent.OpenTargetSettings`. ✅
    -   [x] Add FAB to navigate to `AddFoodScreen` (Navigation setup required). ✅
-   [ ] **UI (`DailyTargetScreen` or Dialog):**
    -   [ ] Create a simple screen or dialog Composable for setting calorie and protein targets.
    -   [ ] Connect inputs to a relevant ViewModel (could be `HomeViewModel` or a dedicated `SettingsViewModel`) to save targets via the repository.
-   [ ] **Dependency Injection (Hilt):**
    -   [x] Provide `HomeViewModel`. ✅
    -   [x] Ensure `FoodRepository` and target storage mechanism (e.g., `SharedPreferences` accessor or `DailyTargetDao`) are provided. ✅
-   [ ] **Navigation:**
    -   [x] Update `MainActivity` / `NavHost` to include `HomeScreen` as the start destination. ✅
    -   [x] Add navigation action from `HomeScreen` FAB to `AddFoodScreen`. ✅
    -   [x] Add navigation action from `HomeScreen` settings button to `DailyTargetScreen` (or handle dialog display). ✅
-   [ ] **Testing:**
    -   [ ] Unit Test: `HomeViewModel` (date changes, data loading, calculations, target handling).
    -   [ ] Unit Test: `FoodRepository` (daily total calculation logic, target saving/loading).
    -   [ ] Instrumentation Test: `HomeScreen` (date selection, summary display, target display, list display, navigation).
    -   [ ] Instrumentation Test: `DailyTargetScreen`/Dialog. 