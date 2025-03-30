package me.parth.shoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import dagger.hilt.android.AndroidEntryPoint
import me.parth.shoku.ui.feature.addfood.AddFoodContract
import me.parth.shoku.ui.feature.addfood.AddFoodScreen
import me.parth.shoku.ui.feature.allentries.AllEntriesScreen
import me.parth.shoku.ui.feature.history.HistoryScreen
import me.parth.shoku.ui.feature.home.HomeScreen
import me.parth.shoku.ui.feature.home.HomeViewModel
import me.parth.shoku.ui.feature.home.HomeContract
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
            composable(
                route = Screen.Home.route,
                arguments = Screen.Home.arguments
            ) { backStackEntry ->
                val viewModel: HomeViewModel = hiltViewModel(backStackEntry)
                val homeState = viewModel.uiState.collectAsState().value

                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToAddEntry = {
                        navController.navigate(Screen.AddFood.createRoute(homeState.selectedDate.toString()))
                    },
                    onNavigateToSettings = { navController.navigate(Screen.DailyTargets.route) },
                    onNavigateToHistory = { navController.navigate(Screen.History.route) }
                )
            }
            composable(
                route = Screen.AddFood.route,
                arguments = Screen.AddFood.arguments
            ) {
                AddFoodScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.DailyTargets.route) {
                val homeViewModel: HomeViewModel = hiltViewModel()
                DailyTargetScreen(
                    viewModel = homeViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.History.route) {
                HistoryScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDayDetail = { date ->
                        navController.navigate(Screen.Home.createRoute(date.toString()))
                    },
                    onNavigateToAllEntries = { navController.navigate(Screen.AllEntries.route) }
                )
            }
            composable(Screen.AllEntries.route) {
                AllEntriesScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyTargetScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect {
            when(it) {
                is HomeContract.Effect.TargetsSavedSuccessfully -> {
                    snackbarHostState.showSnackbar("Targets Saved!")
                }
                is HomeContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar("Error: ${it.message}")
                }
                else -> { /* Ignore other effects like navigation */ }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Enter your daily goals:")

            OutlinedTextField(
                value = uiState.calorieTargetInput,
                onValueChange = { viewModel.onIntent(HomeContract.Intent.UpdateCalorieTargetInput(it)) },
                label = { Text("Calorie Target (kcal)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.proteinTargetInput,
                onValueChange = { viewModel.onIntent(HomeContract.Intent.UpdateProteinTargetInput(it)) },
                label = {
                    Text("Protein Target (grams)")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.onIntent(HomeContract.Intent.SaveTargets) },
                enabled = !uiState.isSavingTarget,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isSavingTarget) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("Save Targets")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ShokuTheme {
        Text("Preview requires ViewModel setup")
    }
}