# Feature: Day-wise History

This document outlines the tasks required to implement the day-wise history screen, showing summaries for past days.

## Core Requirements

1.  Display a list of past days for which food items have been logged.
2.  For each day in the list, show summary totals (e.g., total calories, total protein).
3.  Allow the user to tap on a day in the list to navigate to a detailed view of that day (potentially reusing or adapting the `HomeScreen` logic).

## Implementation To-Do List

-   [x] **Model (`DailySummary`):**
    -   [x] Define a data class `DailySummary(date: LocalDate, totalCalories: Double, totalProtein: Double)` to represent the summary for a single day.
-   [x] **Repository (`FoodRepository`):**
    -   [x] Add method `getDailySummaries(): Flow<List<DailySummary>>`.
        -   [x] Querying `LoggedEntryDao` to get all unique dates with entries.
        -   [x] For each date, calculating the sum of calories and protein (potentially using a specific DAO query or calculating in the repository).
        -   [x] *(Alternative)* Add DAO query `getDailySummaries(): Flow<List<DailySummaryPojo>>` that performs grouping and summation directly in SQL, mapping the POJO to `DailySummary` in the repository.
-   [x] **ViewModel (`HistoryViewModel`):**
    -   [x] Create `HistoryViewModel` class.
    -   [x] Inject `FoodRepository`.
    -   [x] Define `UiState` for `HistoryScreen` including necessary fields.
    -   [x] Fetch `dailySummaries` from the repository and update `UiState`.
    -   [x] Define `Intent` for selecting a day (`SelectDay(date: LocalDate)`).
    -   [x] Define `Effect` for navigation (`NavigateToDayDetail(date: LocalDate)`).
-   [x] **UI (`HistoryScreen`):**
    -   [x] Create `HistoryScreen` Composable function.
    -   [x] Connect to `HistoryViewModel` (`hiltViewModel`, collect state).
    -   [x] Display a list (`LazyColumn`) of `dailySummaries`.
    -   [x] Create `DailySummaryRow` Composable to display date, calories, protein for each item.
    -   [x] Make each row clickable, triggering `Intent.SelectDay`.
    -   [x] Handle loading and error states.
    -   [x] Add appropriate `TopAppBar` (e.g., title "History", back navigation).
-   [ ] **Dependency Injection (Hilt):**
    -   [ ] Provide `HistoryViewModel`.
-   [ ] **Navigation:**
    -   [ ] Add `History` destination to `Screen.kt`.
    -   [ ] Add navigation from somewhere (e.g., `HomeScreen` TopAppBar menu, Bottom Navigation) to `HistoryScreen`.
    -   [ ] Add navigation from `HistoryScreen` (on day selection) to the detailed day view (this might navigate back to `HomeScreen` with the selected date as an argument).
-   [ ] **Testing:**
    -   [ ] Unit Test: `HistoryViewModel` (data loading, state updates, navigation effect).
    -   [ ] Unit Test: `FoodRepository`/DAO (verify `getDailySummaries` logic/query).
    -   [ ] Instrumentation Test: `HistoryScreen` (list display, click navigation). 