package com.smart.expensetracker.data.datasource

import com.smart.expensetracker.data.model.Expense
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface LocalDataSource {
    suspend fun insertExpense(expense: Expense): Long
    suspend fun getExpenseById(id: Long): Expense?
    suspend fun getAllExpenses(): Flow<List<Expense>>
    suspend fun getExpensesByDate(date: Date): Flow<List<Expense>>
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(id: Long)
    suspend fun deleteAllExpenses()
} 