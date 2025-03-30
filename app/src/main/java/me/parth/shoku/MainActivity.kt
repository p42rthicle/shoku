package me.parth.shoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import dagger.hilt.android.AndroidEntryPoint
import me.parth.shoku.ui.feature.addfood.AddFoodScreen
import me.parth.shoku.ui.feature.home.HomeScreen
import me.parth.shoku.ui.navigation.Screen
import me.parth.shoku.ui.theme.ShokuTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShokuAppRoot()
        }
    }
}

@Composable
fun ShokuAppRoot() {
    ShokuTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToAddEntry = { navController.navigate(Screen.AddFood.route) },
                    onNavigateToSettings = { navController.navigate(Screen.DailyTargets.route) }
                )
            }
            composable(Screen.AddFood.route) {
                AddFoodScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.DailyTargets.route) {
                PlaceholderDailyTargetScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderDailyTargetScreen(modifier: Modifier = Modifier, onNavigateBack: () -> Unit) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Set Daily Targets") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Daily Target Settings Placeholder")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ShokuTheme {
        PlaceholderDailyTargetScreen(onNavigateBack = {})
    }
}