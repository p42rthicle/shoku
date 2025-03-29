package me.parth.shoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import dagger.hilt.android.AndroidEntryPoint
import me.parth.shoku.ui.feature.addfood.AddFoodScreen
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
                PlaceholderHomeScreen(
                    modifier = Modifier.fillMaxSize(),
                    onNavigateToAddFood = { navController.navigate(Screen.AddFood.route) }
                )
            }
            composable(Screen.AddFood.route) {
                AddFoodScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun PlaceholderHomeScreen(modifier: Modifier = Modifier, onNavigateToAddFood: () -> Unit) {
    Scaffold(modifier = modifier) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Home Screen Placeholder")
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onNavigateToAddFood) {
                Text("Go to Add Food")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ShokuTheme {
        PlaceholderHomeScreen(onNavigateToAddFood = {})
    }
}