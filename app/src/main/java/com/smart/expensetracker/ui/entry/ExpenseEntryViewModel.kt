package com.smart.expensetracker.ui.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smart.expensetracker.data.model.Expense
import com.smart.expensetracker.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ExpenseEntryViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseEntryUiState())
    val uiState: StateFlow<ExpenseEntryUiState> = _uiState.asStateFlow()

    init {
        getTodaysTotal()
    }

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
        if (title.isNotBlank()) clearError()
    }

    fun updateAmount(amount: String) {
        _uiState.value = _uiState.value.copy(amount = amount)
        if (amount.isNotBlank()) clearError()
    }

    fun updateCategory(category: String) {
        _uiState.value = _uiState.value.copy(category = category)
        if (category.isNotBlank()) clearError()
    }

    fun updateNotes(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    fun showImagePicker() {
        _uiState.value = _uiState.value.copy(showImagePicker = true)
    }

    fun hideImagePicker() {
        _uiState.value = _uiState.value.copy(showImagePicker = false)
    }

    fun selectImageFromCamera() {
        // TODO: Implement camera functionality
        _uiState.value = _uiState.value.copy(
            showImagePicker = false,
            receiptImagePath = "Camera image selected"
        )
    }

    fun selectImageFromGallery() {
        // TODO: Implement gallery functionality
        _uiState.value = _uiState.value.copy(
            showImagePicker = false,
            receiptImagePath = "Gallery image selected"
        )
    }

    fun addExpense() {
        val currentState = _uiState.value
        if (currentState.title.isBlank() || currentState.amount.isBlank() || currentState.category.isBlank()) {
            _uiState.value = currentState.copy(error = "Please fill all required fields")
            return
        }

        val amount = currentState.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _uiState.value = currentState.copy(error = "Please enter a valid amount")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = currentState.copy(isLoading = true, error = null, isAnimating = true)
                
                val expense = Expense(
                    amount = amount,
                    description = currentState.title,
                    category = currentState.category,
                    notes = currentState.notes,
                    date = Date()
                )
                
                expenseRepository.addExpense(expense)
                
                // Simulate animation delay
                kotlinx.coroutines.delay(1000)
                
                _uiState.value = currentState.copy(
                    isLoading = false,
                    title = "",
                    amount = "",
                    category = "",
                    notes = "",
                    receiptImagePath = "",
                    successMessage = "Expense added successfully!",
                    isAnimating = false,
                    showToast = true,
                    shouldScrollToTop = true,
                    shouldBlinkTotal = true,
                    error = null // Clear any previous errors
                )
                
                // Refresh today's total after adding expense
                getTodaysTotal()
                
                // Reset scroll and blink flags after animation completes
                kotlinx.coroutines.delay(500) // Wait for blink animation to complete
                _uiState.value = _uiState.value.copy(shouldScrollToTop = false, shouldBlinkTotal = false)
                
                // Hide toast after 2 seconds
                kotlinx.coroutines.delay(2000)
                _uiState.value = _uiState.value.copy(showToast = false)
                
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    error = "Failed to add expense: ${e.message}",
                    isAnimating = false
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    fun hideToast() {
        _uiState.value = _uiState.value.copy(showToast = false)
    }

    fun getTodaysTotal() {
        viewModelScope.launch {
            try {
                expenseRepository.getTodaysExpenses().collect { expenses ->
                    val total = expenses.sumOf { it.amount }
                    _uiState.value = _uiState.value.copy(todaysTotal = String.format("%.0f", total))
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(todaysTotal = "0")
            }
        }
    }
}

data class ExpenseEntryUiState(
    val title: String = "",
    val amount: String = "",
    val category: String = "",
    val notes: String = "",
    val todaysTotal: String = "0",
    val receiptImagePath: String = "",
    val showImagePicker: Boolean = false,
    val isLoading: Boolean = false,
    val isAnimating: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val showToast: Boolean = false,
    val shouldScrollToTop: Boolean = false,
    val shouldBlinkTotal: Boolean = false
) 