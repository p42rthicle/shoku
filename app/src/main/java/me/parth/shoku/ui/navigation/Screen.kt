package me.parth.shoku.ui.navigation

/**
 * Defines the available screens/destinations in the application for navigation.
 */
sealed class Screen(val route: String) {
    object Home : Screen("home") // Placeholder for the main screen
    object AddFood : Screen("add_food")
    object DailyTargets : Screen("daily_targets") // Add route for target settings
    // Add other screens like History, Settings etc. later
} 