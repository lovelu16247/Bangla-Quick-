package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ads.AdsManager
import com.example.data.model.AppNotification
import com.example.data.model.AppSettings
import com.example.data.model.Category
import com.example.data.model.Exam
import com.example.data.model.ExamAttemptResult
import com.example.data.model.LeaderboardEntry
import com.example.data.model.Level
import com.example.data.model.Question
import com.example.data.model.QuizAttemptResult
import com.example.data.model.RewardHistoryItem
import com.example.data.model.User
import com.example.data.repository.QuizRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class QuizViewModel(application: Application) : AndroidViewModel(application) {
  val repository = QuizRepository(application)
  val adsManager = AdsManager(application)

  val currentUser: StateFlow<User?> = repository.currentUser
  val categories: StateFlow<List<Category>> = repository.getCategoriesFlow()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val levels: StateFlow<List<Level>> = repository.getLevelsFlow()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val publishedExams: StateFlow<List<Exam>> = repository.getPublishedExamsFlow()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val appSettings: StateFlow<AppSettings> = repository.getAppSettingsFlow()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

  val notifications: StateFlow<List<AppNotification>> = repository.getNotificationsFlow()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Leaderboard timeframe: "DAILY", "WEEKLY", "MONTHLY", "ALL_TIME"
  private val _leaderboardTimeframe = MutableStateFlow("ALL_TIME")
  val leaderboardTimeframe: StateFlow<String> = _leaderboardTimeframe.asStateFlow()

  private val _leaderboardList = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
  val leaderboardList: StateFlow<List<LeaderboardEntry>> = _leaderboardList.asStateFlow()

  // Active Quiz Play State
  private val _activeQuizTitle = MutableStateFlow("কুইজ")
  val activeQuizTitle: StateFlow<String> = _activeQuizTitle.asStateFlow()

  private val _quizQuestions = MutableStateFlow<List<Question>>(emptyList())
  val quizQuestions: StateFlow<List<Question>> = _quizQuestions.asStateFlow()

  private val _currentQuestionIndex = MutableStateFlow(0)
  val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

  private val _selectedOptionIndex = MutableStateFlow<Int?>(null)
  val selectedOptionIndex: StateFlow<Int?> = _selectedOptionIndex.asStateFlow()

  private val _isAnswerSubmitted = MutableStateFlow(false)
  val isAnswerSubmitted: StateFlow<Boolean> = _isAnswerSubmitted.asStateFlow()

  private val _quizCorrectCount = MutableStateFlow(0)
  val quizCorrectCount: StateFlow<Int> = _quizCorrectCount.asStateFlow()

  private val _quizWrongCount = MutableStateFlow(0)
  val quizWrongCount: StateFlow<Int> = _quizWrongCount.asStateFlow()

  private val _quizEarnedPoints = MutableStateFlow(0)
  val quizEarnedPoints: StateFlow<Int> = _quizEarnedPoints.asStateFlow()

  private val _quizTimerSeconds = MutableStateFlow(20)
  val quizTimerSeconds: StateFlow<Int> = _quizTimerSeconds.asStateFlow()

  private val _isQuizCompleted = MutableStateFlow(false)
  val isQuizCompleted: StateFlow<Boolean> = _isQuizCompleted.asStateFlow()

  private val _lastQuizResult = MutableStateFlow<QuizAttemptResult?>(null)
  val lastQuizResult: StateFlow<QuizAttemptResult?> = _lastQuizResult.asStateFlow()

  private var quizTimerJob: Job? = null
  private var activeLevelNumber: Int? = null
  private var isDailyQuizActive: Boolean = false

  // Active Exam Play State
  private val _activeExam = MutableStateFlow<Exam?>(null)
  val activeExam: StateFlow<Exam?> = _activeExam.asStateFlow()

  private val _examQuestions = MutableStateFlow<List<Question>>(emptyList())
  val examQuestions: StateFlow<List<Question>> = _examQuestions.asStateFlow()

  private val _examCurrentIndex = MutableStateFlow(0)
  val examCurrentIndex: StateFlow<Int> = _examCurrentIndex.asStateFlow()

  // Map of questionIndex to selectedOption (or -1 if skipped)
  private val _examAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
  val examAnswers: StateFlow<Map<Int, Int>> = _examAnswers.asStateFlow()

  private val _examRemainingSeconds = MutableStateFlow(1200)
  val examRemainingSeconds: StateFlow<Int> = _examRemainingSeconds.asStateFlow()

  private val _lastExamResult = MutableStateFlow<ExamAttemptResult?>(null)
  val lastExamResult: StateFlow<ExamAttemptResult?> = _lastExamResult.asStateFlow()

  private var examTimerJob: Job? = null

  // User Quiz History & Rewards
  private val _userQuizHistory = MutableStateFlow<List<QuizAttemptResult>>(emptyList())
  val userQuizHistory: StateFlow<List<QuizAttemptResult>> = _userQuizHistory.asStateFlow()

  private val _userRewardHistory = MutableStateFlow<List<RewardHistoryItem>>(emptyList())
  val userRewardHistory: StateFlow<List<RewardHistoryItem>> = _userRewardHistory.asStateFlow()

  private val _rewardMessage = MutableStateFlow<String?>(null)
  val rewardMessage: StateFlow<String?> = _rewardMessage.asStateFlow()

  init {
    viewModelScope.launch {
      repository.initializeDatabaseIfNeeded()
      loadLeaderboard("ALL_TIME")
      currentUser.value?.let { u ->
        loadUserHistory(u.id)
      }
    }
  }

  fun setLeaderboardTimeframe(tf: String) {
    _leaderboardTimeframe.value = tf
    loadLeaderboard(tf)
  }

  private fun loadLeaderboard(tf: String) {
    viewModelScope.launch {
      repository.getLeaderboardFlow(tf).collect {
        _leaderboardList.value = it
      }
    }
  }

  fun loadUserHistory(userId: Long) {
    viewModelScope.launch {
      repository.getUserQuizAttemptsFlow(userId).collect {
        _userQuizHistory.value = it
      }
    }
    viewModelScope.launch {
      repository.getUserRewardHistoryFlow(userId).collect {
        _userRewardHistory.value = it
      }
    }
  }

  // Quiz Lifecycle
  fun startQuizForCategory(category: Category) {
    viewModelScope.launch {
      activeLevelNumber = null
      isDailyQuizActive = false
      _activeQuizTitle.value = "${category.nameBn} কুইজ"
      val questions = repository.getQuestionsForCategory(category.id, 10)
      startQuizSession(questions)
    }
  }

  fun startQuizForLevel(level: Level) {
    viewModelScope.launch {
      activeLevelNumber = level.levelNumber
      isDailyQuizActive = false
      _activeQuizTitle.value = level.titleBn
      val questions = repository.getQuestionsForLevel(level.levelNumber, 10)
      startQuizSession(questions)
    }
  }

  fun startDailyQuiz() {
    viewModelScope.launch {
      activeLevelNumber = null
      isDailyQuizActive = true
      _activeQuizTitle.value = "আজকের দৈনিক কুইজ চ্যালেঞ্জ"
      val questions = repository.getRandomQuizQuestions(10)
      startQuizSession(questions)
    }
  }

  private fun startQuizSession(questions: List<Question>) {
    _quizQuestions.value = questions
    _currentQuestionIndex.value = 0
    _selectedOptionIndex.value = null
    _isAnswerSubmitted.value = false
    _quizCorrectCount.value = 0
    _quizWrongCount.value = 0
    _quizEarnedPoints.value = 0
    _isQuizCompleted.value = false
    _lastQuizResult.value = null
    startQuestionTimer()
  }

  private fun startQuestionTimer() {
    quizTimerJob?.cancel()
    val currentQ = _quizQuestions.value.getOrNull(_currentQuestionIndex.value)
    val limit = currentQ?.timeLimitSeconds ?: 20
    _quizTimerSeconds.value = limit

    quizTimerJob = viewModelScope.launch {
      while (_quizTimerSeconds.value > 0 && !_isAnswerSubmitted.value) {
        delay(1000)
        _quizTimerSeconds.value -= 1
      }
      if (_quizTimerSeconds.value == 0 && !_isAnswerSubmitted.value) {
        // Time expired! Mark as wrong or skipped
        submitAnswer(-1)
      }
    }
  }

  fun selectOption(index: Int) {
    if (_isAnswerSubmitted.value) return // Prevent changing answer after submission
    submitAnswer(index)
  }

  private fun submitAnswer(index: Int) {
    if (_isAnswerSubmitted.value) return
    quizTimerJob?.cancel()
    _selectedOptionIndex.value = index
    _isAnswerSubmitted.value = true

    val q = _quizQuestions.value.getOrNull(_currentQuestionIndex.value)
    if (q != null) {
      if (index == q.correctOptionIndex) {
        _quizCorrectCount.value += 1
        _quizEarnedPoints.value += q.points
      } else {
        _quizWrongCount.value += 1
      }
    }
  }

  fun nextQuestion() {
    val nextIdx = _currentQuestionIndex.value + 1
    if (nextIdx < _quizQuestions.value.size) {
      _currentQuestionIndex.value = nextIdx
      _selectedOptionIndex.value = null
      _isAnswerSubmitted.value = false
      startQuestionTimer()
    } else {
      finishQuiz()
    }
  }

  private fun finishQuiz() {
    quizTimerJob?.cancel()
    _isQuizCompleted.value = true
    viewModelScope.launch {
      val total = _quizQuestions.value.size
      val correct = _quizCorrectCount.value
      val wrong = _quizWrongCount.value
      val skipped = maxOf(0, total - (correct + wrong))
      val points = _quizEarnedPoints.value

      val result = repository.submitQuizAttempt(
        title = _activeQuizTitle.value,
        totalQuestions = total,
        correctCount = correct,
        wrongCount = wrong,
        skippedCount = skipped,
        earnedPoints = points,
        levelNumber = activeLevelNumber,
        isDaily = isDailyQuizActive
      )
      _lastQuizResult.value = result
      currentUser.value?.let { loadUserHistory(it.id) }
    }
  }

  // Exam Lifecycle
  fun startExam(exam: Exam) {
    viewModelScope.launch {
      _activeExam.value = exam
      val questions = repository.getQuestionsForCategory(exam.categoryId, exam.totalQuestions)
      val finalQuestions = if (questions.size < exam.totalQuestions) repository.getRandomQuizQuestions(exam.totalQuestions) else questions
      _examQuestions.value = finalQuestions
      _examCurrentIndex.value = 0
      _examAnswers.value = emptyMap()
      _examRemainingSeconds.value = exam.durationMinutes * 60
      _lastExamResult.value = null

      examTimerJob?.cancel()
      examTimerJob = viewModelScope.launch {
        while (_examRemainingSeconds.value > 0) {
          delay(1000)
          _examRemainingSeconds.value -= 1
        }
        submitExam()
      }
    }
  }

  fun setExamCurrentIndex(index: Int) {
    if (index in 0 until _examQuestions.value.size) {
      _examCurrentIndex.value = index
    }
  }

  fun selectExamAnswer(questionIndex: Int, optionIndex: Int) {
    val current = _examAnswers.value.toMutableMap()
    current[questionIndex] = optionIndex
    _examAnswers.value = current
  }

  fun submitExam() {
    examTimerJob?.cancel()
    val exam = _activeExam.value ?: return
    viewModelScope.launch {
      var correct = 0
      var wrong = 0
      var skipped = 0

      _examQuestions.value.forEachIndexed { idx, q ->
        val ans = _examAnswers.value[idx]
        when {
          ans == null || ans == -1 -> skipped++
          ans == q.correctOptionIndex -> correct++
          else -> wrong++
        }
      }

      val result = repository.submitExamAttempt(
        examId = exam.id,
        title = exam.titleBn,
        totalQuestions = _examQuestions.value.size,
        correctCount = correct,
        wrongCount = wrong,
        skippedCount = skipped,
        passingScore = exam.passingScore,
        negativeMarking = exam.negativeMarkingPerWrong
      )
      _lastExamResult.value = result
      currentUser.value?.let { loadUserHistory(it.id) }
    }
  }

  // Rewarded Video Ad flow for points
  fun watchRewardedAdForPoints() {
    val callbackToken = UUID.randomUUID().toString()
    adsManager.showRewardedAd(
      callbackToken = callbackToken,
      onRewardGranted = { pts ->
        viewModelScope.launch {
          repository.addRewardedAdBonus(pts)
          _rewardMessage.value = "অভিনন্দন! আপনি বিজ্ঞাপন দেখে $pts পয়েন্ট অর্জন করেছেন।"
          currentUser.value?.let { loadUserHistory(it.id) }
        }
      },
      onFailed = { msg ->
        _rewardMessage.value = msg
      }
    )
  }

  fun clearRewardMessage() {
    _rewardMessage.value = null
  }

  fun markNotificationAsRead(id: Long) {
    viewModelScope.launch {
      repository.markNotificationAsRead(id)
    }
  }

  fun logout() {
    repository.logout()
  }

  override fun onCleared() {
    super.onCleared()
    quizTimerJob?.cancel()
    examTimerJob?.cancel()
  }
}
