package me.parth.shoku.ui.feature.addfood

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
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
    fun `suggestions update when foodName changes with valid query`() = runTest(testDispatcher) { // Use testDispatcher
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

            // Advance time past debounce (300ms + buffer)
            testDispatcher.scheduler.advanceTimeBy(301.milliseconds)

            // Assert suggestions are updated
            assertEquals(suggestions, awaitItem().suggestions)

            // Ensure no other emissions unexpectedly
            expectNoEvents()
        }

        // Verify
        coVerify { foodRepository.getFoodSuggestions(query) } // Verify repository was called
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
    fun `test suggestion fetching error`() = runTest {
         // TODO: Implement test
    }
} 