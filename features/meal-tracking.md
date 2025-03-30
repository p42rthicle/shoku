# Feature: Meal Tracking

This document outlines the tasks related to logging food items under specific meals.

## Core Requirements

1.  Associate each `LoggedEntry` with a specific meal (e.g., Breakfast, Lunch, Dinner, Snacks).
2.  Allow users to select the meal when adding a food entry.
3.  *(Advanced/Future)* Allow users to dynamically add, edit, or delete meal categories.

## Current Status & To-Do List

The basic functionality using a predefined `Meal` enum is already largely implemented as part of the initial `AddFoodScreen` setup.

-   [x] **Model:** `LoggedEntry` includes a `meal: Meal` property. ✅
-   [x] **Model:** `Meal` enum defines standard meal types (`BREAKFAST`, `LUNCH`, `DINNER`, `SNACKS`). ✅
-   [x] **ViewModel (`AddEntryViewModel`):**
    -   [x] Manages `selectedMeal` in `UiState`. ✅
    -   [x] Handles `Intent.UpdateSelectedMeal`. ✅
    -   [x] Includes `selectedMeal` when creating `LoggedEntry` in `saveEntry()` logic. ✅
-   [x] **UI (`AddFoodScreen` / `MealSelector`):**
    -   [x] `MealSelector` Composable exists. ✅
    -   [x] `MealSelector` displays options from `Meal.entries`. ✅
    -   [x] `MealSelector` updates ViewModel state via `onIntent`. ✅
-   [ ] **Testing:**
    -   [ ] Add Unit Test for `AddEntryViewModel` verifying `UpdateSelectedMeal` intent correctly updates state.
    -   [ ] Add Unit Test for `AddEntryViewModel` verifying the `selectedMeal` state is correctly passed when `saveEntry` creates the `LoggedEntry`.
    -   [ ] Add Instrumentation Test for `AddFoodScreen` verifying `MealSelector` displays correctly and updates ViewModel on selection.
-   [ ] **(Future Enhancement): Dynamic Meal Management**
    -   [ ] Change `Meal` from Enum to a data class/database entity.
    -   [ ] Create `MealDao`, `MealEntity`, add to `AppDatabase`.
    -   [ ] Update `LoggedEntryEntity` to use a Meal ID (foreign key).
    -   [ ] Update `FoodRepository` to manage CRUD operations for Meals.
    -   [ ] Update `AddEntryViewModel`/`HomeViewModel` state to load dynamic meals.
    -   [ ] Update `MealSelector` UI to display dynamic list and potentially add/edit options.
    -   [ ] Update Mappers.

**Note:** Displaying logged items grouped by meal on the main screen or history screen is part of the "Daily Summary" or "History" features, not this specific logging task. 