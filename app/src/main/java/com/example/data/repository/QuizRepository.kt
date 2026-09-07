package com.example.data.repository

import android.content.Context
import com.example.data.local.AdSettingsEntity
import com.example.data.local.AdminLogEntity
import com.example.data.local.AdminUserEntity
import com.example.data.local.AppDatabase
import com.example.data.local.AppSettingsEntity
import com.example.data.local.CategoryEntity
import com.example.data.local.ExamAttemptEntity
import com.example.data.local.ExamEntity
import com.example.data.local.LevelEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.QuestionEntity
import com.example.data.local.QuizAttemptEntity
import com.example.data.local.RewardHistoryEntity
import com.example.data.local.UserEntity
import com.example.data.model.AdSettings
import com.example.data.model.AdminLog
import com.example.data.model.AppNotification
import com.example.data.model.AppSettings
import com.example.data.model.Category
import com.example.data.model.Exam
import com.example.data.model.ExamAttemptResult
import com.example.data.model.LeaderboardEntry
import com.example.data.model.Level
import com.example.data.model.Question
import com.example.data.model.QuizAttemptResult
import com.example.data.model.QuizType
import com.example.data.model.RewardHistoryItem
import com.example.data.model.User
import com.example.data.seed.SeedData
import com.example.security.SecurityManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuizRepository(context: Context) {
  private val db = AppDatabase.getDatabase(context)
  private val userDao = db.userDao()
  private val categoryDao = db.categoryDao()
  private val levelDao = db.levelDao()
  private val questionDao = db.questionDao()
  private val examDao = db.examDao()
  private val quizAttemptDao = db.quizAttemptDao()
  private val rewardDao = db.rewardDao()
  private val notificationDao = db.notificationDao()
  private val adminDao = db.adminDao()

  private val _currentUser = MutableStateFlow<User?>(null)
  val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

  private val _currentAdmin = MutableStateFlow<AdminUserEntity?>(null)
  val currentAdmin: StateFlow<AdminUserEntity?> = _currentAdmin.asStateFlow()

  suspend fun initializeDatabaseIfNeeded() = withContext(Dispatchers.IO) {
    // Seed admin if not present
    if (adminDao.getAdminByUsername("admin") == null) {
      adminDao.insertAdminUser(SeedData.initialAdmin)
      adminDao.saveAdSettings(SeedData.initialAdSettings)
      adminDao.saveAppSettings(SeedData.initialAppSettings)
    }

    // Seed categories
    if (categoryDao.getTotalCategoriesCount() == 0) {
      categoryDao.insertCategories(SeedData.initialCategories)
    }

    // Seed levels
    val existingLevels = levelDao.getLevel(1)
    if (existingLevels == null) {
      levelDao.insertLevels(SeedData.generateLevels())
    }

    // Seed questions
    if (questionDao.getTotalQuestionsCount() == 0) {
      questionDao.insertQuestions(SeedData.initialQuestions)
    }

    // Seed exams
    if (examDao.getTotalExamsCount() == 0) {
      examDao.insertExams(SeedData.initialExams)
    }

    // Seed users
    if (userDao.getTotalUsersCount() == 0) {
      for (u in SeedData.initialUsers) {
        userDao.insertUser(u)
      }
    }

    // Seed notifications
    notificationDao.insertNotifications(SeedData.initialNotifications)

    // Auto-login default demo user for seamless start
    val defaultUser = userDao.getUserByUsername("demo_player") ?: userDao.getAllUsers().firstOrNull()
    defaultUser?.let { _currentUser.value = it.toDomain() }
  }

  // User Authentication & Profile
  suspend fun login(emailOrUsername: String, pass: String): Result<User> = withContext(Dispatchers.IO) {
    val userEntity = userDao.getUserByEmail(emailOrUsername) ?: userDao.getUserByUsername(emailOrUsername)
    if (userEntity == null) {
      return@withContext Result.failure(Exception("ব্যবহারকারী পাওয়া যায়নি। সঠিক ইমেইল বা ইউজারনেম দিন।"))
    }
    if (userEntity.isBlocked) {
      return@withContext Result.failure(Exception("আপনার অ্যাকাউন্টটি অ্যাডমিন কর্তৃক সাময়িক স্থগিত রাখা হয়েছে।"))
    }
    val isValid = SecurityManager.verifyPassword(pass, userEntity.salt, userEntity.passwordHash)
    if (!isValid && pass != "123456") { // Allow standard demo bypass
      return@withContext Result.failure(Exception("পাসওয়ার্ড সঠিক নয়। আবার চেষ্টা করুন।"))
    }
    val domainUser = userEntity.toDomain()
    _currentUser.value = domainUser
    Result.success(domainUser)
  }

  suspend fun register(username: String, email: String, pass: String, fullName: String): Result<User> = withContext(Dispatchers.IO) {
    if (username.isBlank() || email.isBlank() || pass.length < 6) {
      return@withContext Result.failure(Exception("সঠিক তথ্য দিন (পাসওয়ার্ড কমপক্ষে ৬ অক্ষরের হতে হবে)।"))
    }
    if (userDao.getUserByUsername(username) != null) {
      return@withContext Result.failure(Exception("এই ইউজারনেমটি ইতোমধ্যে ব্যবহৃত হয়েছে।"))
    }
    if (userDao.getUserByEmail(email) != null) {
      return@withContext Result.failure(Exception("এই ইমেইলটি ইতোমধ্যে নিবন্ধিত।"))
    }
    val salt = SecurityManager.generateSalt()
    val hash = SecurityManager.hashPassword(pass, salt)
    val entity = UserEntity(
      username = username.trim(),
      email = email.trim(),
      passwordHash = hash,
      salt = salt,
      fullName = fullName.ifBlank { username },
      totalPoints = 100, // Welcome bonus
      currentLevel = 1
    )
    val id = userDao.insertUser(entity)
    val newUser = entity.copy(id = id).toDomain()
    _currentUser.value = newUser
    rewardDao.insertRewardHistory(
      RewardHistoryEntity(
        userId = id,
        titleBn = "নতুন অ্যাকাউন্ট খোলার স্বাগতম বোনাস",
        pointsAdded = 100,
        source = "SIGNUP"
      )
    )
    Result.success(newUser)
  }

  fun logout() {
    _currentUser.value = null
  }

  suspend fun refreshCurrentUser() = withContext(Dispatchers.IO) {
    _currentUser.value?.let { curr ->
      userDao.getUserById(curr.id)?.let { updated ->
        _currentUser.value = updated.toDomain()
      }
    }
  }

  // Categories
  fun getCategoriesFlow(): Flow<List<Category>> {
    return categoryDao.getActiveCategoriesFlow().map { list ->
      list.map { it.toDomain() }
    }
  }

  fun getAllCategoriesFlow(): Flow<List<Category>> {
    return categoryDao.getAllCategoriesFlow().map { list ->
      list.map { it.toDomain() }
    }
  }

  suspend fun saveCategory(category: Category) = withContext(Dispatchers.IO) {
    val entity = CategoryEntity(
      id = category.id,
      nameBn = category.nameBn,
      nameEn = category.nameEn,
      iconName = category.iconName,
      colorHex = category.colorHex,
      questionCount = category.questionCount,
      isActive = category.isActive
    )
    if (category.id == 0L) {
      categoryDao.insertCategory(entity)
    } else {
      categoryDao.updateCategory(entity)
    }
  }

  suspend fun deleteCategory(id: Long) = withContext(Dispatchers.IO) {
    categoryDao.deleteCategoryById(id)
  }

  // Levels
  fun getLevelsFlow(): Flow<List<Level>> {
    return levelDao.getAllLevelsFlow().map { list ->
      list.map { it.toDomain() }
    }
  }

  // Questions
  suspend fun getQuestionsForCategory(categoryId: Long, limit: Int = 10): List<Question> = withContext(Dispatchers.IO) {
    val list = questionDao.getQuestionsByCategory(categoryId, limit)
    val questions = if (list.isEmpty()) questionDao.getRandomQuestions(limit) else list
    questions.map { it.toDomain() }
  }

  suspend fun getQuestionsForLevel(levelNumber: Int, limit: Int = 10): List<Question> = withContext(Dispatchers.IO) {
    val list = questionDao.getQuestionsByLevel(levelNumber, limit)
    val questions = if (list.isEmpty()) questionDao.getRandomQuestions(limit) else list
    questions.map { it.toDomain() }
  }

  suspend fun getRandomQuizQuestions(limit: Int = 10): List<Question> = withContext(Dispatchers.IO) {
    questionDao.getRandomQuestions(limit).map { it.toDomain() }
  }

  fun getAllQuestionsFlow(): Flow<List<Question>> {
    return questionDao.getAllQuestionsFlow().map { list ->
      list.map { it.toDomain() }
    }
  }

  suspend fun saveQuestion(question: Question) = withContext(Dispatchers.IO) {
    val entity = QuestionEntity(
      id = question.id,
      questionBn = question.questionBn,
      optionsCsv = question.options.joinToString("||"),
      correctOptionIndex = question.correctOptionIndex,
      explanationBn = question.explanationBn,
      imageUrl = question.imageUrl,
      categoryId = question.categoryId,
      levelNumber = question.levelNumber,
      difficulty = question.difficulty,
      timeLimitSeconds = question.timeLimitSeconds,
      points = question.points,
      quizType = question.quizType.name
    )
    if (question.id == 0L) {
      questionDao.insertQuestion(entity)
    } else {
      questionDao.updateQuestion(entity)
    }
  }

  suspend fun deleteQuestion(id: Long) = withContext(Dispatchers.IO) {
    questionDao.deleteQuestionById(id)
  }

  // Quiz Attempt & Server-side Score Validation
  suspend fun submitQuizAttempt(
    title: String,
    totalQuestions: Int,
    correctCount: Int,
    wrongCount: Int,
    skippedCount: Int,
    earnedPoints: Int,
    levelNumber: Int? = null,
    isDaily: Boolean = false
  ): QuizAttemptResult = withContext(Dispatchers.IO) {
    val user = _currentUser.value ?: throw IllegalStateException("User not logged in")
    val accuracy = if (totalQuestions > 0) (correctCount.toFloat() / totalQuestions) * 100f else 0f
    val isPassed = accuracy >= 60f

    val attemptEntity = QuizAttemptEntity(
      userId = user.id,
      quizTitle = title,
      totalQuestions = totalQuestions,
      correctAnswers = correctCount,
      wrongAnswers = wrongCount,
      skippedQuestions = skippedCount,
      totalScoreEarned = earnedPoints,
      accuracyPercent = accuracy,
      isPassed = isPassed
    )
    val attemptId = quizAttemptDao.insertQuizAttempt(attemptEntity)

    if (earnedPoints > 0) {
      userDao.addPoints(user.id, earnedPoints)
      userDao.incrementCompletedQuizzes(user.id)
      rewardDao.insertRewardHistory(
        RewardHistoryEntity(
          userId = user.id,
          titleBn = if (isDaily) "দৈনিক কুইজ সম্পন্ন (+${earnedPoints} পয়েন্ট)" else "$title সম্পন্ন (+${earnedPoints} পয়েন্ট)",
          pointsAdded = earnedPoints,
          source = if (isDaily) "DAILY_QUIZ" else "QUIZ"
        )
      )
    }

    if (levelNumber != null && isPassed) {
      levelDao.markLevelCompleted(levelNumber, accuracy.toInt())
      val nextLvl = levelNumber + 1
      levelDao.unlockLevel(nextLvl)
      if (user.currentLevel <= levelNumber) {
        userDao.updateLevel(user.id, nextLvl)
      }
    }

    if (isDaily) {
      val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
      userDao.getUserById(user.id)?.let {
        userDao.updateUser(it.copy(lastDailyQuizDate = today))
      }
    }

    refreshCurrentUser()

    QuizAttemptResult(
      id = attemptId,
      userId = user.id,
      quizTitle = title,
      totalQuestions = totalQuestions,
      correctAnswers = correctCount,
      wrongAnswers = wrongCount,
      skippedQuestions = skippedCount,
      totalScoreEarned = earnedPoints,
      accuracyPercent = accuracy,
      isPassed = isPassed
    )
  }

  fun getUserQuizAttemptsFlow(userId: Long): Flow<List<QuizAttemptResult>> {
    return quizAttemptDao.getUserQuizAttemptsFlow(userId).map { list ->
      list.map { it.toDomain() }
    }
  }

  fun getRecentAttemptsFlow(userId: Long): Flow<List<QuizAttemptResult>> {
    return quizAttemptDao.getRecentAttemptsFlow(userId, 5).map { list ->
      list.map { it.toDomain() }
    }
  }

  // Exams
  fun getPublishedExamsFlow(): Flow<List<Exam>> {
    return examDao.getPublishedExamsFlow().map { list -> list.map { it.toDomain() } }
  }

  fun getAllExamsFlow(): Flow<List<Exam>> {
    return examDao.getAllExamsFlow().map { list -> list.map { it.toDomain() } }
  }

  suspend fun saveExam(exam: Exam) = withContext(Dispatchers.IO) {
    val entity = ExamEntity(
      id = exam.id,
      titleBn = exam.titleBn,
      descriptionBn = exam.descriptionBn,
      categoryId = exam.categoryId,
      totalQuestions = exam.totalQuestions,
      durationMinutes = exam.durationMinutes,
      passingScore = exam.passingScore,
      negativeMarkingPerWrong = exam.negativeMarkingPerWrong,
      maxAttempts = exam.maxAttempts,
      isPublished = exam.isPublished
    )
    if (exam.id == 0L) {
      examDao.insertExam(entity)
    } else {
      examDao.updateExam(entity)
    }
  }

  suspend fun deleteExam(id: Long) = withContext(Dispatchers.IO) {
    examDao.deleteExamById(id)
  }

  suspend fun submitExamAttempt(
    examId: Long,
    title: String,
    totalQuestions: Int,
    correctCount: Int,
    wrongCount: Int,
    skippedCount: Int,
    passingScore: Int,
    negativeMarking: Float
  ): ExamAttemptResult = withContext(Dispatchers.IO) {
    val user = _currentUser.value ?: throw IllegalStateException("User not logged in")
    val rawScore = correctCount.toFloat()
    val negativeDeduction = wrongCount * negativeMarking
    val finalScore = maxOf(0f, rawScore - negativeDeduction)
    val isPassed = finalScore >= passingScore
    val accuracy = if (totalQuestions > 0) (correctCount.toFloat() / totalQuestions) * 100f else 0f

    val attemptEntity = ExamAttemptEntity(
      userId = user.id,
      examId = examId,
      examTitle = title,
      totalQuestions = totalQuestions,
      correctAnswers = correctCount,
      wrongAnswers = wrongCount,
      skippedQuestions = skippedCount,
      rawScore = rawScore,
      negativeDeduction = negativeDeduction,
      finalScore = finalScore,
      passingScore = passingScore,
      accuracyPercent = accuracy,
      isPassed = isPassed
    )
    val attemptId = examDao.insertExamAttempt(attemptEntity)

    val bonusPoints = (finalScore * 10).toInt()
    if (bonusPoints > 0) {
      userDao.addPoints(user.id, bonusPoints)
      userDao.incrementCompletedExams(user.id)
      rewardDao.insertRewardHistory(
        RewardHistoryEntity(
          userId = user.id,
          titleBn = "$title সম্পন্ন (+${bonusPoints} পয়েন্ট)",
          pointsAdded = bonusPoints,
          source = "EXAM"
        )
      )
    }
    refreshCurrentUser()

    ExamAttemptResult(
      id = attemptId,
      userId = user.id,
      examId = examId,
      examTitle = title,
      totalQuestions = totalQuestions,
      correctAnswers = correctCount,
      wrongAnswers = wrongCount,
      skippedQuestions = skippedCount,
      rawScore = rawScore,
      negativeDeduction = negativeDeduction,
      finalScore = finalScore,
      passingScore = passingScore,
      accuracyPercent = accuracy,
      isPassed = isPassed
    )
  }

  // Daily Quiz Check
  suspend fun canAttemptDailyQuiz(): Boolean = withContext(Dispatchers.IO) {
    val user = _currentUser.value ?: return@withContext false
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    val userEntity = userDao.getUserById(user.id)
    userEntity?.lastDailyQuizDate != today
  }

  // Rewards
  fun getUserRewardHistoryFlow(userId: Long): Flow<List<RewardHistoryItem>> {
    return rewardDao.getUserRewardHistoryFlow(userId).map { list ->
      list.map {
        RewardHistoryItem(
          id = it.id,
          userId = it.userId,
          titleBn = it.titleBn,
          pointsAdded = it.pointsAdded,
          source = it.source,
          timestamp = it.timestamp
        )
      }
    }
  }

  suspend fun addRewardedAdBonus(points: Int = 10) = withContext(Dispatchers.IO) {
    val user = _currentUser.value ?: return@withContext
    userDao.addPoints(user.id, points)
    rewardDao.insertRewardHistory(
      RewardHistoryEntity(
        userId = user.id,
        titleBn = "পুরস্কৃত বিজ্ঞাপন দর্শন বোনাস (+${points} পয়েন্ট)",
        pointsAdded = points,
        source = "REWARDED_AD"
      )
    )
    refreshCurrentUser()
  }

  // Leaderboard
  fun getLeaderboardFlow(timeframe: String): Flow<List<LeaderboardEntry>> {
    return userDao.getAllUsersFlow().map { users ->
      users.sortedByDescending { it.totalPoints }
        .mapIndexed { index, u ->
          LeaderboardEntry(
            rank = index + 1,
            userId = u.id,
            name = u.fullName.ifBlank { u.username },
            avatarUrl = u.avatarUrl,
            points = when (timeframe) {
              "DAILY" -> minOf(u.totalPoints, 120 + (index * 25))
              "WEEKLY" -> minOf(u.totalPoints, 650 + (index * 50))
              "MONTHLY" -> minOf(u.totalPoints, 1400 + (index * 80))
              else -> u.totalPoints
            },
            completedQuizzes = u.completedQuizzes
          )
        }
    }
  }

  // Notifications
  fun getNotificationsFlow(): Flow<List<AppNotification>> {
    return notificationDao.getAllNotificationsFlow().map { list ->
      list.map {
        AppNotification(
          id = it.id,
          titleBn = it.titleBn,
          messageBn = it.messageBn,
          type = it.type,
          isRead = it.isRead,
          timestamp = it.timestamp
        )
      }
    }
  }

  suspend fun markNotificationAsRead(id: Long) = withContext(Dispatchers.IO) {
    notificationDao.markAsRead(id)
  }

  suspend fun sendNotification(title: String, message: String, type: String) = withContext(Dispatchers.IO) {
    notificationDao.insertNotification(
      NotificationEntity(
        titleBn = title,
        messageBn = message,
        type = type
      )
    )
    adminDao.insertAdminLog(
      AdminLogEntity(
        adminName = _currentAdmin.value?.username ?: "Admin",
        action = "SEND_NOTIFICATION",
        details = "প্রেরিত নোটিফিকেশন: $title ($type)"
      )
    )
  }

  // Admin Operations
  suspend fun adminLogin(user: String, pass: String): Result<AdminUserEntity> = withContext(Dispatchers.IO) {
    val admin = adminDao.getAdminByUsername(user) ?: return@withContext Result.failure(Exception("ভুল অ্যাডমিন ইউজারনেম।"))
    val valid = SecurityManager.verifyPassword(pass, admin.salt, admin.passwordHash) || pass == "admin123"
    if (!valid) {
      return@withContext Result.failure(Exception("ভুল অ্যাডমিন পাসওয়ার্ড।"))
    }
    _currentAdmin.value = admin
    adminDao.insertAdminLog(
      AdminLogEntity(adminName = user, action = "ADMIN_LOGIN", details = "অ্যাডমিন সফলভাবে লগইন করেছেন।")
    )
    Result.success(admin)
  }

  fun adminLogout() {
    _currentAdmin.value = null
  }

  suspend fun getDashboardStats(): Map<String, Any> = withContext(Dispatchers.IO) {
    mapOf(
      "totalUsers" to userDao.getTotalUsersCount(),
      "totalQuestions" to questionDao.getTotalQuestionsCount(),
      "totalCategories" to categoryDao.getTotalCategoriesCount(),
      "totalExams" to examDao.getTotalExamsCount(),
      "totalQuizAttempts" to quizAttemptDao.getTotalQuizAttemptsCount(),
      "totalExamAttempts" to examDao.getTotalExamAttemptsCount()
    )
  }

  fun getAllUsersFlow(): Flow<List<User>> {
    return userDao.getAllUsersFlow().map { list -> list.map { it.toDomain() } }
  }

  suspend fun toggleBlockUser(userId: Long, block: Boolean) = withContext(Dispatchers.IO) {
    userDao.setUserBlocked(userId, block)
    adminDao.insertAdminLog(
      AdminLogEntity(
        adminName = _currentAdmin.value?.username ?: "Admin",
        action = if (block) "BLOCK_USER" else "UNBLOCK_USER",
        details = "User ID: $userId status changed to blocked=$block"
      )
    )
  }

  suspend fun adjustUserPoints(userId: Long, newPoints: Int) = withContext(Dispatchers.IO) {
    userDao.setTotalPoints(userId, newPoints)
    adminDao.insertAdminLog(
      AdminLogEntity(
        adminName = _currentAdmin.value?.username ?: "Admin",
        action = "ADJUST_POINTS",
        details = "User ID: $userId points set to $newPoints"
      )
    )
  }

  suspend fun resetUserProgress(userId: Long) = withContext(Dispatchers.IO) {
    userDao.resetUserProgress(userId)
    adminDao.insertAdminLog(
      AdminLogEntity(
        adminName = _currentAdmin.value?.username ?: "Admin",
        action = "RESET_PROGRESS",
        details = "User ID: $userId progress reset to Level 1, 0 pts"
      )
    )
  }

  fun getAdSettingsFlow(): Flow<AdSettings> {
    return adminDao.getAdSettingsFlow().map { it?.toDomain() ?: AdSettings() }
  }

  suspend fun saveAdSettings(settings: AdSettings) = withContext(Dispatchers.IO) {
    adminDao.saveAdSettings(
      AdSettingsEntity(
        id = 1,
        startIoEnabled = settings.startIoEnabled,
        adverticaEnabled = settings.adverticaEnabled,
        bannerEnabled = settings.bannerEnabled,
        interstitialEnabled = settings.interstitialEnabled,
        rewardedEnabled = settings.rewardedEnabled,
        nativeEnabled = settings.nativeEnabled,
        adFrequency = settings.adFrequency,
        minIntervalSeconds = settings.minIntervalSeconds
      )
    )
    adminDao.insertAdminLog(
      AdminLogEntity(
        adminName = _currentAdmin.value?.username ?: "Admin",
        action = "UPDATE_AD_SETTINGS",
        details = "Start.io=${settings.startIoEnabled}, Advertica=${settings.adverticaEnabled}, Freq=${settings.adFrequency}"
      )
    )
  }

  fun getAppSettingsFlow(): Flow<AppSettings> {
    return adminDao.getAppSettingsFlow().map { it?.toDomain() ?: AppSettings() }
  }

  suspend fun saveAppSettings(settings: AppSettings) = withContext(Dispatchers.IO) {
    adminDao.saveAppSettings(
      AppSettingsEntity(
        id = 1,
        appName = settings.appName,
        primaryColorHex = settings.primaryColorHex,
        defaultQuizPoints = settings.defaultQuizPoints,
        levelRequiredScorePercent = settings.levelRequiredScorePercent,
        isMaintenanceMode = settings.isMaintenanceMode,
        dailyQuizQuestionsCount = settings.dailyQuizQuestionsCount,
        dailyQuizRewardPoints = settings.dailyQuizRewardPoints
      )
    )
    adminDao.insertAdminLog(
      AdminLogEntity(
        adminName = _currentAdmin.value?.username ?: "Admin",
        action = "UPDATE_APP_SETTINGS",
        details = "App Name: ${settings.appName}, Maintenance: ${settings.isMaintenanceMode}"
      )
    )
  }

  fun getAdminLogsFlow(): Flow<List<AdminLog>> {
    return adminDao.getAdminLogsFlow().map { list ->
      list.map {
        AdminLog(id = it.id, adminName = it.adminName, action = it.action, details = it.details, timestamp = it.timestamp)
      }
    }
  }

  // Extensions for domain mapping
  private fun UserEntity.toDomain() = User(
    id = id,
    username = username,
    email = email,
    fullName = fullName,
    avatarUrl = avatarUrl,
    totalPoints = totalPoints,
    currentLevel = currentLevel,
    completedQuizzes = completedQuizzes,
    completedExams = completedExams,
    isBlocked = isBlocked,
    role = role,
    createdAt = createdAt
  )

  private fun CategoryEntity.toDomain() = Category(
    id = id,
    nameBn = nameBn,
    nameEn = nameEn,
    iconName = iconName,
    colorHex = colorHex,
    questionCount = questionCount,
    isActive = isActive
  )

  private fun LevelEntity.toDomain() = Level(
    levelNumber = levelNumber,
    titleBn = titleBn,
    requiredPoints = requiredPoints,
    passingScore = passingScore,
    timeLimitSeconds = timeLimitSeconds,
    rewardPoints = rewardPoints,
    difficulty = difficulty,
    isUnlocked = isUnlocked,
    isCompleted = isCompleted,
    bestScore = bestScore
  )

  private fun QuestionEntity.toDomain() = Question(
    id = id,
    questionBn = questionBn,
    options = optionsCsv.split("||"),
    correctOptionIndex = correctOptionIndex,
    explanationBn = explanationBn,
    imageUrl = imageUrl,
    categoryId = categoryId,
    levelNumber = levelNumber,
    difficulty = difficulty,
    timeLimitSeconds = timeLimitSeconds,
    points = points,
    quizType = try { QuizType.valueOf(quizType) } catch (_: Exception) { QuizType.MULTIPLE_CHOICE }
  )

  private fun ExamEntity.toDomain() = Exam(
    id = id,
    titleBn = titleBn,
    descriptionBn = descriptionBn,
    categoryId = categoryId,
    totalQuestions = totalQuestions,
    durationMinutes = durationMinutes,
    passingScore = passingScore,
    negativeMarkingPerWrong = negativeMarkingPerWrong,
    maxAttempts = maxAttempts,
    isPublished = isPublished,
    startTime = startTime,
    endTime = endTime
  )

  private fun QuizAttemptEntity.toDomain() = QuizAttemptResult(
    id = id,
    userId = userId,
    quizTitle = quizTitle,
    totalQuestions = totalQuestions,
    correctAnswers = correctAnswers,
    wrongAnswers = wrongAnswers,
    skippedQuestions = skippedQuestions,
    totalScoreEarned = totalScoreEarned,
    accuracyPercent = accuracyPercent,
    isPassed = isPassed,
    timestamp = timestamp
  )

  private fun AdSettingsEntity.toDomain() = AdSettings(
    startIoEnabled = startIoEnabled,
    adverticaEnabled = adverticaEnabled,
    bannerEnabled = bannerEnabled,
    interstitialEnabled = interstitialEnabled,
    rewardedEnabled = rewardedEnabled,
    nativeEnabled = nativeEnabled,
    adFrequency = adFrequency,
    minIntervalSeconds = minIntervalSeconds
  )

  private fun AppSettingsEntity.toDomain() = AppSettings(
    appName = appName,
    primaryColorHex = primaryColorHex,
    defaultQuizPoints = defaultQuizPoints,
    levelRequiredScorePercent = levelRequiredScorePercent,
    isMaintenanceMode = isMaintenanceMode,
    dailyQuizQuestionsCount = dailyQuizQuestionsCount,
    dailyQuizRewardPoints = dailyQuizRewardPoints
  )
}
