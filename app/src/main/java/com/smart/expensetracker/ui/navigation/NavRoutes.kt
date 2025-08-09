package com.smart.expensetracker.ui.navigation

sealed class NavRoutes(val route: String) {
    object ExpenseEntry : NavRoutes("expense_entry")
    object ExpenseList : NavRoutes("expense_list")
    object ExpenseReport : NavRoutes("expense_report")
} 