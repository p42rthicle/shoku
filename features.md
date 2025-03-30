# Food Log App Features and Implementation Plan

This document outlines the core features of the Food Log App and a potential implementation plan, focusing on modularity and reusable components.

## Core Features

1.  **Food Item Entry:**
    *   Users can add food items with details:
        *   Name (e.g., "Milk", "Bread", "Butter")
        *   Quantity (e.g., 250, 2, 10)
        *   Unit (e.g., "ml", "pc", "g")
        *   Calories (e.g., 170)
        *   Protein (e.g., 8g)
    *   Simple text-based input for logging.

2.  **Autocomplete Suggestions:**
    *   As the user types a food name, suggest previously logged items.
    *   Suggestions ranked by frequency of use.

3.  **Meal Tracking:**
    *   Log food items under specific meals (e.g., Breakfast, Lunch, Dinner, Snacks).
    *   Users can add/edit meal names dynamically.

4.  **Daily Summary & Target:**
    *   Display total calories and protein consumed for the current day.
    *   Users can set and edit daily calorie and protein targets.
    *   Visual indication of progress towards targets.

5.  **Day-wise History:**
    *   List view showing past days with their total calories and protein.
    *   Clicking a day opens a detailed view.

6.  **Detailed Daily Breakdown:**
    *   Show all meals and food items logged for a selected day.

7.  **Full History:**
    *   Access to the complete log history, not just daily summaries.

## Architecture Overview

This project will adhere to the principles of **Clean Architecture** to promote separation of concerns, testability, and maintainability. The architecture will be structured into layers:

1.  **Presentation Layer:**
    *   **UI:** Built with **Jetpack Compose**.
    *   **State Management:** Implemented using the **MVI (Model-View-Intent)** pattern. ViewModels will manage UI state (`Model`), process user actions (`Intent`), and expose a single state Flow for the UI to observe. UI elements will emit events representing user interactions.
2.  **Domain Layer (Optional but Recommended):**
    *   Contains core business logic, independent of UI and data layers.
    *   May include **Use Cases** (Interactors) that encapsulate specific application operations (e.g., `LogFoodItemUseCase`, `GetDailySummaryUseCase`).
    *   Uses plain Kotlin/Java objects (Models).
3.  **Data Layer:**
    *   Implements the **Repository Pattern** to abstract data sources.
    *   Repositories (`FoodRepository`) manage data retrieval and storage, deciding whether to fetch from local sources (database) or remote sources (if applicable later).
    *   **Local Data Source:** **Room Persistence Library** for on-device storage.
    *   **Data Models:** Separate data entities (`FoodItemEntity`) used by Room, mapped to/from domain models by the Repository.

**Dependency Flow:** Presentation → Domain → Data. Dependencies only point inwards.
**Dependency Injection:** Hilt (or Koin) will be used to manage dependencies between layers.

## Implementation Plan (Step-by-Step)

We will implement the application following the Clean Architecture principles outlined above, using MVI for the presentation layer, Jetpack Compose for the UI, Kotlin Coroutines/Flows, Room, and Hilt/Koin for DI.

**Phase 1: Core Data, Logging & Setup**

1.  **Project Setup:** Create Android project, add necessary dependencies (Compose, Room, Coroutines, Navigation, DI framework).
2.  **Models:** Define data classes: `FoodItem`, `LoggedEntry`, `Meal`, `DailySummary`.
3.  **Room Database:**
    *   Define Entities (`FoodItemEntity`, `LoggedEntryEntity`).
    *   Define DAOs (`FoodItemDao`, `LoggedEntryDao`) with basic CRUD operations (insert, query).
    *   Create the `AppDatabase` class extending `RoomDatabase`.
4.  **Dependency Injection:** Set up Hilt or Koin modules for Database, DAOs, and Repositories.
5.  **Repository:**
    *   Create `FoodRepository` interface and implementation.
    *   Inject DAOs into the repository.
    *   Implement methods to insert `LoggedEntry` and insert/query `FoodItem`.
6.  **ViewModel:**
    *   Create `AddEntryViewModel`.
    *   Inject `FoodRepository`.
    *   Implement functions to handle saving a new `LoggedEntry` and potentially a new `FoodItem` if it doesn't exist.
7.  **UI - Add Food Screen (`AddFoodScreen` - Est: 20 mins):
    *   Build the basic Composable layout for `AddFoodScreen`.
    *   Add input fields (name, quantity, unit, calories, protein).
    *   Add a dropdown/selector for `unit` (e.g., grams, katori, pc, ml).
    *   Add an optional notes section.
    *   Add a Save button.
    *   (Suggestion feature deferred to Phase 2).
8.  **UI - Meal Selection:**
    *   Create a reusable Composable (`MealSelector`) for selecting/creating meals (can be simple initially, perhaps a dropdown or buttons).
    *   Integrate `MealSelector` into `AddFoodScreen`.
9.  **Connect UI & Logic:**
    *   Connect `AddFoodScreen` inputs and button to `AddEntryViewModel` functions.
    *   Ensure data flows from UI -> ViewModel -> Repository -> Database.
    *   **Testing:** Write initial Unit Tests for `AddEntryViewModel` saving logic and Repository insert methods.

**Phase 2: Daily View, Suggestions & Targets**

1.  **Repository Enhancements:**
    *   Add `FoodRepository` methods to query `FoodItem` based on name prefix (for suggestions).
    *   Add `FoodRepository` methods to fetch `LoggedEntry` items for a specific date.
    *   Add `FoodRepository` methods to calculate daily totals (calories, protein) for a date.
    *   Implement logic for suggestion ranking (frequency-based).
2.  **ViewModel - Home:**
    *   Create `HomeViewModel`.
    *   Inject `FoodRepository`.
    *   Add state for selected date, daily summary (calories/protein), daily logs, and targets.
    *   Implement functions to fetch data for the selected date and update state using Flows.
    *   Implement functions to handle date selection changes.
3.  **ViewModel - Add Entry Updates:**
    *   Update `AddEntryViewModel` to fetch food suggestions based on user input using the new repository method.
    *   Add state to hold suggestions.
    *   Implement logic to auto-fill calorie/protein when a suggestion is selected (allowing overrides).
4.  **UI - Add Food Screen Enhancements:**
    *   Integrate suggestion display below the food name input field.
    *   Connect suggestion logic to `AddEntryViewModel`.
5.  **UI - Home Screen (`HomeScreen` - Est: 20 mins):
    *   Build the `HomeScreen` Composable layout.
    *   Implement the top scrollable date selector Composable.
    *   Implement the Pie chart/Progress indicator Composable for calories/protein vs. targets.
    *   Implement the list display for meals and logged items for the selected day.
    *   Add a placeholder for recent/suggested foods (if desired).
    *   Add the FAB to navigate to `AddFoodScreen` (Navigation setup in Phase 3).
    *   Connect the `HomeScreen` UI elements to `HomeViewModel` state (using `collectAsStateWithLifecycle`).
6.  **Daily Targets:**
    *   Add functionality to store/retrieve daily targets (could be simple `SharedPreferences` initially or a dedicated table).
    *   Update `HomeViewModel` to load/save targets.
    *   Create `DailyTargetSetting` UI (can be simple screen or dialog).
    *   **Testing:** Add Unit Tests for ViewModel suggestion logic, daily summary calculation, and target handling. Add UI tests for `AddFoodScreen` suggestion interaction and `HomeScreen` display.

5.  **Testing:**
    *   Write Unit Tests for ViewModels and Repository logic.
    *   Write UI Tests (Instrumentation Tests) for key user flows and Composables.

## Key Considerations for Modularity

*   **Reusable Composables:** Design small, focused Composables (e.g., `CalorieProteinDisplay`, `QuantityUnitInput`, `FoodItemRow`, `DateSelector`, `MealCard`) that perform a single task and can be reused across different screens (`HomeScreen`, `AddFoodScreen`, `HistoryScreen`).
*   **ViewModel Separation:** Each screen (`HomeScreen`, `AddFoodScreen`, `HistoryScreen`) should have its own ViewModel responsible for its specific UI state and business logic, following the MVI pattern. Avoid making ViewModels too large; consider smaller, feature-specific ViewModels if needed.
*   **Repository Pattern:** Centralize all data access logic (database, network, preferences) within Repository classes. ViewModels (or Use Cases) should only interact with Repositories, not directly with data sources like DAOs.
*   **Domain Layer/Use Cases:** Implementing a Domain layer with Use Cases is recommended to encapsulate business logic, making ViewModels leaner and logic more testable and reusable.
*   **Dependency Injection:** Utilize a DI framework like Hilt or Koin consistently to provide dependencies (Repositories, Use Cases, DAOs, Context, etc.) to ViewModels and other classes. This decouples components and makes testing easier.
*   **State Management (MVI):** Follow MVI principles strictly. ViewModels expose a single `StateFlow<UiState>` and handle incoming `Intents` (user actions). The UI observes the state and sends intents.
*   **Navigation:** Define clear navigation routes and use the Navigation Compose library to handle transitions between screens. Pass only necessary identifiers (like IDs or dates) as arguments, not complex objects.
*   **Data Layer Separation:** Keep database entities (`FoodItemEntity`) separate from domain/UI models (`FoodItem`). Use mappers in the Repository layer (or Use Cases) to convert between them.
*   **Feature Modules (Optional):** For larger apps, consider organizing code into separate Gradle modules based on features or layers (e.g., `:feature:log`, `:feature:history`, `:domain`, `:data`, `:core:ui`).