package me.parth.shoku.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import me.parth.shoku.domain.model.Meal
import java.time.LocalDate

/**
 * Room Entity representing a logged food entry in the database.
 */
@Entity(
    tableName = "logged_entries",
    foreignKeys = [ForeignKey(
        entity = FoodItemEntity::class,
        parentColumns = ["id"],
        childColumns = ["food_item_id"],
        onDelete = ForeignKey.SET_NULL // Keep log even if food template is deleted
    )]
)
data class LoggedEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val foodName: String,
    val quantity: Double,
    val unit: String,
    val calories: Double,
    val protein: Double,
    val date: String, // Store as ISO String (YYYY-MM-DD)
    val meal: Meal,      // Stored as String via TypeConverter
    val notes: String? = null,
    @ColumnInfo(name = "food_item_id", index = true) // Foreign key column
    val foodItemId: Int? = null
) 