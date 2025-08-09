package com.smart.expensetracker.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smart.expensetracker.R
import com.smart.expensetracker.ui.components.*
import com.smart.expensetracker.ui.theme.*
import com.smart.expensetracker.utils.Constants
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    viewModel: ExpenseListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            // TODO: Show snackbar for error
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        // Sticky Top Bar
        TopBar(
            title = "Expense Tracker",
            onRefresh = { viewModel.loadExpenses() }
        )

        Spacer(modifier = Modifier.height(16.dp))
        // Content Area
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Total Expenses Card
            TotalExpensesCard(
                totalAmount = uiState.totalAmount,
                expenseCount = uiState.expenseCount,
                averageAmount = uiState.averageAmount,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filters Section
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Black,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Date Range and Category Filters
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilterDropdown(
                        value = uiState.selectedDateRange,
                        onValueChange = { selected ->
                            if (selected == "Custom") {
                                showDatePicker = true
                            } else {
                                viewModel.updateDateRange(selected)
                            }
                        },
                        label = "Date Range",
                        options = listOf("Today", "This Week", "This Month", "All Time", "Custom"),
                        modifier = Modifier.weight(1f)
                    )

                    FilterDropdown(
                        value = uiState.selectedCategory,
                        onValueChange = viewModel::updateCategoryFilter,
                        label = "Category",
                        options = listOf("All Categories") + Constants.EXPENSE_CATEGORIES,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // View Type Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ViewTypeButton(
                        text = "By Time",
                        isSelected = uiState.selectedViewType == "By Time",
                        onClick = { viewModel.updateViewType("By Time") },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.time),
                                contentDescription = "Time",
                                tint = if (uiState.selectedViewType == "By Time") White else Black
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )

                    ViewTypeButton(
                        text = "By Category",
                        isSelected = uiState.selectedViewType == "By Category",
                        onClick = { viewModel.updateViewType("By Category") },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.filter),
                                contentDescription = "Category",
                                tint = if (uiState.selectedViewType == "By Category") White else Black
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content Area
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else if (uiState.expenses.isEmpty()) {
                ExpenseEmptyState(
                    title = "No expenses found",
                    message = "Try adjusting your filters or add some expenses to get started."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    uiState.groupedSections.forEach { section ->
                        item(key = "header_${section.title}") {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = section.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Black
                                )
                                Text(
                                    text = "₹${String.format("%.0f", section.totalAmount)}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Black
                                )
                            }
                        }
                        items(section.items, key = { it.id }) { expense ->
                            ExpenseListItem(expense = expense, onDelete = { viewModel.deleteExpense(expense.id) })
                        }
                    }
                }
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        // If no date picked, keep previous
                        showDatePicker = false
                    }) { Text("Done") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) {
                val state = rememberDatePickerState()
                DatePicker(state = state)
                LaunchedEffect(state.selectedDateMillis) {
                    state.selectedDateMillis?.let { millis ->
                        val localDate = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        viewModel.setCustomDate(localDate)
                    }
                }
            }
        }
    }
} 