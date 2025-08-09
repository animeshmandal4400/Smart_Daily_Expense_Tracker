package com.smart.expensetracker.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    
    private val dateFormat = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat(Constants.DISPLAY_DATE_FORMAT, Locale.getDefault())
    
    fun formatDate(date: Date): String {
        return dateFormat.format(date)
    }
    
    fun formatDisplayDate(date: Date): String {
        return displayDateFormat.format(date)
    }
    
    fun parseDate(dateString: String): Date? {
        return try {
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
    
    fun getToday(): Date {
        return Calendar.getInstance().time
    }
    
    fun getStartOfWeek(): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }
    
    fun getEndOfWeek(): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.time
    }
    
    fun getStartOfMonth(): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }
    
    fun getEndOfMonth(): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.time
    }
} 