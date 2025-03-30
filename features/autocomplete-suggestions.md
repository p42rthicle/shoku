# Feature: Autocomplete Suggestions

This document outlines the tasks required to implement the food name autocomplete suggestion feature in the Add Food screen.

## To-Do List

-   [x] **Repository:** Ensure `FoodRepository` interface has `getFoodSuggestions(query: String): Flow<List<FoodItem>>`. **(Done)**
-   [x] **Repository:** Ensure `FoodRepositoryImpl` implements `getFoodSuggestions` by calling `FoodItemDao.getFoodItemSuggestions(query)` and mapping results. **(Done)**
-   [x] **DAO:** Ensure `FoodItemDao` has the `getFoodItemSuggestions(query: String)` method with a `LIKE '%' || :query || '%'` query and `ORDER BY frequency DESC`. **(Done)**
-   [x] **ViewModel (Contract):** Update `AddFoodContract.UiState` to include a field for suggestions (e.g., `suggestions: List<FoodItem> = emptyList()`). **(Done)**
-   [x] **ViewModel (Contract):** Add necessary `Intent`s for suggestion selection (e.g., `SelectSuggestion`). **(Done)**
-   [x] **ViewModel:** Update `AddEntryViewModel`: **(Done)**
    -   [x] Trigger repository's `getFoodSuggestions` whenever `UiState.foodName` changes (using `viewModelScope`, `debounce`, `flatMapLatest` on the `foodName` state changes).
    -   [x] Use `flatMapLatest` to handle rapid changes and cancel previous requests.
    -   [x] Update the `UiState.suggestions` list based on the Flow results from the repository.
    -   [x] Handle potential errors during the suggestion fetching.
    -   [x] Handle the `SelectSuggestion` intent to update multiple fields (`foodName`, `calories`, `protein`, `unit`) and clear suggestions.
-   [x] **UI (`AddFoodScreen` / `AddFoodForm`):**
    -   [x] Modify the `Food Name` `OutlinedTextField` area to display the list of suggestions (`uiState.suggestions`) below it when the list is not empty (e.g., using a `Column` or `LazyColumn` below the TextField).
    -   [x] Make each suggestion item clickable.
    -   [x] When a suggestion item is clicked, trigger the `Intent.SelectSuggestion(foodItem: FoodItem)`.
-   [x] **Testing:** Add tests for suggestion logic in ViewModel and Repository (optional but recommended).