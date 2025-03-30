package me.parth.shoku.ui.feature.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import me.parth.shoku.domain.model.LoggedEntry
import me.parth.shoku.domain.model.Meal // Assuming Meal enum is accessible
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToAddEntry: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToEditEntry: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current

    // Observe lifecycle events
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Refresh ALL data for the selected date when screen resumes
                viewModel.onIntent(HomeContract.Intent.LoadDataForSelectedDate)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        // Cleanup observer on dispose
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Handle effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeContract.Effect.NavigateToTargetSettings -> onNavigateToSettings()
                is HomeContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }
                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Shoku Diary") },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Filled.Refresh, contentDescription = "History")
                    }
                    IconButton(onClick = { viewModel.onIntent(HomeContract.Intent.OpenTargetSettings) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddEntry) {
                Icon(Icons.Default.Add, contentDescription = "Add Food Entry")
            }
        }
    ) { paddingValues ->
        HomeScreenContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onIntent = viewModel::onIntent,
            onNavigateToEditEntry = onNavigateToEditEntry
        )
    }
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    uiState: HomeContract.UiState,
    onIntent: (HomeContract.Intent) -> Unit,
    onNavigateToEditEntry: (Long) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DateSelector(
            selectedDate = uiState.selectedDate,
            onDateChange = { newDate -> onIntent(HomeContract.Intent.ChangeDate(newDate)) }
        )

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else if (uiState.error != null) {
            Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
            // TODO: Add a retry button?
        } else {
            DailySummaryDisplay(
                totalCalories = uiState.totalCalories,
                totalProtein = uiState.totalProtein
            )

            TargetProgressIndicator(
                totalCalories = uiState.totalCalories,
                totalProtein = uiState.totalProtein,
                calorieTarget = uiState.calorieTarget,
                proteinTarget = uiState.proteinTarget
            )

            DailyLogList(
                entries = uiState.dailyEntries,
                onIntent = onIntent,
                onNavigateToEditEntry = onNavigateToEditEntry
            )
        }
    }
}

// --- Placeholder Composable Components ---

@Composable
fun DateSelector(selectedDate: LocalDate, onDateChange: (LocalDate) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = { onDateChange(selectedDate.minusDays(1)) }) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous Day")
        }
        Text(
            text = selectedDate.format(DateTimeFormatter.ofPattern("EEE, MMM d, yyyy")),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        IconButton(onClick = { onDateChange(selectedDate.plusDays(1)) }) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Day")
        }
    }
}

@Composable
fun DailySummaryDisplay(totalCalories: Double, totalProtein: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround // Use SpaceAround for better distribution
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "${totalCalories.toInt()}", style = MaterialTheme.typography.headlineMedium)
                Text(text = "kcal", style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "${totalProtein.toInt()}", style = MaterialTheme.typography.headlineMedium)
                Text(text = "grams protein", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun TargetProgressIndicator(
    totalCalories: Double,
    totalProtein: Double,
    calorieTarget: Double,
    proteinTarget: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Daily Goal Progress", style = MaterialTheme.typography.titleMedium)
            // Calorie Progress
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Calories", style = MaterialTheme.typography.bodyMedium)
                    Text("${totalCalories.toInt()} / ${calorieTarget.toInt()} kcal", style = MaterialTheme.typography.bodyMedium)
                }
                LinearProgressIndicator(
                    progress = { (totalCalories / calorieTarget.coerceAtLeast(1.0)).toFloat() },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)) // Make it thicker and rounded
                )
            }
            // Protein Progress
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Protein", style = MaterialTheme.typography.bodyMedium)
                    Text("${totalProtein.toInt()} / ${proteinTarget.toInt()} g", style = MaterialTheme.typography.bodyMedium)
                }
                LinearProgressIndicator(
                    progress = { (totalProtein / proteinTarget.coerceAtLeast(1.0)).toFloat() },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DailyLogList(
    entries: List<LoggedEntry>,
    onIntent: (HomeContract.Intent) -> Unit,
    onNavigateToEditEntry: (Long) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Today's Log", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))

        if (entries.isEmpty()) {
            Text(
                text = "No entries logged for this date yet.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            )
        } else {
            // Group entries by meal
            val groupedEntries = entries.groupBy { it.meal }

            // Use LazyColumn to display the list efficiently
            LazyColumn(
                modifier = Modifier.weight(1f), // Use weight only if inside a Column/Row that defines size
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Iterate through each meal group
                groupedEntries.forEach { (meal, entriesForMeal) ->
                    // Sticky header for the meal name (optional, but nice)
                    stickyHeader {
                        Surface(
                            modifier = Modifier.fillParentMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceVariant // Distinguish header
                        ) {
                            Text(
                                text = meal.name.lowercase().replaceFirstChar { it.titlecase() },
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // List items for the current meal with swipe-to-delete
                    items(entriesForMeal, key = { it.id }) { entry ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { dismissValue ->
                                if (dismissValue == SwipeToDismissBoxValue.EndToStart || dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                                    onIntent(HomeContract.Intent.DeleteEntry(entry))
                                    true // Indicate dismiss action is consumed
                                } else {
                                    false
                                }
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = { // Content shown behind the item during swipe
                                val direction = dismissState.targetValue // Use targetValue
                                val alignment = when(direction) {
                                     SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                                     SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                                     SwipeToDismissBoxValue.Settled -> Alignment.Center // Or Start for consistency
                                }
                                // Don't draw a full background color, just the icon
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        // Optional: Add slight background tint on swipe if desired
                                        // .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f))
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = alignment
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete Icon",
                                        tint = MaterialTheme.colorScheme.error // Make icon red
                                    )
                                }
                            }
                        ) {
                            FoodLogItem(
                                entry = entry,
                                onClick = { onNavigateToEditEntry(entry.id) }
                            )
                        }
                        Divider(thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun FoodLogItem(entry: LoggedEntry, onClick: () -> Unit) {
    // Simple display for a single logged food item
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = entry.foodName, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "${entry.quantity} ${entry.unit}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = "${entry.calories.toInt()} kcal", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "${entry.protein.toInt()} g pro",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
} 