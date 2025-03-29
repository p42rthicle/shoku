package me.parth.shoku.data.local.converter

import androidx.room.TypeConverter
import me.parth.shoku.domain.model.Meal

/**
 * Room TypeConverter for the Meal enum.
 * Converts Meal enum to its String name for storage and back.
 */
class MealConverter {
    @TypeConverter
    fun fromMeal(meal: Meal?): String? {
        return meal?.name
    }

    @TypeConverter
    fun toMeal(name: String?): Meal? {
        return name?.let { Meal.valueOf(it) }
    }
} 