package com.smart.expensetracker.data.datasource

import androidx.room.*
import com.smart.expensetracker.data.model.Expense
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ExpenseDao {
    
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Long): Expense?
    
    @Query("SELECT * FROM expenses WHERE date(date/1000, 'unixepoch') = date(:date/1000, 'unixepoch') ORDER BY date DESC")
    fun getExpensesByDate(date: Date): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getExpensesByDateRange(startDate: Date, endDate: Date): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE date(date/1000, 'unixepoch') = date('now') ORDER BY date DESC")
    fun getTodaysExpenses(): Flow<List<Expense>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long
    
    @Update
    suspend fun updateExpense(expense: Expense)
    
    @Delete
    suspend fun deleteExpense(expense: Expense)
    
    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: Long)
} 