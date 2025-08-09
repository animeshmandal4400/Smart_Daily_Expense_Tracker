package com.smart.expensetracker.utils

object ValidationUtils {
    
    fun isValidAmount(amount: String): Boolean {
        return amount.toDoubleOrNull()?.let { it > 0 } ?: false
    }
    
    fun isValidDescription(description: String): Boolean {
        return description.trim().isNotBlank() && description.length <= 100
    }
    
    fun isValidCategory(category: String): Boolean {
        return category.trim().isNotBlank()
    }
    
    fun getAmountError(amount: String): String? {
        return when {
            amount.isBlank() -> "Amount is required"
            !isValidAmount(amount) -> "Please enter a valid amount greater than 0"
            else -> null
        }
    }
    
    fun getDescriptionError(description: String): String? {
        return when {
            description.isBlank() -> "Description is required"
            description.length > 100 -> "Description must be less than 100 characters"
            else -> null
        }
    }
    
    fun getCategoryError(category: String): String? {
        return when {
            category.isBlank() -> "Category is required"
            else -> null
        }
    }
} 