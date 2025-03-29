package me.parth.shoku.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import me.parth.shoku.data.local.entity.LoggedEntryEntity
import java.time.LocalDate

@Dao
interface LoggedEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoggedEntry(entry: LoggedEntryEntity): Long

    @Query("SELECT * FROM logged_entries WHERE date = :date ORDER BY id ASC")
    fun getEntriesForDate(date: String): Flow<List<LoggedEntryEntity>>

    // Add queries for history, daily summaries etc. later as needed

} 