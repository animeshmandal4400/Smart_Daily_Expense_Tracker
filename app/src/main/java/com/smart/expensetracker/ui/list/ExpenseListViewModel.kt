package com.smart.expensetracker.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smart.expensetracker.data.model.Expense
import com.smart.expensetracker.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseListUiState())
    val uiState: StateFlow<ExpenseListUiState> = _uiState.asStateFlow()

    init {
        loadExpenses()
    }

    fun loadExpenses() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                expenseRepository.getAllExpenses().collect { expenses ->
                    val filteredExpenses = filterExpenses(expenses)
                    val totalAmount = filteredExpenses.sumOf { it.amount }
                    val averageAmount = if (filteredExpenses.isNotEmpty()) totalAmount / filteredExpenses.size else 0.0

                    val groupedSections = buildGroupedSections(filteredExpenses)

                    _uiState.value = _uiState.value.copy(
                        expenses = filteredExpenses,
                        groupedSections = groupedSections,
                        totalAmount = String.format("%.0f", totalAmount),
                        expenseCount = "${filteredExpenses.size} expenses",
                        averageAmount = String.format("%.0f", averageAmount),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load expenses: ${e.message}"
                )
            }
        }
    }

    fun updateDateRange(dateRange: String) {
        _uiState.value = _uiState.value.copy(selectedDateRange = dateRange, selectedCustomDate = null)
        loadExpenses()
    }

    fun setCustomDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedCustomDate = date, selectedDateRange = "Custom")
        loadExpenses()
    }

    fun updateCategoryFilter(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        loadExpenses()
    }

    fun updateViewType(viewType: String) {
        _uiState.value = _uiState.value.copy(selectedViewType = viewType)
        // Rebuild grouped sections with current expenses
        val currentExpenses = _uiState.value.expenses
        val newGroupedSections = buildGroupedSections(currentExpenses)
        _uiState.value = _uiState.value.copy(groupedSections = newGroupedSections)
    }

    fun deleteExpense(id: Long) {
        viewModelScope.launch {
            try {
                expenseRepository.deleteExpense(id)
                loadExpenses()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to delete expense: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun filterExpenses(expenses: List<Expense>): List<Expense> {
        var filtered = expenses

        val selectedDate = _uiState.value.selectedCustomDate
        if (_uiState.value.selectedDateRange == "Custom" && selectedDate != null) {
            filtered = filtered.filter { expense ->
                expense.date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate() == selectedDate
            }
        } else {
            // Filter by date range
            when (_uiState.value.selectedDateRange) {
                "Today" -> {
                    val today = LocalDate.now()
                    filtered = filtered.filter { expense ->
                        val expenseDate = expense.date.toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        expenseDate == today
                    }
                }
                "This Week" -> {
                    val now = LocalDate.now()
                    val startOfWeek = now.minusDays(now.dayOfWeek.value.toLong() - 1)
                    val endOfWeek = startOfWeek.plusDays(6)
                    filtered = filtered.filter { expense ->
                        val expenseDate = expense.date.toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        expenseDate in startOfWeek..endOfWeek
                    }
                }
                "This Month" -> {
                    val now = LocalDate.now()
                    val startOfMonth = now.withDayOfMonth(1)
                    val endOfMonth = now.withDayOfMonth(now.month.length(now.isLeapYear))
                    filtered = filtered.filter { expense ->
                        val expenseDate = expense.date.toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        expenseDate in startOfMonth..endOfMonth
                    }
                }
                // "All Time" - no filtering needed
            }
        }

        // Filter by category
        if (_uiState.value.selectedCategory != "All Categories") {
            filtered = filtered.filter { it.category == _uiState.value.selectedCategory }
        }

        return filtered
    }

    private fun buildGroupedSections(expenses: List<Expense>): List<GroupedExpenseSection> {
        return if (_uiState.value.selectedViewType == "By Category") {
            val groups = expenses.groupBy { it.category }
            groups.map { (category, items) ->
                GroupedExpenseSection(
                    title = category,
                    totalAmount = items.sumOf { it.amount },
                    items = items.sortedByDescending { it.date.time }
                )
            }.sortedByDescending { it.totalAmount }
        } else {
            // By Time: group by day label (Today, Yesterday, or date)
            val groups = expenses.groupBy { expense ->
                val date = expense.date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                when (date) {
                    LocalDate.now() -> "Today"
                    LocalDate.now().minusDays(1) -> "Yesterday"
                    else -> date.format(DateTimeFormatter.ofPattern("d MMM"))
                }
            }
            val orderIndex: (String) -> Int = { label ->
                when (label) {
                    "Today" -> 0
                    "Yesterday" -> 1
                    else -> 2
                }
            }
            groups.map { (label, items) ->
                GroupedExpenseSection(
                    title = label,
                    totalAmount = items.sumOf { it.amount },
                    items = items.sortedByDescending { it.date.time }
                )
            }.sortedWith(compareBy<GroupedExpenseSection> { orderIndex(it.title) }.thenByDescending { it.items.firstOrNull()?.date?.time ?: 0 })
        }
    }
}

data class GroupedExpenseSection(
    val title: String,
    val totalAmount: Double,
    val items: List<Expense>
)

data class ExpenseListUiState(
    val expenses: List<Expense> = emptyList(),
    val groupedSections: List<GroupedExpenseSection> = emptyList(),
    val totalAmount: String = "0",
    val expenseCount: String = "0 expenses",
    val averageAmount: String = "0",
    val selectedDateRange: String = "Today",
    val selectedCustomDate: LocalDate? = null,
    val selectedCategory: String = "All Categories",
    val selectedViewType: String = "By Time",
    val isLoading: Boolean = false,
    val error: String? = null
) 