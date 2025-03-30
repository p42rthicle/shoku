package me.parth.shoku.ui.feature.addfood

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import me.parth.shoku.domain.model.FoodItem
import me.parth.shoku.domain.repository.FoodRepository
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.flow.flow // For error testing

@ExperimentalCoroutinesApi
class AddEntryViewModelTest {

    // Rules for JUnit, MockK, and synchronous execution
    @get:Rule
    val mockkRule = MockKRule(this) // Initializes mocks annotated with @MockK

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule() // Executes tasks synchronously

    // Mock the repository dependency
    @MockK
    private lateinit var foodRepository: FoodRepository

    // SUT (System Under Test)
    private lateinit var viewModel: AddEntryViewModel

    // Test dispatcher for controlling coroutines
    private val testDispatcher = StandardTestDispatcher() // Or UnconfinedTestDispatcher

    @Before
    fun setUp() {
        // Set the main dispatcher to the test dispatcher before each test
        Dispatchers.setMain(testDispatcher)
        // Create the ViewModel instance with the mock repository
        viewModel = AddEntryViewModel(foodRepository)
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher after each test
        Dispatchers.resetMain()
    }

    // --- Test Cases will go here ---

    // Example test structure (to be filled in)
    @Test
    fun `test suggestion fetching`() = runTest {
        // TODO: Implement test
    }

    @Test
    fun `suggestions update when foodName changes with valid query`() =
        runTest(testDispatcher) { // Use testDispatcher
            // Arrange
            val query = "apple"
            val suggestions = listOf(
                FoodItem(1, "apple", 95.0, 0.5, "piece", 10),
                FoodItem(2, "apple pie", 411.0, 4.0, "slice", 5)
            )
            coEvery { foodRepository.getFoodSuggestions(query) } returns flowOf(suggestions) // Mock repository response

            // Act & Assert using Turbine
            viewModel.uiState.test {
                // Initial state
                assertEquals(emptyList<FoodItem>(), awaitItem().suggestions)

                // User types query
                viewModel.onIntent(AddFoodContract.Intent.UpdateFoodName(query))

                // Skip potential immediate state emission due to name update (if any)
                // We are primarily interested in the state *after* suggestions are loaded.
                // Alternatively, you could awaitItem() here and assert the name changed but suggestions are still empty.
                skipItems(1) // Or awaitItem() and assert intermediate state

                // Advance time past debounce (300ms + buffer)
                testDispatcher.scheduler.advanceTimeBy(301.milliseconds)

                // Assert suggestions are updated in the next emission
                val finalState = awaitItem()
                assertEquals(query, finalState.foodName) // Verify name is still correct
                assertEquals(suggestions, finalState.suggestions)

                // Ensure no other emissions unexpectedly
                expectNoEvents()
            }

            // Verify
            coVerify { foodRepository.getFoodSuggestions(query) } // Verify repository was called
        }

    @Test
    fun `suggestions do not update when foodName query is too short`() = runTest(testDispatcher) {
        // Arrange
        val shortQuery = "a" // Less than 2 characters

        // Act & Assert
        viewModel.uiState.test {
            assertEquals(emptyList<FoodItem>(), awaitItem().suggestions) // Initial state

            viewModel.onIntent(AddFoodContract.Intent.UpdateFoodName(shortQuery))

            // Advance time past debounce, shouldn't matter but good practice
            testDispatcher.scheduler.advanceTimeBy(301.milliseconds)

            // Assert suggestions remain empty
            assertEquals(emptyList<FoodItem>(), expectMostRecentItem().suggestions)
            // or you could check that no new item was emitted after the first awaitItem()
            // expectNoEvents() // This would work if the state object itself doesn't change (only name)
        }

        // Verify
        coVerify(exactly = 0) { foodRepository.getFoodSuggestions(any()) } // Ensure repository was NOT called
    }

    @Test
    fun `test suggestion selection`() = runTest {
        // TODO: Implement test
    }

    @Test
    fun `test suggestion fetching with short query`() = runTest {
        // TODO: Implement test
    }

    @Test
    fun `selecting a suggestion updates state correctly`() = runTest(testDispatcher) {
        // Arrange
        val suggestion = FoodItem(
            id = 5,
            name = "Banana",
            calories = 105.0,
            protein = 1.3,
            defaultUnit = "medium",
            frequency = 20
        )
        // **Add this mock**: Handle the secondary call triggered by observeFoodNameChanges
        coEvery { foodRepository.getFoodSuggestions(suggestion.name) } returns flowOf(emptyList()) // Or flowOf(listOf(suggestion)) if needed

        // Act & Assert
        viewModel.uiState.test {
            awaitItem() // Consume initial state

            // Send SelectSuggestion intent
            viewModel.onIntent(AddFoodContract.Intent.SelectSuggestion(suggestion))

            // Assert state updates from the SelectSuggestion intent
            val updatedState = awaitItem()
            assertEquals(suggestion.name, updatedState.foodName)
            assertEquals("1", updatedState.quantity) // Quantity defaults to "1"
            assertEquals(suggestion.defaultUnit, updatedState.unit)
            assertEquals(suggestion.calories.toString(), updatedState.calories)
            assertEquals(suggestion.protein.toString(), updatedState.protein)
            assertEquals(
                emptyList<FoodItem>(),
                updatedState.suggestions
            ) // Suggestions cleared by SelectSuggestion

            // Advance time past debounce to allow the secondary getFoodSuggestions call to potentially complete
            // Even though we mocked it, we need to ensure the test doesn't finish too early if using StandardTestDispatcher
            testDispatcher.scheduler.advanceTimeBy(301.milliseconds)

            // Check if another state emission occurred due to the secondary call (unlikely to change asserted fields)
            // If the secondary call returned suggestions, the state might emit again.
            // Since we mocked it to return emptyList, and suggestions are already empty,
            // there might not be another distinct emission. expectNoEvents() might be okay.
            // Or use expectMostRecentItem() and re-assert if needed.
            expectNoEvents() // Assuming the secondary call doesn't cause a *distinct* state change here
        }
    }
}