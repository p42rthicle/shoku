package me.parth.shoku.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import me.parth.shoku.data.local.entity.LoggedEntryEntity
import me.parth.shoku.data.local.pojo.DailySummaryPojo
import java.time.LocalDate

@Dao
interface LoggedEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoggedEntry(entry: LoggedEntryEntity): Long

    @Delete
    suspend fun deleteLoggedEntry(entry: LoggedEntryEntity): Int

    @Query("SELECT * FROM logged_entries WHERE date = :date ORDER BY meal ASC, id ASC")
    fun getEntriesForDate(date: String): Flow<List<LoggedEntryEntity>>

    // New query for daily summaries
    @Query("""
        SELECT date,
               SUM(calories) as totalCalories,
               SUM(protein) as totalProtein
        FROM logged_entries
        GROUP BY date
        ORDER BY date DESC
    """)
    fun getDailySummaries(): Flow<List<DailySummaryPojo>>

    // Query to get all entries, ordered most recent first
    @Query("SELECT * FROM logged_entries ORDER BY date DESC, id DESC")
    fun getAllLoggedEntries(): Flow<List<LoggedEntryEntity>>

    @Update
    suspend fun updateLoggedEntry(entry: LoggedEntryEntity): Int

    @Query("SELECT * FROM logged_entries WHERE id = :id LIMIT 1")
    suspend fun getEntryById(id: Long): LoggedEntryEntity?

    // Add queries for history, daily summaries etc. later as needed

} 