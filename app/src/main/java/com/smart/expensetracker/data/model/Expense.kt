package com.smart.expensetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val description: String,
    val category: String,
    val notes: String = "",
    val date: Date,
    val createdAt: Date = Date()
) 