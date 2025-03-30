# Feature: Detailed Daily Breakdown

This document outlines the tasks related to viewing all logged food items for a specific day.

## Core Requirements

1.  Select or navigate to a specific date.
2.  Display all `LoggedEntry` items for that date.
3.  Group or clearly show the `Meal` associated with each entry.
4.  *(Future Enhancement)* Allow editing or deleting individual entries from this view.

## Current Status & To-Do List

This functionality is currently implemented within the existing `HomeScreen` composable.

-   [x] **Navigation:** `HomeScreen` accepts an optional `date` argument to display details for a specific day. Navigation from `HistoryScreen` passes the selected date. ✅
-   [x] **ViewModel (`HomeViewModel`):** Fetches `LoggedEntry` items for the `selectedDate` using `repository.getEntriesForDate()`. ✅
-   [x] **UI (`HomeScreen` / `DailyLogList`):**
    -   [x] Displays the list of `LoggedEntry` items fetched by the ViewModel. ✅
    -   [x] `DailyLogList` groups entries by `Meal` using sticky headers. ✅
-   [ ] **(Future Enhancement): Edit/Delete Functionality**
    -   [ ] Add UI elements (e.g., click action, swipe action, buttons) to trigger edit/delete on items in `DailyLogList`.
    -   [ ] Add corresponding `Intent`s (e.g., `EditEntry(entry)`, `DeleteEntry(entry)`).
    -   [ ] Add handling logic in the relevant ViewModel (`HomeViewModel` or potentially a dedicated one).
    -   [ ] Add corresponding methods in `FoodRepository` and DAOs (`updateLoggedEntry`, `deleteLoggedEntry`).
    -   [ ] Handle navigation to an edit screen if needed.

**Conclusion:** The core requirement of viewing a detailed daily breakdown is complete via `HomeScreen`. Further enhancements like editing/deleting are noted. 