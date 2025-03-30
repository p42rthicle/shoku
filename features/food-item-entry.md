# Feature: Food Item Entry

This document outlines the tasks required to implement the basic food item entry feature.

## To-Do List

-   [x] **Project Setup:** Ensure necessary dependencies (Compose, Room, Coroutines, ViewModel, Navigation Compose, Hilt) are added to `build.gradle.kts` files. ✅
-   [x] **Data Model:** Define the `FoodItem` data class (representing the concept of a food) in the domain/common layer. ✅
-   [x] **Data Model:** Define the `LoggedEntry` data class (representing an instance of eating a food) in the domain/common layer. ✅
-   [x] **Data Model:** Define the `Meal` data class/enum (e.g., Breakfast, Lunch, Dinner) in the domain/common layer. ✅
-   [x] **Database:** Define `FoodItemEntity` Room Entity. ✅
-   [x] **Database:** Define `LoggedEntryEntity` Room Entity (including fields for timestamp, meal type, quantity, unit, calories, protein, and potentially a foreign key to `FoodItemEntity` or store denormalized food details). ✅
-   [x] **Database:** Define `FoodItemDao` interface with methods for inserting and possibly querying `FoodItemEntity`. ✅
-   [x] **Database:** Define `LoggedEntryDao` interface with method for inserting `LoggedEntryEntity`. ✅
-   [x] **Database:** Create `AppDatabase` class extending `RoomDatabase`, listing entities and DAOs. ✅
-   [x] **Dependency Injection:** Set up DI modules (Hilt/Koin) to provide `AppDatabase`, `FoodItemDao`, and `LoggedEntryDao`.
-   [x] **Repository:** Define `FoodRepository` interface with methods like `addLoggedEntry(entry: LoggedEntry)` and `getOrCreateFoodItem(name: String, calories: Double, protein: Double): FoodItem`. ✅
-   [x] **Repository:** Implement `FoodRepositoryImpl` injecting DAOs and implementing the interface methods (handle mapping between domain models and entities). ✅
-   [x] **Dependency Injection:** Add `FoodRepository` to DI modules. ✅
-   [x] **ViewModel (MVI):** Define `AddFoodContract` containing `UiState`, `Intent`, and `Effect` classes for the Add Food screen. ✅
-   [x] **ViewModel (MVI):** Create `AddEntryViewModel` implementing `ViewModel()`. ✅
-   [x] **ViewModel (MVI):** Inject `FoodRepository` (or a `LogFoodItemUseCase`) into `AddEntryViewModel`. ✅
-   [x] **ViewModel (MVI):** Implement MVI logic: handle `Intent.SaveEntry`, call repository/use case, update `UiState` (e.g., show loading, success/error message via `Effect`). ✅
-   [x] **UI (`AddFoodScreen`):** Create the basic Composable structure for `AddFoodScreen`. ✅
-   [x] **UI (`AddFoodScreen`):** Add `TextField` Composables for Name, Quantity, Calories, Protein. ✅
-   [x] **UI (`AddFoodScreen`):** Add a dropdown/selector Composable for Unit (e.g., using `ExposedDropdownMenuBox`). Populate with initial units (g, ml, pc, etc.). ✅
-   [x] **UI (`AddFoodScreen`):** Add an optional `TextField` for notes/recipe. ✅
-   [x] **UI (`AddFoodScreen`):** Create a `MealSelector` Composable (e.g., Row of Buttons or Dropdown) and integrate it. ✅
-   [x] **UI (`AddFoodScreen`):** Add a `Button` Composable for saving the entry. ✅
-   [x] **UI Connection:** Connect `AddFoodScreen` input fields' state to the ViewModel's state management (or directly trigger intents). ✅
-   [x] **UI Connection:** Trigger `Intent.SaveEntry` from the Save Button's `onClick` lambda, passing the current input data. ✅
-   [x] **Navigation (Basic):** Set up basic navigation to be able to reach `AddFoodScreen` (even if temporary from MainActivity initially). ✅

### Testing (To Be Done)

-   [ ] **Unit Test: `AddEntryViewModel`**
    -   [ ] Verify successful save (`SaveEntry` intent) inserts correct data via repository.
    -   [ ] Verify save fails when required fields are blank/invalid (e.g., name, zero quantity).
    -   [ ] Verify state updates correctly for each `UpdateX` intent (e.g., `UpdateQuantity`, `UpdateUnit`).
    -   [ ] Verify `isLoading` state is true during save and false after.
    -   [ ] Verify `EntrySavedSuccessfully` effect is sent on success.
    -   [ ] Verify `ShowError` effect is sent on validation failure or repository error.
-   [ ] **Unit Test: `FoodRepositoryImpl`**
    -   [ ] Verify `addLoggedEntry` correctly inserts `LoggedEntryEntity`.
    -   [ ] Verify `addLoggedEntry` creates a new `FoodItemEntity` if it doesn't exist.
    -   [ ] Verify `addLoggedEntry` increments frequency of existing `FoodItemEntity`.
    -   [ ] Verify `addLoggedEntry` handles potential race conditions or errors during insertion gracefully.
-   [ ] **Instrumentation Test: `AddFoodScreen`**
    -   [ ] Verify all input fields are present and editable.
    -   [ ] Verify dropdowns (`Unit`, `Meal`) display options and update state on selection.
    -   [ ] Verify typing in fields updates the underlying ViewModel state.
    -   [ ] Verify clicking 'Save' triggers the correct ViewModel intent.
    -   [ ] Verify error messages/states are shown correctly on validation failure (if UI handles this).
    -   [ ] Verify successful save navigates away or clears the form (depending on desired behavior).