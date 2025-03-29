package me.parth.shoku.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.parth.shoku.data.local.converter.MealConverter
import me.parth.shoku.data.local.dao.FoodItemDao
import me.parth.shoku.data.local.dao.LoggedEntryDao
import me.parth.shoku.data.local.entity.FoodItemEntity
import me.parth.shoku.data.local.entity.LoggedEntryEntity

@Database(
    entities = [FoodItemEntity::class, LoggedEntryEntity::class],
    version = 1, // Increment version on schema changes
    exportSchema = false // Schema export is recommended for production apps
)
@TypeConverters(MealConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodItemDao(): FoodItemDao
    abstract fun loggedEntryDao(): LoggedEntryDao

    companion object {
        const val DATABASE_NAME = "shoku_db"
    }
} 