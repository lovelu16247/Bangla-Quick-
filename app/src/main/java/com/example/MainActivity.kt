package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AdminViewModel
import com.example.ui.viewmodel.QuizViewModel

class MainActivity : ComponentActivity() {

  private val quizViewModel: QuizViewModel by viewModels()
  private val adminViewModel: AdminViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val navController = rememberNavController()
        AppNavigation(
          navController = navController,
          quizViewModel = quizViewModel,
          adminViewModel = adminViewModel
        )
      }
    }
  }
}
