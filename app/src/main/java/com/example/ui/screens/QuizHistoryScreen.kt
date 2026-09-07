package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.QuizAttemptResult
import com.example.ui.components.AppTopBar
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.QuizViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun QuizHistoryScreen(
  viewModel: QuizViewModel,
  onNavigateBack: () -> Unit
) {
  val history by viewModel.userQuizHistory.collectAsStateWithLifecycle()
  val user by viewModel.currentUser.collectAsStateWithLifecycle()

  Scaffold(
    topBar = {
      AppTopBar(
        title = "কুইজ অনুশীলনের ইতিহাস",
        showBack = true,
        onBack = onNavigateBack,
        points = user?.totalPoints ?: 0
      )
    },
    modifier = Modifier.testTag("quiz_history_screen")
  ) { padding ->
    if (history.isEmpty()) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = "এখনো কোন কুইজ সম্পন্ন হয়নি।",
          fontSize = 16.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(history) { attempt ->
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = attempt.quizTitle,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.weight(1f)
                )

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (attempt.isPassed) SuccessGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                ) {
                  Text(
                    text = if (attempt.isPassed) "উত্তীর্ণ (${attempt.accuracyPercent.toInt()}%)" else "অনুত্তীর্ণ (${attempt.accuracyPercent.toInt()}%)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (attempt.isPassed) SuccessGreen else MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "সঠিক: ${attempt.correctAnswers} | ভুল: ${attempt.wrongAnswers} | বাকি: ${attempt.skippedQuestions}",
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                  text = "+${attempt.totalScoreEarned} পয়েন্ট",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = PrimaryGreen
                )
              }

              Spacer(modifier = Modifier.height(4.dp))

              val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(attempt.timestamp))
              Text(
                text = dateStr,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
              )
            }
          }
        }
      }
    }
  }
}
