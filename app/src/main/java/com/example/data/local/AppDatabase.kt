package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [
    UserEntity::class,
    CategoryEntity::class,
    LevelEntity::class,
    QuestionEntity::class,
    QuizEntity::class,
    QuizAttemptEntity::class,
    ExamEntity::class,
    ExamAttemptEntity::class,
    UserAnswerEntity::class,
    RewardHistoryEntity::class,
    LeaderboardEntity::class,
    NotificationEntity::class,
    AdSettingsEntity::class,
    AdminUserEntity::class,
    AdminLogEntity::class,
    AppSettingsEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun userDao(): UserDao
  abstract fun categoryDao(): CategoryDao
  abstract fun levelDao(): LevelDao
  abstract fun questionDao(): QuestionDao
  abstract fun examDao(): ExamDao
  abstract fun quizAttemptDao(): QuizAttemptDao
  abstract fun rewardDao(): RewardDao
  abstract fun notificationDao(): NotificationDao
  abstract fun adminDao(): AdminDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "bangla_quiz_master.db"
        )
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
