package com.example.doanckapi.data.db

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "history_table")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val timestamp: Long,
    val HighBP: Float,
    val HighChol: Float,
    val HeartDiseaseorAttack: Float,
    val Stroke: Float,
    val GenHlth: Float,
    val MentHlth: Float,
    val PhysHlth: Float,
    val BMI: Float,
    val Age: Float,
    val Income: Float,
    val PhysActivity: Float,
    val DiffWalk: Float,
    val predictionResult: Int, // 0 safe, 1 risk, or other labels depending on the model
    val diabetesType: String? = null
)

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    @Query("SELECT * FROM history_table WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllHistory(userId: Int): Flow<List<HistoryEntity>>
    
    @Query("DELETE FROM history_table WHERE userId = :userId")
    suspend fun clearHistory(userId: Int)

    @Delete
    suspend fun deleteHistory(history: HistoryEntity)
}

@Database(entities = [HistoryEntity::class, UserEntity::class], version = 7, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "diabetes_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
