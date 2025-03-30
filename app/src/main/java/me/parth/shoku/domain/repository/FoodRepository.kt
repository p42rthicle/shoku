package me.parth.shoku.domain.repository

import kotlinx.coroutines.flow.Flow
import me.parth.shoku.domain.model.DailySummary
import me.parth.shoku.domain.model.FoodItem
import me.parth.shoku.domain.model.LoggedEntry
import java.time.LocalDate

/**
 * Interface defining the contract for data operations related to food and logging.
 */
interface FoodRepository {

    /**
     * Adds a new food log entry to the database.
     * If the food item doesn't exist in the suggestions table, it might create it.
     * It should also handle updating the frequency of the base FoodItem.
     */
    suspend fun addLoggedEntry(entry: LoggedEntry)

    /**
     * Deletes a specific logged entry.
     */
    suspend fun deleteLoggedEntry(entry: LoggedEntry)

    /**
     * Updates an existing logged entry.
     */
    suspend fun updateLoggedEntry(entry: LoggedEntry)

    /**
      * Gets a single logged entry by its ID.
      */
    suspend fun getEntryById(id: Long): LoggedEntry?

    /**
     * Retrieves a flow of logged entries for a specific date.
     */
    fun getEntriesForDate(date: LocalDate): Flow<List<LoggedEntry>>

    /**
     * Retrieves a flow of food item suggestions based on a query string.
     * Results should be ranked by frequency.
     */
    fun getFoodSuggestions(query: String): Flow<List<FoodItem>>

    // --- Daily Targets ---
    suspend fun saveCalorieTarget(target: Double)
    suspend fun getCalorieTarget(): Double // Returns default if not set
    suspend fun saveProteinTarget(target: Double)
    suspend fun getProteinTarget(): Double // Returns default if not set

    // --- History --- 
    fun getDailySummaries(): Flow<List<DailySummary>>
    fun getAllLoggedEntries(): Flow<List<LoggedEntry>>

    // Add other methods as needed (e.g., get daily summary, get history)
} 