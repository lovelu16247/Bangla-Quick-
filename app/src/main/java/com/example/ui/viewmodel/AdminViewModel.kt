package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AdminUserEntity
import com.example.data.model.AdSettings
import com.example.data.model.AdminLog
import com.example.data.model.AppSettings
import com.example.data.model.Category
import com.example.data.model.Exam
import com.example.data.model.Question
import com.example.data.model.User
import com.example.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) : AndroidViewModel(application) {
  val repository = QuizRepository(application)

  val currentAdmin: StateFlow<AdminUserEntity?> = repository.currentAdmin
  val appSettings: StateFlow<AppSettings> = repository.getAppSettingsFlow()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

  val adSettings: StateFlow<AdSettings> = repository.getAdSettingsFlow()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdSettings())

  val allCategories: StateFlow<List<Category>> = repository.getAllCategoriesFlow()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allQuestions: StateFlow<List<Question>> = repository.getAllQuestionsFlow()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allExams: StateFlow<List<Exam>> = repository.getAllExamsFlow()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allUsers: StateFlow<List<User>> = repository.getAllUsersFlow()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val adminLogs: StateFlow<List<AdminLog>> = repository.getAdminLogsFlow()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _dashboardStats = MutableStateFlow<Map<String, Any>>(emptyMap())
  val dashboardStats: StateFlow<Map<String, Any>> = _dashboardStats.asStateFlow()

  private val _statusMessage = MutableStateFlow<String?>(null)
  val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

  init {
    loadDashboardStats()
  }

  fun loadDashboardStats() {
    viewModelScope.launch {
      _dashboardStats.value = repository.getDashboardStats()
    }
  }

  fun login(user: String, pass: String, onSuccess: () -> Unit) {
    viewModelScope.launch {
      val res = repository.adminLogin(user, pass)
      if (res.isSuccess) {
        loadDashboardStats()
        onSuccess()
      } else {
        _statusMessage.value = res.exceptionOrNull()?.message ?: "অ্যাডমিন লগইন ব্যর্থ হয়েছে।"
      }
    }
  }

  fun logout() {
    repository.adminLogout()
  }

  fun clearStatusMessage() {
    _statusMessage.value = null
  }

  // Category management
  fun saveCategory(category: Category) {
    viewModelScope.launch {
      repository.saveCategory(category)
      _statusMessage.value = "ক্যাটাগরি সফলভাবে সংরক্ষণ করা হয়েছে।"
      loadDashboardStats()
    }
  }

  fun deleteCategory(id: Long) {
    viewModelScope.launch {
      repository.deleteCategory(id)
      _statusMessage.value = "ক্যাটাগরি মুছে ফেলা হয়েছে।"
      loadDashboardStats()
    }
  }

  // Question management
  fun saveQuestion(question: Question) {
    viewModelScope.launch {
      repository.saveQuestion(question)
      _statusMessage.value = "প্রশ্ন সফলভাবে সংরক্ষণ করা হয়েছে।"
      loadDashboardStats()
    }
  }

  fun duplicateQuestion(question: Question) {
    viewModelScope.launch {
      val copy = question.copy(id = 0, questionBn = "${question.questionBn} (কপি)")
      repository.saveQuestion(copy)
      _statusMessage.value = "প্রশ্ন ডুপ্লিকেট করা হয়েছে।"
      loadDashboardStats()
    }
  }

  fun deleteQuestion(id: Long) {
    viewModelScope.launch {
      repository.deleteQuestion(id)
      _statusMessage.value = "প্রশ্ন মুছে ফেলা হয়েছে।"
      loadDashboardStats()
    }
  }

  fun bulkImportQuestions(rawText: String) {
    viewModelScope.launch {
      try {
        val lines = rawText.trim().split("\n").filter { it.isNotBlank() }
        var imported = 0
        for (line in lines) {
          // Format: Question | Option1, Option2, Option3, Option4 | CorrectIndex (0-3) | Explanation | CategoryId
          val parts = line.split("|").map { it.trim() }
          if (parts.size >= 4) {
            val qText = parts[0]
            val opts = parts[1].split(",").map { it.trim() }
            val correctIdx = parts[2].toIntOrNull() ?: 0
            val exp = parts[3]
            val catId = parts.getOrNull(4)?.toLongOrNull() ?: 1L
            repository.saveQuestion(
              Question(
                questionBn = qText,
                options = opts,
                correctOptionIndex = correctIdx,
                explanationBn = exp,
                categoryId = catId,
                points = 10
              )
            )
            imported++
          }
        }
        _statusMessage.value = "সফলভাবে $imported টি প্রশ্ন ইম্পোর্ট করা হয়েছে।"
        loadDashboardStats()
      } catch (e: Exception) {
        _statusMessage.value = "ইম্পোর্ট ব্যর্থ: ${e.message}"
      }
    }
  }

  // Exam management
  fun saveExam(exam: Exam) {
    viewModelScope.launch {
      repository.saveExam(exam)
      _statusMessage.value = "পরীক্ষা সফলভাবে সংরক্ষণ করা হয়েছে।"
      loadDashboardStats()
    }
  }

  fun deleteExam(id: Long) {
    viewModelScope.launch {
      repository.deleteExam(id)
      _statusMessage.value = "পরীক্ষা মুছে ফেলা হয়েছে।"
      loadDashboardStats()
    }
  }

  // User management
  fun toggleBlockUser(userId: Long, block: Boolean) {
    viewModelScope.launch {
      repository.toggleBlockUser(userId, block)
      _statusMessage.value = if (block) "ব্যবহারকারীকে ব্লক করা হয়েছে।" else "ব্যবহারকারীকে আনব্লক করা হয়েছে।"
    }
  }

  fun adjustUserPoints(userId: Long, points: Int) {
    viewModelScope.launch {
      repository.adjustUserPoints(userId, points)
      _statusMessage.value = "ব্যবহারকারীর পয়েন্ট পরিবর্তন করা হয়েছে।"
    }
  }

  fun resetUserProgress(userId: Long) {
    viewModelScope.launch {
      repository.resetUserProgress(userId)
      _statusMessage.value = "ব্যবহারকারীর অগ্রগতি রিসেট করা হয়েছে।"
    }
  }

  // Notification broadcast
  fun broadcastNotification(title: String, message: String, type: String) {
    viewModelScope.launch {
      repository.sendNotification(title, message, type)
      _statusMessage.value = "সকল ব্যবহারকারীর নিকট নোটিফিকেশন পাঠানো হয়েছে।"
    }
  }

  // Ad settings
  fun saveAdSettings(settings: AdSettings) {
    viewModelScope.launch {
      repository.saveAdSettings(settings)
      _statusMessage.value = "বিজ্ঞাপন সেটিংস হালনাগাদ করা হয়েছে।"
    }
  }

  // App settings
  fun saveAppSettings(settings: AppSettings) {
    viewModelScope.launch {
      repository.saveAppSettings(settings)
      _statusMessage.value = "অ্যাপ কনফিগারেশন সফলভাবে সংরক্ষিত হয়েছে।"
    }
  }
}
