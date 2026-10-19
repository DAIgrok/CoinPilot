package com.coinpilot

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val platform: String,
    val taskType: String,
    val status: String = "Pendente",
    val details: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY platform, taskType")
    fun observeAll(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: TaskEntity)

    @Query("UPDATE tasks SET status = :status, details = :details, updatedAt = :now WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, details: String, now: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun count(): Int

    suspend fun seedDefaults() {
        if (count() == 0) {
            insert(TaskEntity(platform = "Shopee", taskType = "Check-in diário"))
            insert(TaskEntity(platform = "Shopee", taskType = "Jogos de moedas"))
            insert(TaskEntity(platform = "AliExpress", taskType = "Check-in diário"))
            insert(TaskEntity(platform = "AliExpress", taskType = "Jogos de moedas"))
        }
    }
}

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class CoinPilotDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile private var INSTANCE: CoinPilotDatabase? = null

        fun get(context: Context): CoinPilotDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CoinPilotDatabase::class.java,
                    "coinpilot.db"
                ).build().also { INSTANCE = it }
            }
    }
}
