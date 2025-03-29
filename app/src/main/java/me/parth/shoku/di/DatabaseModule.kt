package me.parth.shoku.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.parth.shoku.data.local.AppDatabase
import me.parth.shoku.data.local.dao.FoodItemDao
import me.parth.shoku.data.local.dao.LoggedEntryDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Lives as long as the application
object DatabaseModule {

    @Provides
    @Singleton // Ensure only one instance of the database
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
        // Add migrations here if needed later
        .build()
    }

    @Provides
    @Singleton
    fun provideFoodItemDao(appDatabase: AppDatabase): FoodItemDao {
        return appDatabase.foodItemDao()
    }

    @Provides
    @Singleton
    fun provideLoggedEntryDao(appDatabase: AppDatabase): LoggedEntryDao {
        return appDatabase.loggedEntryDao()
    }
} 