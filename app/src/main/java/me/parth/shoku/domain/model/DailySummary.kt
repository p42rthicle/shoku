package me.parth.shoku.domain.model

import java.time.LocalDate

/**
 * Represents the summary totals for a single day.
 */
data class DailySummary(
    val date: LocalDate,
    val totalCalories: Double,
    val totalProtein: Double
) 