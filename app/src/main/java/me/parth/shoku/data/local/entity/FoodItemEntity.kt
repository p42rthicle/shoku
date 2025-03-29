package me.parth.shoku.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Entity representing a distinct food item in the database.
 */
@Entity(
    tableName = "food_items",
    indices = [Index(value = ["name"], unique = true)] // Ensure food names are unique
)
data class FoodItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val calories: Double, // Per standard unit
    val protein: Double, // Per standard unit
    val defaultUnit: String? = null,
    val frequency: Int = 0
) 