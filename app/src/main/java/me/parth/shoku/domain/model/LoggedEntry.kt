package me.parth.shoku.domain.model

import java.time.LocalDate

/**
 * Represents a specific instance of a food item being logged by the user.
 *
 * @param id Unique identifier for this log entry.
 * @param foodName Name of the food logged (denormalized for easier display).
 * @param quantity Quantity consumed.
 * @param unit Unit of the quantity (e.g., "g", "ml", "pc", "katori").
 * @param calories Calories for the logged quantity.
 * @param protein Protein for the logged quantity.
 * @param date Date the food was logged.
 * @param meal Type of meal (e.g., Breakfast, Lunch).
 * @param notes Optional user notes.
 * @param foodItemId Optional foreign key to the FoodItem if this log was based on a known item.
 */
data class LoggedEntry(
    val id: Long = 0, // Change to Long (default 0 for new entries)
    val foodName: String,
    val quantity: Double,
    val unit: String,
    val calories: Double,
    val protein: Double,
    val date: LocalDate = LocalDate.now(),
    val meal: Meal,
    val notes: String? = null,
    val foodItemId: Int? = null // Optional link to FoodItem
) 