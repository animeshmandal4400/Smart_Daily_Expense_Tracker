package com.smart.expensetracker.data.repository

import com.smart.expensetracker.data.model.Expense
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface ExpenseRepository {
    suspend fun addExpense(expense: Expense): Long
    suspend fun getExpenseById(id: Long): Expense?
    suspend fun getAllExpenses(): Flow<List<Expense>>
    suspend fun getExpensesByDate(date: Date): Flow<List<Expense>>
    suspend fun getExpensesByDateRange(startDate: Date, endDate: Date): Flow<List<Expense>>
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(id: Long)
    suspend fun getTodaysExpenses(): Flow<List<Expense>>
} 