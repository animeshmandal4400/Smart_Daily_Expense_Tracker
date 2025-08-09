package com.smart.expensetracker.data.datasource

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.smart.expensetracker.data.model.Expense
import com.smart.expensetracker.data.model.DateConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

@Database(
    entities = [Expense::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class ExpenseDatabase : RoomDatabase() {
    
    abstract fun expenseDao(): ExpenseDao
    
    companion object {
        @Volatile
        private var INSTANCE: ExpenseDatabase? = null
        
        fun getDatabase(context: Context): ExpenseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExpenseDatabase::class.java,
                    "expense_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate with sample data
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = INSTANCE?.expenseDao()
                            dao?.let { expenseDao ->
                                val sampleExpenses = listOf(
                                    Expense(
                                        amount = 450.0,
                                        description = "Office Lunch",
                                        category = "Food",
                                        notes = "Team lunch at local restaurant",
                                        date = Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000) // Yesterday
                                    ),
                                    Expense(
                                        amount = 120.0,
                                        description = "Coffee Supplies",
                                        category = "Food",
                                        notes = "Coffee beans and filters",
                                        date = Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000) // 2 days ago
                                    ),
                                    Expense(
                                        amount = 350.0,
                                        description = "Taxi to Client Meeting",
                                        category = "Travel",
                                        notes = "Transportation to client office",
                                        date = Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000) // 3 days ago
                                    ),
                                    Expense(
                                        amount = 25000.0,
                                        description = "Employee Salary",
                                        category = "Staff",
                                        notes = "Monthly salary payment",
                                        date = Date(System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000) // 5 days ago
                                    ),
                                    Expense(
                                        amount = 1200.0,
                                        description = "Internet Bill",
                                        category = "Other",
                                        notes = "Monthly internet service",
                                        date = Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000) // 7 days ago
                                    )
                                )
                                sampleExpenses.forEach { expense ->
                                    expenseDao.insertExpense(expense)
                                }
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 