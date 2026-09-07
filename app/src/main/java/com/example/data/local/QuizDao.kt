package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
  @Query("SELECT * FROM users WHERE id = :id")
  suspend fun getUserById(id: Long): UserEntity?

  @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
  suspend fun getUserByEmail(email: String): UserEntity?

  @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
  suspend fun getUserByUsername(username: String): UserEntity?

  @Query("SELECT * FROM users ORDER BY totalPoints DESC")
  fun getAllUsersFlow(): Flow<List<UserEntity>>

  @Query("SELECT * FROM users ORDER BY totalPoints DESC")
  suspend fun getAllUsers(): List<UserEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUser(user: UserEntity): Long

  @Update
  suspend fun updateUser(user: UserEntity)

  @Query("UPDATE users SET totalPoints = totalPoints + :points WHERE id = :userId")
  suspend fun addPoints(userId: Long, points: Int)

  @Query("UPDATE users SET currentLevel = :level WHERE id = :userId")
  suspend fun updateLevel(userId: Long, level: Int)

  @Query("UPDATE users SET completedQuizzes = completedQuizzes + 1 WHERE id = :userId")
  suspend fun incrementCompletedQuizzes(userId: Long)

  @Query("UPDATE users SET completedExams = completedExams + 1 WHERE id = :userId")
  suspend fun incrementCompletedExams(userId: Long)

  @Query("UPDATE users SET isBlocked = :blocked WHERE id = :userId")
  suspend fun setUserBlocked(userId: Long, blocked: Boolean)

  @Query("UPDATE users SET totalPoints = :points WHERE id = :userId")
  suspend fun setTotalPoints(userId: Long, points: Int)

  @Query("UPDATE users SET currentLevel = 1, completedQuizzes = 0, completedExams = 0, totalPoints = 0 WHERE id = :userId")
  suspend fun resetUserProgress(userId: Long)

  @Query("SELECT COUNT(*) FROM users")
  suspend fun getTotalUsersCount(): Int
}

@Dao
interface CategoryDao {
  @Query("SELECT * FROM categories WHERE isActive = 1 ORDER BY id ASC")
  fun getActiveCategoriesFlow(): Flow<List<CategoryEntity>>

  @Query("SELECT * FROM categories ORDER BY id ASC")
  fun getAllCategoriesFlow(): Flow<List<CategoryEntity>>

  @Query("SELECT * FROM categories WHERE id = :id")
  suspend fun getCategoryById(id: Long): CategoryEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategory(category: CategoryEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategories(categories: List<CategoryEntity>)

  @Update
  suspend fun updateCategory(category: CategoryEntity)

  @Query("DELETE FROM categories WHERE id = :id")
  suspend fun deleteCategoryById(id: Long)

  @Query("SELECT COUNT(*) FROM categories")
  suspend fun getTotalCategoriesCount(): Int
}

@Dao
interface LevelDao {
  @Query("SELECT * FROM levels ORDER BY levelNumber ASC")
  fun getAllLevelsFlow(): Flow<List<LevelEntity>>

  @Query("SELECT * FROM levels WHERE levelNumber = :levelNumber")
  suspend fun getLevel(levelNumber: Int): LevelEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLevels(levels: List<LevelEntity>)

  @Query("UPDATE levels SET isUnlocked = 1 WHERE levelNumber = :levelNumber")
  suspend fun unlockLevel(levelNumber: Int)

  @Query("UPDATE levels SET isCompleted = 1, bestScore = MAX(bestScore, :score) WHERE levelNumber = :levelNumber")
  suspend fun markLevelCompleted(levelNumber: Int, score: Int)
}

@Dao
interface QuestionDao {
  @Query("SELECT * FROM questions ORDER BY id DESC")
  fun getAllQuestionsFlow(): Flow<List<QuestionEntity>>

  @Query("SELECT * FROM questions WHERE categoryId = :categoryId ORDER BY RANDOM() LIMIT :limit")
  suspend fun getQuestionsByCategory(categoryId: Long, limit: Int = 10): List<QuestionEntity>

  @Query("SELECT * FROM questions WHERE levelNumber = :levelNumber ORDER BY RANDOM() LIMIT :limit")
  suspend fun getQuestionsByLevel(levelNumber: Int, limit: Int = 10): List<QuestionEntity>

  @Query("SELECT * FROM questions ORDER BY RANDOM() LIMIT :limit")
  suspend fun getRandomQuestions(limit: Int = 10): List<QuestionEntity>

  @Query("SELECT * FROM questions WHERE id = :id")
  suspend fun getQuestionById(id: Long): QuestionEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertQuestion(question: QuestionEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertQuestions(questions: List<QuestionEntity>)

  @Update
  suspend fun updateQuestion(question: QuestionEntity)

  @Query("DELETE FROM questions WHERE id = :id")
  suspend fun deleteQuestionById(id: Long)

  @Query("SELECT COUNT(*) FROM questions")
  suspend fun getTotalQuestionsCount(): Int
}

@Dao
interface ExamDao {
  @Query("SELECT * FROM exams WHERE isPublished = 1 ORDER BY id DESC")
  fun getPublishedExamsFlow(): Flow<List<ExamEntity>>

  @Query("SELECT * FROM exams ORDER BY id DESC")
  fun getAllExamsFlow(): Flow<List<ExamEntity>>

  @Query("SELECT * FROM exams WHERE id = :id")
  suspend fun getExamById(id: Long): ExamEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertExam(exam: ExamEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertExams(exams: List<ExamEntity>)

  @Update
  suspend fun updateExam(exam: ExamEntity)

  @Query("DELETE FROM exams WHERE id = :id")
  suspend fun deleteExamById(id: Long)

  @Query("SELECT COUNT(*) FROM exams")
  suspend fun getTotalExamsCount(): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertExamAttempt(attempt: ExamAttemptEntity): Long

  @Query("SELECT * FROM exam_attempts WHERE userId = :userId ORDER BY timestamp DESC")
  fun getUserExamAttemptsFlow(userId: Long): Flow<List<ExamAttemptEntity>>

  @Query("SELECT COUNT(*) FROM exam_attempts WHERE examId = :examId AND userId = :userId")
  suspend fun getExamAttemptsCount(examId: Long, userId: Long): Int

  @Query("SELECT COUNT(*) FROM exam_attempts")
  suspend fun getTotalExamAttemptsCount(): Int
}

@Dao
interface QuizAttemptDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertQuizAttempt(attempt: QuizAttemptEntity): Long

  @Query("SELECT * FROM quiz_attempts WHERE userId = :userId ORDER BY timestamp DESC")
  fun getUserQuizAttemptsFlow(userId: Long): Flow<List<QuizAttemptEntity>>

  @Query("SELECT * FROM quiz_attempts WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
  fun getRecentAttemptsFlow(userId: Long, limit: Int = 5): Flow<List<QuizAttemptEntity>>

  @Query("SELECT COUNT(*) FROM quiz_attempts")
  suspend fun getTotalQuizAttemptsCount(): Int
}

@Dao
interface RewardDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRewardHistory(item: RewardHistoryEntity): Long

  @Query("SELECT * FROM reward_history WHERE userId = :userId ORDER BY timestamp DESC")
  fun getUserRewardHistoryFlow(userId: Long): Flow<List<RewardHistoryEntity>>
}

@Dao
interface NotificationDao {
  @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
  fun getAllNotificationsFlow(): Flow<List<NotificationEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNotification(notification: NotificationEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNotifications(notifications: List<NotificationEntity>)

  @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
  suspend fun markAsRead(id: Long)

  @Query("UPDATE notifications SET isRead = 1")
  suspend fun markAllAsRead()
}

@Dao
interface AdminDao {
  @Query("SELECT * FROM admin_users WHERE username = :username LIMIT 1")
  suspend fun getAdminByUsername(username: String): AdminUserEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAdminUser(admin: AdminUserEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAdminLog(log: AdminLogEntity): Long

  @Query("SELECT * FROM admin_logs ORDER BY timestamp DESC LIMIT 100")
  fun getAdminLogsFlow(): Flow<List<AdminLogEntity>>

  @Query("SELECT * FROM ad_settings WHERE id = 1")
  fun getAdSettingsFlow(): Flow<AdSettingsEntity?>

  @Query("SELECT * FROM ad_settings WHERE id = 1")
  suspend fun getAdSettings(): AdSettingsEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveAdSettings(settings: AdSettingsEntity)

  @Query("SELECT * FROM app_settings WHERE id = 1")
  fun getAppSettingsFlow(): Flow<AppSettingsEntity?>

  @Query("SELECT * FROM app_settings WHERE id = 1")
  suspend fun getAppSettings(): AppSettingsEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveAppSettings(settings: AppSettingsEntity)
}
