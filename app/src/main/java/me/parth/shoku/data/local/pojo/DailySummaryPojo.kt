package me.parth.shoku.data.local.pojo

/**
 * Plain Old Java Object to hold the results of the daily summary aggregation query.
 * Room can map query results directly to this class.
 * Note: Date is stored as String in the entity, so it will be String here too.
 */
data class DailySummaryPojo(
    val date: String,
    val totalCalories: Double,
    val totalProtein: Double
) 