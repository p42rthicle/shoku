package me.parth.shoku.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import me.parth.shoku.data.local.dao.FoodItemDao
import me.parth.shoku.data.local.dao.LoggedEntryDao
import me.parth.shoku.data.local.entity.FoodItemEntity
import me.parth.shoku.data.local.entity.LoggedEntryEntity
import me.parth.shoku.domain.model.FoodItem
import me.parth.shoku.domain.model.LoggedEntry
import me.parth.shoku.domain.repository.FoodRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of FoodRepository.
 * Handles data operations using DAOs and maps between domain models and entities.
 */
@Singleton
class FoodRepositoryImpl @Inject constructor(
    private val foodItemDao: FoodItemDao,
    private val loggedEntryDao: LoggedEntryDao
) : FoodRepository {

    override suspend fun addLoggedEntry(entry: LoggedEntry) {
        // Ensure database operations run on an appropriate background thread
        withContext(Dispatchers.IO) {
            // 1. Check if a FoodItem exists with this name, or create/update it
            var foodItemId: Int?
            val existingFoodItem = foodItemDao.getFoodItemByName(entry.foodName)

            if (existingFoodItem != null) {
                // Food exists, increment frequency and get its ID
                foodItemId = existingFoodItem.id
                foodItemDao.incrementFrequency(foodItemId)
                // Optional: Update default calories/protein if entry provides different values?
                // For now, we just reuse the existing item and increment frequency.
            } else {
                // Food doesn't exist, create a new FoodItemEntity
                // Estimate base calories/protein per unit. Handle division by zero.
                val baseCalories = if (entry.quantity > 0) entry.calories / entry.quantity else 0.0
                val baseProtein = if (entry.quantity > 0) entry.protein / entry.quantity else 0.0

                val newFoodItem = FoodItemEntity(
                    name = entry.foodName.trim(), // Ensure name is trimmed
                    calories = baseCalories,
                    protein = baseProtein,
                    defaultUnit = entry.unit,
                    frequency = 1 // Start frequency at 1
                )
                val newId = foodItemDao.insertFoodItem(newFoodItem)
                // If insert succeeded (not -1), get the ID. Otherwise, maybe log error?
                foodItemId = if (newId != -1L) newId.toInt() else {
                    // Attempt to retrieve again in case of race condition or if IGNORE failed silently
                    // (though getFoodItemByName should be reliable here)
                    foodItemDao.getFoodItemByName(entry.foodName)?.id
                }
            }

            // 2. Create the LoggedEntryEntity using the determined foodItemId
            val loggedEntryEntity = entry.toEntity(foodItemId)

            // 3. Insert the LoggedEntryEntity
            loggedEntryDao.insertLoggedEntry(loggedEntryEntity)
        }
    }

    override fun getEntriesForDate(date: LocalDate): Flow<List<LoggedEntry>> {
        return loggedEntryDao.getEntriesForDate(date.toString()) // Convert LocalDate to String for DAO
            .map { entityList -> // Map the list of entities
                entityList.map { it.toDomainModel() } // Map each entity to domain model
            }
            .flowOn(Dispatchers.IO) // Ensure flow collection happens off the main thread
    }

    override fun getFoodSuggestions(query: String): Flow<List<FoodItem>> {
        // Add wildcard '%' for LIKE query if query is not blank
        val effectiveQuery = if (query.isBlank()) "" else query
        return foodItemDao.getFoodItemSuggestions(effectiveQuery)
            .map { entityList -> // Map the list of entities
                entityList.map { it.toDomainModel() } // Map each entity to domain model
            }
            .flowOn(Dispatchers.IO) // Ensure flow collection happens off the main thread
    }
}

// --- Mappers --- (Could be in separate files: e.g., data/local/mapper/FoodMappers.kt)

fun LoggedEntryEntity.toDomainModel(): LoggedEntry {
    return LoggedEntry(
        id = this.id,
        foodName = this.foodName,
        quantity = this.quantity,
        unit = this.unit,
        calories = this.calories,
        protein = this.protein,
        date = LocalDate.parse(this.date), // Parse String back to LocalDate
        meal = this.meal,
        notes = this.notes,
        foodItemId = this.foodItemId
    )
}

fun LoggedEntry.toEntity(foodItemId: Int?): LoggedEntryEntity {
    return LoggedEntryEntity(
        id = this.id, // ID is 0 for new entries, Room generates it
        foodName = this.foodName.trim(), // Ensure name is trimmed
        quantity = this.quantity,
        unit = this.unit,
        calories = this.calories,
        protein = this.protein,
        date = this.date.toString(), // Convert LocalDate to String for storage
        meal = this.meal,
        notes = this.notes,
        foodItemId = foodItemId // Use the determined foodItemId from repository logic
    )
}

fun FoodItemEntity.toDomainModel(): FoodItem {
    return FoodItem(
        id = this.id,
        name = this.name,
        calories = this.calories,
        protein = this.protein,
        defaultUnit = this.defaultUnit,
        frequency = this.frequency
    )
}

// FoodItem domain model to FoodItemEntity mapping is handled inline within the addLoggedEntry logic
// when creating a *new* food item, so a dedicated mapper function isn't strictly needed here.
 