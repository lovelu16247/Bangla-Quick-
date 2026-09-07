package com.example.data.model

data class User(
  val id: Long = 0,
  val username: String,
  val email: String,
  val fullName: String,
  val avatarUrl: String = "",
  val totalPoints: Int = 0,
  val currentLevel: Int = 1,
  val completedQuizzes: Int = 0,
  val completedExams: Int = 0,
  val isBlocked: Boolean = false,
  val role: String = "USER", // "USER" or "ADMIN"
  val createdAt: Long = System.currentTimeMillis()
)

data class Category(
  val id: Long = 0,
  val nameBn: String,
  val nameEn: String,
  val iconName: String,
  val colorHex: String,
  val questionCount: Int = 0,
  val isActive: Boolean = true
)

data class Level(
  val levelNumber: Int,
  val titleBn: String,
  val requiredPoints: Int,
  val passingScore: Int,
  val timeLimitSeconds: Int = 20,
  val rewardPoints: Int = 50,
  val difficulty: String = "EASY", // "EASY", "MEDIUM", "HARD"
  val isUnlocked: Boolean = false,
  val isCompleted: Boolean = false,
  val bestScore: Int = 0
)

enum class QuizType {
  MULTIPLE_CHOICE,
  TRUE_FALSE,
  TIMED_QUIZ
}

data class Question(
  val id: Long = 0,
  val questionBn: String,
  val options: List<String>,
  val correctOptionIndex: Int,
  val explanationBn: String,
  val imageUrl: String = "",
  val categoryId: Long,
  val categoryName: String = "",
  val levelNumber: Int = 1,
  val difficulty: String = "MEDIUM",
  val timeLimitSeconds: Int = 20,
  val points: Int = 10,
  val quizType: QuizType = QuizType.MULTIPLE_CHOICE
)

data class Exam(
  val id: Long = 0,
  val titleBn: String,
  val descriptionBn: String,
  val categoryId: Long,
  val categoryName: String = "",
  val totalQuestions: Int = 20,
  val durationMinutes: Int = 20,
  val passingScore: Int = 14,
  val negativeMarkingPerWrong: Float = 0.25f,
  val maxAttempts: Int = 3,
  val attemptsTaken: Int = 0,
  val isPublished: Boolean = true,
  val startTime: Long = 0L,
  val endTime: Long = 0L
)

data class QuizAttemptResult(
  val id: Long = 0,
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

data class ExamAttemptResult(
  val id: Long = 0,
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

data class LeaderboardEntry(
  val rank: Int,
  val userId: Long,
  val name: String,
  val avatarUrl: String,
  val points: Int,
  val completedQuizzes: Int
)

data class RewardItem(
  val id: Long = 0,
  val titleBn: String,
  val descriptionBn: String,
  val pointsRequired: Int,
  val rewardType: String, // "BADGE", "VOUCHER", "CERTIFICATE", "BONUS"
  val isClaimed: Boolean = false
)

data class RewardHistoryItem(
  val id: Long = 0,
  val userId: Long,
  val titleBn: String,
  val pointsAdded: Int,
  val source: String, // "QUIZ", "LEVEL", "DAILY_QUIZ", "EXAM", "REWARDED_AD"
  val timestamp: Long = System.currentTimeMillis()
)

data class AppNotification(
  val id: Long = 0,
  val titleBn: String,
  val messageBn: String,
  val type: String, // "NEW_QUIZ", "NEW_EXAM", "DAILY_QUIZ", "LEVEL_UPDATE", "ANNOUNCEMENT"
  val isRead: Boolean = false,
  val timestamp: Long = System.currentTimeMillis()
)

data class AdSettings(
  val startIoEnabled: Boolean = true,
  val adverticaEnabled: Boolean = true,
  val bannerEnabled: Boolean = true,
  val interstitialEnabled: Boolean = true,
  val rewardedEnabled: Boolean = true,
  val nativeEnabled: Boolean = true,
  val adFrequency: Int = 3, // show interstitial every N quizzes
  val minIntervalSeconds: Int = 45 // min seconds between interstitials
)

data class AppSettings(
  val appName: String = "Bangla Quiz Master",
  val primaryColorHex: String = "#0D7A4A",
  val defaultQuizPoints: Int = 10,
  val levelRequiredScorePercent: Int = 70,
  val isMaintenanceMode: Boolean = false,
  val dailyQuizQuestionsCount: Int = 10,
  val dailyQuizRewardPoints: Int = 50
)

data class AdminLog(
  val id: Long = 0,
  val adminName: String,
  val action: String,
  val details: String,
  val timestamp: Long = System.currentTimeMillis()
)
