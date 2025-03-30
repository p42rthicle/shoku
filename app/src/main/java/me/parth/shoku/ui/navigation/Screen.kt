package me.parth.shoku.ui.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

/**
 * Defines the available screens/destinations in the application for navigation.
 */
sealed class Screen(val route: String) {
    // Define route with optional argument for date (ISO-8601 string)
    object Home : Screen("home?date={date}") {
        // Function to create the route with a specific date
        fun createRoute(date: String?) = if (date != null) "home?date=$date" else "home"
        // Argument definition
        val dateArg = "date"
        val arguments = listOf(
            navArgument(dateArg) {
                type = NavType.StringType
                nullable = true
                defaultValue = null // Default to null (meaning current date)
            }
        )
    }
    // Add optional date argument to AddFood route
    object AddFood : Screen("add_food?date={date}") {
        fun createRoute(date: String?) = if (date != null) "add_food?date=$date" else "add_food"
        val dateArg = "date"
        val arguments = listOf(
            navArgument(dateArg) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    }
    object DailyTargets : Screen("daily_targets") // Add route for target settings
    object History : Screen("history") // Add route for history screen
    object AllEntries : Screen("all_entries") // Add route for all entries screen
    // Add other screens like History, Settings etc. later
} 