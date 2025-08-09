package com.smart.expensetracker.di

import android.content.Context
import androidx.room.Room
import com.smart.expensetracker.data.datasource.ExpenseDao
import com.smart.expensetracker.data.datasource.ExpenseDatabase
import com.smart.expensetracker.data.repository.ExpenseRepository
import com.smart.expensetracker.data.repository.ExpenseRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(
        expenseRepositoryImpl: ExpenseRepositoryImpl
    ): ExpenseRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideExpenseDatabase(
        @ApplicationContext context: Context
    ): ExpenseDatabase {
        return ExpenseDatabase.getDatabase(context)
    }
    
    @Provides
    @Singleton
    fun provideExpenseDao(database: ExpenseDatabase): ExpenseDao {
        return database.expenseDao()
    }
} 