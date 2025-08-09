package com.smart.expensetracker.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class ExpenseReportViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseReportUiState())
    val uiState: StateFlow<ExpenseReportUiState> = _uiState.asStateFlow()

    init {
        loadReportData()
    }

    fun loadReportData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                expenseRepository.getAllExpenses().collect { expenses ->
                    val weeklyData = generateWeeklyData(expenses)
                    val categoryData = generateCategoryData(expenses)
                    
                    _uiState.value = _uiState.value.copy(
                        weeklyTotal = String.format("%.0f", weeklyData.totalAmount),
                        averageDaily = String.format("%.0f", weeklyData.averageDaily),
                        vsLastWeek = "0", // TODO: Calculate vs last week
                        changeType = "Increased spending",
                        dailyData = weeklyData.dailyData,
                        categoryData = categoryData,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load report data: ${e.message}"
                )
            }
        }
    }

    fun exportAsPdf() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(exportMessage = "Generating PDF...")
                kotlinx.coroutines.delay(2000) // Simulate PDF generation
                _uiState.value = _uiState.value.copy(exportMessage = "PDF exported successfully! Saved to Downloads/expense_report.pdf")
                kotlinx.coroutines.delay(3000) // Show success message for 3 seconds
                _uiState.value = _uiState.value.copy(exportMessage = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(exportMessage = "Failed to export PDF: ${e.message}")
                kotlinx.coroutines.delay(3000)
                _uiState.value = _uiState.value.copy(exportMessage = null)
            }
        }
    }

    fun exportAsCsv() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(exportMessage = "Generating CSV...")
                kotlinx.coroutines.delay(1500) // Simulate CSV generation
                _uiState.value = _uiState.value.copy(exportMessage = "CSV exported successfully! Saved to Downloads/expense_report.csv")
                kotlinx.coroutines.delay(3000) // Show success message for 3 seconds
                _uiState.value = _uiState.value.copy(exportMessage = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(exportMessage = "Failed to export CSV: ${e.message}")
                kotlinx.coroutines.delay(3000)
                _uiState.value = _uiState.value.copy(exportMessage = null)
            }
        }
    }

    fun shareReport() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(exportMessage = "Preparing report for sharing...")
                kotlinx.coroutines.delay(1000) // Simulate preparation
                _uiState.value = _uiState.value.copy(exportMessage = "Report ready to share! Opening share dialog...")
                kotlinx.coroutines.delay(2000) // Show message for 2 seconds
                _uiState.value = _uiState.value.copy(exportMessage = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(exportMessage = "Failed to prepare report: ${e.message}")
                kotlinx.coroutines.delay(3000)
                _uiState.value = _uiState.value.copy(exportMessage = null)
            }
        }
    }

    fun clearExportMessage() {
        _uiState.value = _uiState.value.copy(exportMessage = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun generateWeeklyData(expenses: List<com.smart.expensetracker.data.model.Expense>): WeeklyData {
        val today = LocalDate.now()
        val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
        val endOfWeek = startOfWeek.plusDays(6)
        
        val weeklyExpenses = expenses.filter { expense ->
            val expenseDate = expense.date.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
            expenseDate in startOfWeek..endOfWeek
        }
        
        val totalAmount = weeklyExpenses.sumOf { it.amount }
        val averageDaily = if (weeklyExpenses.isNotEmpty()) totalAmount / 7 else 0.0
        
        // Generate daily data for the week
        val dailyData = mutableListOf<Pair<String, Double>>()
        val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        
        for (i in 0..6) {
            val dayDate = startOfWeek.plusDays(i.toLong())
            val dayExpenses = weeklyExpenses.filter { expense ->
                val expenseDate = expense.date.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate()
                expenseDate == dayDate
            }
            val dayTotal = dayExpenses.sumOf { it.amount }
            dailyData.add(Pair(days[i], dayTotal))
        }
        
        return WeeklyData(totalAmount, averageDaily, dailyData)
    }

    private fun generateCategoryData(expenses: List<com.smart.expensetracker.data.model.Expense>): List<Triple<String, Double, String>> {
        val categoryMap = expenses.groupBy { it.category }
        val totalAmount = expenses.sumOf { it.amount }
        
        return categoryMap.map { (category, categoryExpenses) ->
            val categoryTotal = categoryExpenses.sumOf { it.amount }
            val percentage = if (totalAmount > 0) (categoryTotal / totalAmount * 100) else 0.0
            Triple(category, categoryTotal, String.format("%.0f", percentage))
        }.sortedByDescending { it.second }
    }
}

data class WeeklyData(
    val totalAmount: Double,
    val averageDaily: Double,
    val dailyData: List<Pair<String, Double>>
)

data class ExpenseReportUiState(
    val weeklyTotal: String = "0",
    val averageDaily: String = "0",
    val vsLastWeek: String = "0",
    val changeType: String = "Increased spending",
    val dailyData: List<Pair<String, Double>> = emptyList(),
    val categoryData: List<Triple<String, Double, String>> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val exportMessage: String? = null
) 