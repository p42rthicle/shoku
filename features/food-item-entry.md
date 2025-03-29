# Feature: Food Item Entry

This document outlines the tasks required to implement the basic food item entry feature.

## To-Do List

-   [ ] **Project Setup:** Ensure necessary dependencies (Compose, Room, Coroutines, ViewModel, DI framework) are added to `build.gradle` files.
-   [ ] **Data Model:** Define the `FoodItem` data class (representing the concept of a food) in the domain/common layer.
-   [ ] **Data Model:** Define the `LoggedEntry` data class (representing an instance of eating a food) in the domain/common layer.
-   [ ] **Data Model:** Define the `Meal` data class/enum (e.g., Breakfast, Lunch, Dinner) in the domain/common layer.
-   [ ] **Database:** Define `FoodItemEntity` Room Entity.
-   [ ] **Database:** Define `LoggedEntryEntity` Room Entity (including fields for timestamp, meal type, quantity, unit, calories, protein, and potentially a foreign key to `FoodItemEntity` or store denormalized food details).
-   [ ] **Database:** Define `FoodItemDao` interface with methods for inserting and possibly querying `FoodItemEntity`.
-   [ ] **Database:** Define `LoggedEntryDao` interface with method for inserting `LoggedEntryEntity`.
-   [ ] **Database:** Create `AppDatabase` class extending `RoomDatabase`, listing entities and DAOs.
-   [ ] **Dependency Injection:** Set up DI modules (Hilt/Koin) to provide `AppDatabase`, `FoodItemDao`, and `LoggedEntryDao`.
-   [ ] **Repository:** Define `FoodRepository` interface with methods like `addLoggedEntry(entry: LoggedEntry)` and `getOrCreateFoodItem(name: String, calories: Double, protein: Double): FoodItem`.
-   [ ] **Repository:** Implement `FoodRepositoryImpl` injecting DAOs and implementing the interface methods (handle mapping between domain models and entities).
-   [ ] **Dependency Injection:** Add `FoodRepository` to DI modules.
-   [ ] **ViewModel (MVI):** Define `AddFoodContract` containing `UiState`, `Intent`, and `Effect` classes for the Add Food screen.
-   [ ] **ViewModel (MVI):** Create `AddEntryViewModel` implementing `ViewModel()`.
-   [ ] **ViewModel (MVI):** Inject `FoodRepository` (or a `LogFoodItemUseCase`) into `AddEntryViewModel`.
-   [ ] **ViewModel (MVI):** Implement MVI logic: handle `Intent.SaveEntry`, call repository/use case, update `UiState` (e.g., show loading, success/error message via `Effect`).
-   [ ] **UI (`AddFoodScreen`):** Create the basic Composable structure for `AddFoodScreen`.
-   [ ] **UI (`AddFoodScreen`):** Add `TextField` Composables for Name, Quantity, Calories, Protein.
-   [ ] **UI (`AddFoodScreen`):** Add a dropdown/selector Composable for Unit (e.g., using `ExposedDropdownMenuBox`). Populate with initial units (g, ml, pc, etc.).
-   [ ] **UI (`AddFoodScreen`):** Add an optional `TextField` for notes/recipe.
-   [ ] **UI (`AddFoodScreen`):** Create a `MealSelector` Composable (e.g., Row of Buttons or Dropdown) and integrate it.
-   [ ] **UI (`AddFoodScreen`):** Add a `Button` Composable for saving the entry.
-   [ ] **UI Connection:** Connect `AddFoodScreen` input fields' state to the ViewModel's state management (or directly trigger intents).
-   [ ] **UI Connection:** Trigger `Intent.SaveEntry` from the Save Button's `onClick` lambda, passing the current input data.
-   [ ] **Navigation (Basic):** Set up basic navigation to be able to reach `AddFoodScreen` (even if temporary from MainActivity initially). 