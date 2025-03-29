package me.parth.shoku.ui.feature.addfood

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.parth.shoku.domain.model.Meal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodScreen(
    viewModel: AddEntryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {

}
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//    val context = LocalContext.current
//    // Handle effects
//    LaunchedEffect(Unit) {
//        viewModel.effect.collect { effect ->
//            when (effect) {
//                is AddFoodContract.Effect.EntrySavedSuccessfully -> {
//                    Toast.makeText(context, \"Entry Saved!\", Toast.LENGTH_SHORT).show()
//                    onNavigateBack() // Navigate back after successful save
//                }
//
//                is AddFoodContract.Effect.ShowError -> {
//                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
//                }
//            }
//        }
//    }
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(\"Add Food Entry\") },
//                    navigationIcon = {
//                        IconButton(onClick = onNavigateBack) {
//                            Icon(Icons.Filled.ArrowBack, contentDescription = \"Navigate back\")
//                        }
//                    }
//                    )
//                }
//            ) {
//                \ paddingValues ->
//                AddFoodForm(
//                    modifier = Modifier
//                        .padding(paddingValues)
//                        .padding(16.dp)
//                        .fillMaxSize(),
//                    uiState = uiState,
//                    onIntent = viewModel::onIntent // Pass intent handler
//                )
//            }
//        }
//        @OptIn(ExperimentalMaterial3Api::class)
//        @Composable
//        fun AddFoodForm(
//            modifier: Modifier = Modifier,
//            uiState: AddFoodContract.UiState,
//            onIntent: (AddFoodContract.Intent) -> Unit
//        ) {
//            Column(
//                modifier = modifier.verticalScroll(rememberScrollState()),
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                Food Name
//                        OutlinedTextField(
//                            value = uiState.foodName,
//                            onValueChange = { onIntent(AddFoodContract.Intent.UpdateFoodName(it)) },
//                            label = {
//                                Text(\"Food Name\") },
//                                modifier = Modifier.fillMaxWidth(),
//                                singleLine = true,
//                                isError =
//                                    uiState.foodName.isBlank() && uiState.isLoading // Show error only after attempt
//                                )
//                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                                    Quantity
//                                    OutlinedTextField(
//                                        value = uiState.quantity,
//                                        onValueChange = {
//                                            onIntent(
//                                                AddFoodContract.Intent.UpdateQuantity(
//                                                    it
//                                                )
//                                            )
//                                        },
//                                        label = {
//                                            Text(\"Quantity\") },
//                                            modifier = Modifier.weight(1f),
//                                            keyboardOptions =
//                                                KeyboardOptions(keyboardType = KeyboardType.NumberDecimal),
//                                            singleLine = true,
//                                            isError =
//                                                uiState.quantity.toDoubleOrNull() == null && uiState.isLoading
//                                            )
//                                            Unit Dropdown
//                                                    UnitSelector( // Extracted Composable for Unit dropdown
//                                                        selectedUnit = uiState.unit,
//                                                        availableUnits = uiState.availableUnits,
//                                                        onUnitSelected = {
//                                                            onIntent(
//                                                                AddFoodContract.Intent.UpdateUnit(
//                                                                    it
//                                                                )
//                                                            )
//                                                        },
//                                                        modifier = Modifier.weight(1f)
//                                                    )
//                                        }
//                                                Row (horizontalArrangement =
//                                            Arrangement.spacedBy(8.dp)) {
//                                            Calories
//                                            OutlinedTextField(
//                                                value = uiState.calories,
//                                                onValueChange = {
//                                                    onIntent(
//                                                        AddFoodContract.Intent.UpdateCalories(
//                                                            it
//                                                        )
//                                                    )
//                                                },
//                                                label = {
//                                                    Text(\"Calories (kcal)\") },
//                                                    modifier = Modifier.weight(1f),
//                                                    keyboardOptions =
//                                                        KeyboardOptions(keyboardType = KeyboardType.NumberDecimal),
//                                                    singleLine = true,
//                                                    isError =
//                                                        uiState.calories.toDoubleOrNull() == null && uiState.isLoading
//                                                    )
//                                                    Protein
//                                                    OutlinedTextField(
//                                                        value = uiState.protein,
//                                                        onValueChange = {
//                                                            onIntent(
//                                                                AddFoodContract.Intent.UpdateProtein(
//                                                                    it
//                                                                )
//                                                            )
//                                                        },
//                                                        label = {
//                                                            Text(\"Protein (g)\") },
//                                                            modifier = Modifier.weight(1f),
//                                                            keyboardOptions =
//                                                                KeyboardOptions(keyboardType = KeyboardType.NumberDecimal),
//                                                            singleLine = true,
//                                                            isError =
//                                                                uiState.protein.toDoubleOrNull() == null && uiState.isLoading
//                                                            )
//                                                        }
//                                                                Meal Selector
//                                                                MealSelector ( // Extracted Composable for Meal selection
//                                                                selectedMeal = uiState.selectedMeal,
//                                                        availableMeals = uiState.availableMeals,
//                                                        onMealSelected = {
//                                                            onIntent(
//                                                                AddFoodContract.Intent.UpdateSelectedMeal(
//                                                                    it
//                                                                )
//                                                            )
//                                                        },
//                                                        modifier = Modifier.fillMaxWidth()
//                                                    )
//                                                    Notes
//                                                    OutlinedTextField(
//                                                        value = uiState.notes,
//                                                        onValueChange = {
//                                                            onIntent(
//                                                                AddFoodContract.Intent.UpdateNotes(
//                                                                    it
//                                                                )
//                                                            )
//                                                        },
//                                                        label = {
//                                                            Text(\"Notes (Optional)\") },
//                                                            modifier = Modifier.fillMaxWidth(),
//                                                            minLines = 3
//                                                            )
//                                                            Spacer(modifier = Modifier.height(16.dp))
//                                                            Save Button
//                                                                    Button(
//                                                                        onClick = {
//                                                                            onIntent(
//                                                                                AddFoodContract.Intent.SaveEntry
//                                                                            )
//                                                                        },
//                                                                        enabled = !uiState.isLoading,
//                                                                        modifier = Modifier.align(
//                                                                            Alignment.End
//                                                                        )
//                                                                    ) {
//                                                                        if (uiState.isLoading) {
//                                                                            CircularProgressIndicator(
//                                                                                modifier = Modifier.size(
//                                                                                    24.dp
//                                                                                ),
//                                                                                strokeWidth = 2.dp
//                                                                            )
//                                                                        } else {
//                                                                            Text(\"Save Entry\")
//                                                                        }
//                                                                    }
//                                                        }
//                                                }
//                                                @OptIn(ExperimentalMaterial3Api::class)
//                                                @Composable
//                                                fun UnitSelector(
//                                                    selectedUnit: String,
//                                                    availableUnits: List<String>,
//                                                    onUnitSelected: (String) -> Unit,
//                                                    modifier: Modifier = Modifier
//                                                ) {
//                                                    var expanded by remember { mutableStateOf(false) }
//                                                    ExposedDropdownMenuBox(
//                                                        expanded = expanded,
//                                                        onExpandedChange = { expanded = !expanded },
//                                                        modifier = modifier
//                                                    ) {
//                                                        OutlinedTextField(
//                                                            value = selectedUnit,
//                                                            onValueChange = {}, // Read-only
//                                                            readOnly = true,
//                                                            label = {
//                                                                Text(\"Unit\") },
//                                                                trailingIcon = {
//                                                                    ExposedDropdownMenuDefaults.TrailingIcon(
//                                                                        expanded = expanded
//                                                                    )
//                                                                },
//                                                                modifier =
//                                                                    Modifier.menuAnchor() // Important for anchoring the dropdown
//                                                                )
//                                                                ExposedDropdownMenu(
//                                                                    expanded = expanded,
//                                                                    onDismissRequest = {
//                                                                        expanded = false
//                                                                    }
//                                                                ) {
//                                                                    availableUnits.forEach { unit ->
//                                                                        DropdownMenuItem(
//                                                                            text = { Text(unit) },
//                                                                            onClick = {
//                                                                                onUnitSelected(unit)
//                                                                                expanded = false
//                                                                            }
//                                                                        )
//                                                                    }
//                                                                }
//                                                            }
//                                                    }
//                                                    @OptIn(ExperimentalMaterial3Api::class)
//                                                    @Composable
//                                                    fun MealSelector(
//                                                        selectedMeal: Meal,
//                                                        availableMeals: List<Meal>,
//                                                        onMealSelected: (Meal) -> Unit,
//                                                        modifier: Modifier = Modifier
//                                                    ) {
//                                                        var expanded by remember {
//                                                            mutableStateOf(
//                                                                false
//                                                            )
//                                                        }
//                                                        ExposedDropdownMenuBox(
//                                                            expanded = expanded,
//                                                            onExpandedChange = {
//                                                                expanded = !expanded
//                                                            },
//                                                            modifier = modifier
//                                                        ) {
//                                                            OutlinedTextField(
//                                                                value = selectedMeal.name.lowercase()
//                                                                    .replaceFirstChar { it.titlecase() },
//                                                                onValueChange = {}, // Read-only
//                                                                readOnly = true,
//                                                                label = {
//                                                                    Text(\"Meal\") },
//                                                                    trailingIcon = {
//                                                                        ExposedDropdownMenuDefaults.TrailingIcon(
//                                                                            expanded = expanded
//                                                                        )
//                                                                    },
//                                                                    modifier = Modifier.menuAnchor()
//                                                                    )
//                                                                    ExposedDropdownMenu(
//                                                                        expanded = expanded,
//                                                                        onDismissRequest = {
//                                                                            expanded = false
//                                                                        }
//                                                                    ) {
//                                                                        availableMeals.forEach { meal ->
//                                                                            DropdownMenuItem(
//                                                                                text = {
//                                                                                    Text(
//                                                                                        meal.name.lowercase()
//                                                                                            .replaceFirstChar { it.titlecase() })
//                                                                                },
//                                                                                onClick = {
//                                                                                    onMealSelected(
//                                                                                        meal
//                                                                                    )
//                                                                                    expanded = false
//                                                                                }
//                                                                            )
//                                                                        }
//                                                                    }
//                                                                }
//                                                        }
//
//                                                    }
//                                                }
//                                        }
//                                }
//                            }
//            }