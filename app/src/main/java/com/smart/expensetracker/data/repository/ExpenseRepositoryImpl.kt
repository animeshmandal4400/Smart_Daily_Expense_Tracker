package com.smart.expensetracker.data.repository

import com.smart.expensetracker.data.datasource.ExpenseDao
import com.smart.expensetracker.data.model.Expense
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {

    override suspend fun addExpense(expense: Expense): Long {
        return expenseDao.insertExpense(expense)
    }

    override suspend fun getExpenseById(id: Long): Expense? {
        return expenseDao.getExpenseById(id)
    }

    override suspend fun getAllExpenses(): Flow<List<Expense>> {
        return expenseDao.getAllExpenses()
    }

    override suspend fun getExpensesByDate(date: Date): Flow<List<Expense>> {
        return expenseDao.getExpensesByDate(date)
    }

    override suspend fun getExpensesByDateRange(startDate: Date, endDate: Date): Flow<List<Expense>> {
        return expenseDao.getExpensesByDateRange(startDate, endDate)
    }

    override suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense)
    }

    override suspend fun deleteExpense(id: Long) {
        expenseDao.deleteExpenseById(id)
    }

    override suspend fun getTodaysExpenses(): Flow<List<Expense>> {
        return expenseDao.getTodaysExpenses()
    }
} 