package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ui.screens.AboutPrivacyScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.DailyQuizScreen
import com.example.ui.screens.ExamListScreen
import com.example.ui.screens.ExamPlayScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.LevelSelectionScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.QuizHistoryScreen
import com.example.ui.screens.QuizPlayScreen
import com.example.ui.screens.ResultScreen
import com.example.ui.screens.RewardsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.admin.AdminCategoryExamScreen
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.admin.AdminLoginScreen
import com.example.ui.screens.admin.AdminQuestionScreen
import com.example.ui.screens.admin.AdminUserAdSettingsScreen
import com.example.ui.viewmodel.AdminViewModel
import com.example.ui.viewmodel.QuizViewModel

@Composable
fun AppNavigation(
  navController: NavHostController,
  quizViewModel: QuizViewModel,
  adminViewModel: AdminViewModel
) {
  NavHost(
    navController = navController,
    startDestination = NavRoutes.SPLASH
  ) {
    // 1. Splash Screen
    composable(NavRoutes.SPLASH) {
      SplashScreen(
        onNavigateNext = {
          navController.navigate(NavRoutes.ONBOARDING) {
            popUpTo(NavRoutes.SPLASH) { inclusive = true }
          }
        }
      )
    }

    // 2. Onboarding Screen
    composable(NavRoutes.ONBOARDING) {
      OnboardingScreen(
        onFinishOnboarding = {
          navController.navigate(NavRoutes.AUTH) {
            popUpTo(NavRoutes.ONBOARDING) { inclusive = true }
          }
        }
      )
    }

    // 3. Auth Screen (Login & Registration)
    composable(NavRoutes.AUTH) {
      AuthScreen(
        repository = quizViewModel.repository,
        onAuthSuccess = {
          navController.navigate(NavRoutes.HOME) {
            popUpTo(NavRoutes.AUTH) { inclusive = true }
          }
        }
      )
    }

    // 4. Home Screen
    composable(NavRoutes.HOME) {
      HomeScreen(
        viewModel = quizViewModel,
        onNavigate = { route -> navController.navigate(route) },
        onCategoryClick = { category ->
          quizViewModel.startQuizForCategory(category)
          navController.navigate(NavRoutes.QUIZ_PLAY)
        },
        onExamClick = { exam ->
          quizViewModel.startExam(exam)
          navController.navigate(NavRoutes.EXAM_PLAY)
        }
      )
    }

    // 5. Categories Screen
    composable(NavRoutes.CATEGORIES) {
      CategoriesScreen(
        viewModel = quizViewModel,
        onNavigate = { route -> navController.navigate(route) },
        onCategorySelected = { category ->
          quizViewModel.startQuizForCategory(category)
          navController.navigate(NavRoutes.QUIZ_PLAY)
        }
      )
    }

    // 6. Level Selection Screen (1 to 100)
    composable(NavRoutes.LEVEL_SELECTION) {
      LevelSelectionScreen(
        viewModel = quizViewModel,
        onNavigateBack = { navController.popBackStack() },
        onNavigateToQuiz = {
          navController.navigate(NavRoutes.QUIZ_PLAY)
        }
      )
    }

    // 7. Quiz Play Screen
    composable(NavRoutes.QUIZ_PLAY) {
      QuizPlayScreen(
        viewModel = quizViewModel,
        onNavigateBack = { navController.popBackStack() },
        onQuizFinished = {
          navController.navigate(NavRoutes.RESULT) {
            popUpTo(NavRoutes.QUIZ_PLAY) { inclusive = true }
          }
        }
      )
    }

    // 8. Exam List Screen
    composable(NavRoutes.EXAM_LIST) {
      ExamListScreen(
        viewModel = quizViewModel,
        onNavigate = { route -> navController.navigate(route) }
      )
    }

    // 9. Exam Play Screen
    composable(NavRoutes.EXAM_PLAY) {
      ExamPlayScreen(
        viewModel = quizViewModel,
        onNavigateBack = { navController.popBackStack() },
        onExamFinished = {
          navController.navigate(NavRoutes.RESULT) {
            popUpTo(NavRoutes.EXAM_PLAY) { inclusive = true }
          }
        }
      )
    }

    // 10. Result Screen
    composable(NavRoutes.RESULT) {
      ResultScreen(
        viewModel = quizViewModel,
        onNavigateHome = {
          navController.navigate(NavRoutes.HOME) {
            popUpTo(NavRoutes.HOME) { inclusive = false }
          }
        },
        onNavigateLeaderboard = {
          navController.navigate(NavRoutes.LEADERBOARD)
        }
      )
    }

    // 11. Leaderboard Screen
    composable(NavRoutes.LEADERBOARD) {
      LeaderboardScreen(
        viewModel = quizViewModel,
        onNavigate = { route -> navController.navigate(route) }
      )
    }

    // 12. Daily Quiz Screen
    composable(NavRoutes.DAILY_QUIZ) {
      DailyQuizScreen(
        viewModel = quizViewModel,
        onNavigateBack = { navController.popBackStack() },
        onStartDailyQuiz = {
          navController.navigate(NavRoutes.QUIZ_PLAY)
        }
      )
    }

    // 13. Quiz History Screen
    composable(NavRoutes.QUIZ_HISTORY) {
      QuizHistoryScreen(
        viewModel = quizViewModel,
        onNavigateBack = { navController.popBackStack() }
      )
    }

    // 14. Profile Screen
    composable(NavRoutes.PROFILE) {
      ProfileScreen(
        viewModel = quizViewModel,
        onNavigate = { route -> navController.navigate(route) }
      )
    }

    // 15. Settings Screen
    composable(NavRoutes.SETTINGS) {
      SettingsScreen(
        onNavigateBack = { navController.popBackStack() }
      )
    }

    // 16. Notifications Screen
    composable(NavRoutes.NOTIFICATIONS) {
      NotificationsScreen(
        viewModel = quizViewModel,
        onNavigateBack = { navController.popBackStack() }
      )
    }

    // 17. Rewards Screen
    composable(NavRoutes.REWARDS) {
      RewardsScreen(
        viewModel = quizViewModel,
        onNavigateBack = { navController.popBackStack() }
      )
    }

    // 18. About / Privacy Screen
    composable(NavRoutes.ABOUT_PRIVACY) {
      AboutPrivacyScreen(
        onNavigateBack = { navController.popBackStack() }
      )
    }

    // ================= ADMIN ROUTES =================
    composable(NavRoutes.ADMIN_LOGIN) {
      AdminLoginScreen(
        viewModel = adminViewModel,
        onNavigateBack = { navController.popBackStack() },
        onLoginSuccess = {
          navController.navigate(NavRoutes.ADMIN_DASHBOARD) {
            popUpTo(NavRoutes.ADMIN_LOGIN) { inclusive = true }
          }
        }
      )
    }

    composable(NavRoutes.ADMIN_DASHBOARD) {
      AdminDashboardScreen(
        viewModel = adminViewModel,
        onNavigateBack = { navController.popBackStack() },
        onNavigateToQuestions = { navController.navigate(NavRoutes.ADMIN_QUESTIONS) },
        onNavigateToCategoriesExams = { navController.navigate(NavRoutes.ADMIN_CATEGORIES_EXAMS) },
        onNavigateToUsersAds = { navController.navigate(NavRoutes.ADMIN_USERS_ADS) }
      )
    }

    composable(NavRoutes.ADMIN_QUESTIONS) {
      AdminQuestionScreen(
        viewModel = adminViewModel,
        onNavigateBack = { navController.popBackStack() }
      )
    }

    composable(NavRoutes.ADMIN_CATEGORIES_EXAMS) {
      AdminCategoryExamScreen(
        viewModel = adminViewModel,
        onNavigateBack = { navController.popBackStack() }
      )
    }

    composable(NavRoutes.ADMIN_USERS_ADS) {
      AdminUserAdSettingsScreen(
        viewModel = adminViewModel,
        onNavigateBack = { navController.popBackStack() }
      )
    }
  }
}
