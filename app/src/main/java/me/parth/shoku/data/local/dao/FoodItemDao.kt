package me.parth.shoku.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import me.parth.shoku.data.local.entity.FoodItemEntity

@Dao
interface FoodItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE) // Ignore if food item name already exists
    suspend fun insertFoodItem(foodItem: FoodItemEntity): Long // Returns row ID or -1 if ignored

    @Query("SELECT * FROM food_items WHERE name = :name LIMIT 1")
    suspend fun getFoodItemByName(name: String): FoodItemEntity?

    @Update
    suspend fun updateFoodItem(foodItem: FoodItemEntity)

    @Query("SELECT * FROM food_items ORDER BY frequency DESC, name ASC")
    fun getAllFoodItemsSortedByFrequency(): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM food_items WHERE name LIKE :query || '%' ORDER BY frequency DESC, name ASC LIMIT 10")
    fun getFoodItemSuggestions(query: String): Flow<List<FoodItemEntity>>

    // We might need a way to increment frequency later
    @Query("UPDATE food_items SET frequency = frequency + 1 WHERE id = :id")
    suspend fun incrementFrequency(id: Int)
} 