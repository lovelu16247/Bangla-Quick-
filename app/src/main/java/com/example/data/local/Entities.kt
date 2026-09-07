package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "users")
data class UserEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val username: String,
  val email: String,
  val passwordHash: String,
  val salt: String,
  val fullName: String,
  val avatarUrl: String = "",
  val totalPoints: Int = 0,
  val currentLevel: Int = 1,
  val completedQuizzes: Int = 0,
  val completedExams: Int = 0,
  val isBlocked: Boolean = false,
  val role: String = "USER",
  val lastDailyQuizDate: String = "",
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class CategoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val nameBn: String,
  val nameEn: String,
  val iconName: String,
  val colorHex: String,
  val questionCount: Int = 0,
  val isActive: Boolean = true
)

@Entity(tableName = "levels")
data class LevelEntity(
  @PrimaryKey val levelNumber: Int,
  val titleBn: String,
  val requiredPoints: Int,
  val passingScore: Int,
  val timeLimitSeconds: Int = 20,
  val rewardPoints: Int = 50,
  val difficulty: String = "EASY",
  val isUnlocked: Boolean = false,
  val isCompleted: Boolean = false,
  val bestScore: Int = 0
)

@Entity(tableName = "questions")
data class QuestionEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val questionBn: String,
  val optionsCsv: String, // comma or delimiter-separated options
  val correctOptionIndex: Int,
  val explanationBn: String,
  val imageUrl: String = "",
  val categoryId: Long,
  val levelNumber: Int = 1,
  val difficulty: String = "MEDIUM",
  val timeLimitSeconds: Int = 20,
  val points: Int = 10,
  val quizType: String = "MULTIPLE_CHOICE"
)

@Entity(tableName = "quizzes")
data class QuizEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val titleBn: String,
  val categoryId: Long,
  val totalQuestions: Int = 10,
  val rewardPoints: Int = 50,
  val isDailyQuiz: Boolean = false,
  val dailyDateString: String = "" // YYYY-MM-DD
)

@Entity(tableName = "quiz_attempts")
data class QuizAttemptEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val userId: Long,
  val quizTitle: String,
  val totalQuestions: Int,
  val correctAnswers: Int,
  val wrongAnswers: Int,
  val skippedQuestions: Int,
  val totalScoreEarned: Int,
  val accuracyPercent: Float,
  val isPassed: Boolean,
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "exams")
data class ExamEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val titleBn: String,
  val descriptionBn: String,
  val categoryId: Long,
  val totalQuestions: Int = 20,
  val durationMinutes: Int = 20,
  val passingScore: Int = 14,
  val negativeMarkingPerWrong: Float = 0.25f,
  val maxAttempts: Int = 3,
  val isPublished: Boolean = true,
  val startTime: Long = 0L,
  val endTime: Long = 0L
)

@Entity(tableName = "exam_attempts")
data class ExamAttemptEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val userId: Long,
  val examId: Long,
  val examTitle: String,
  val totalQuestions: Int,
  val correctAnswers: Int,
  val wrongAnswers: Int,
  val skippedQuestions: Int,
  val rawScore: Float,
  val negativeDeduction: Float,
  val finalScore: Float,
  val passingScore: Int,
  val accuracyPercent: Float,
  val isPassed: Boolean,
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_answers")
data class UserAnswerEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val attemptId: Long,
  val attemptType: String, // "QUIZ" or "EXAM"
  val questionId: Long,
  val selectedOptionIndex: Int,
  val isCorrect: Boolean
)

@Entity(tableName = "reward_history")
data class RewardHistoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val userId: Long,
  val titleBn: String,
  val pointsAdded: Int,
  val source: String,
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "leaderboard")
data class LeaderboardEntity(
  @PrimaryKey val userId: Long,
  val username: String,
  val fullName: String,
  val avatarUrl: String,
  val points: Int,
  val completedQuizzes: Int,
  val timeframe: String = "ALL_TIME" // "DAILY", "WEEKLY", "MONTHLY", "ALL_TIME"
)

@Entity(tableName = "notifications")
data class NotificationEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val titleBn: String,
  val messageBn: String,
  val type: String,
  val isRead: Boolean = false,
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "ad_settings")
data class AdSettingsEntity(
  @PrimaryKey val id: Int = 1,
  val startIoEnabled: Boolean = true,
  val adverticaEnabled: Boolean = true,
  val bannerEnabled: Boolean = true,
  val interstitialEnabled: Boolean = true,
  val rewardedEnabled: Boolean = true,
  val nativeEnabled: Boolean = true,
  val adFrequency: Int = 3,
  val minIntervalSeconds: Int = 45
)

@Entity(tableName = "admin_users")
data class AdminUserEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val username: String,
  val passwordHash: String,
  val salt: String,
  val fullName: String,
  val role: String = "SUPER_ADMIN",
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "admin_logs")
data class AdminLogEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val adminName: String,
  val action: String,
  val details: String,
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
  @PrimaryKey val id: Int = 1,
  val appName: String = "Bangla Quiz Master",
  val primaryColorHex: String = "#0D7A4A",
  val defaultQuizPoints: Int = 10,
  val levelRequiredScorePercent: Int = 70,
  val isMaintenanceMode: Boolean = false,
  val dailyQuizQuestionsCount: Int = 10,
  val dailyQuizRewardPoints: Int = 50
)
