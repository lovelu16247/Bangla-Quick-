package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ads.SmartAdBanner
import com.example.ui.navigation.NavRoutes
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.PrimaryGreenDark
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.QuizViewModel

@Composable
fun ResultScreen(
  viewModel: QuizViewModel,
  onNavigateHome: () -> Unit,
  onNavigateLeaderboard: () -> Unit
) {
  val quizResult by viewModel.lastQuizResult.collectAsStateWithLifecycle()
  val examResult by viewModel.lastExamResult.collectAsStateWithLifecycle()

  val isExam = examResult != null
  val isPassed = if (isExam) examResult?.isPassed == true else quizResult?.isPassed == true
  val title = if (isExam) examResult?.examTitle ?: "পরীক্ষার ফলাফল" else quizResult?.quizTitle ?: "কুইজ ফলাফল"
  val total = if (isExam) examResult?.totalQuestions ?: 0 else quizResult?.totalQuestions ?: 0
  val correct = if (isExam) examResult?.correctAnswers ?: 0 else quizResult?.correctAnswers ?: 0
  val wrong = if (isExam) examResult?.wrongAnswers ?: 0 else quizResult?.wrongAnswers ?: 0
  val skipped = if (isExam) examResult?.skippedQuestions ?: 0 else quizResult?.skippedQuestions ?: 0
  val accuracy = if (isExam) examResult?.accuracyPercent ?: 0f else quizResult?.accuracyPercent ?: 0f
  val pointsEarned = if (isExam) (examResult?.finalScore?.times(10))?.toInt() ?: 0 else quizResult?.totalScoreEarned ?: 0

  Scaffold(
    modifier = Modifier.testTag("result_screen")
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(10.dp))

      // Celebration or encouragement Icon
      Box(
        modifier = Modifier
          .size(90.dp)
          .clip(CircleShape)
          .background(
            if (isPassed) {
              Brush.radialGradient(listOf(SuccessGreen.copy(alpha = 0.3f), Color.Transparent))
            } else {
              Brush.radialGradient(listOf(GoldenAmber.copy(alpha = 0.3f), Color.Transparent))
            }
          ),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .size(70.dp)
            .clip(CircleShape)
            .background(if (isPassed) SuccessGreen else GoldenAmber),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isPassed) Icons.Default.EmojiEvents else Icons.Default.Replay,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(38.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = if (isPassed) "অভিনন্দন! আপনি সফল হয়েছেন!" else "ভালো চেষ্টা! আরও অনুশীলন প্রয়োজন",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = if (isPassed) SuccessGreen else MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center
      )

      Text(
        text = title,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
      )

      // Score Highlight Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "${accuracy.toInt()}%",
            fontSize = 44.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isPassed) PrimaryGreen else MaterialTheme.colorScheme.onSurface
          )

          Text(
            text = "সঠিকতার হার (Accuracy)",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          if (isExam) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceAround
            ) {
              Text(
                text = "প্রাপ্ত নম্বর: ${examResult?.rawScore}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "নেগেটিভ কর্তন: -${examResult?.negativeDeduction}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.error
              )
              Text(
                text = "চূড়ান্ত স্কোর: ${examResult?.finalScore}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen
              )
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Breakdown stats row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            StatPill(label = "সঠিক", value = "$correct", color = SuccessGreen)
            StatPill(label = "ভুল", value = "$wrong", color = MaterialTheme.colorScheme.error)
            StatPill(label = "বাকি", value = "$skipped", color = MaterialTheme.colorScheme.onSurfaceVariant)
            StatPill(label = "পয়েন্ট", value = "+$pointsEarned", color = GoldenAmber)
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Smart Ad Banner
      SmartAdBanner(
        adsManager = viewModel.adsManager,
        placementTag = "result_screen"
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Action Buttons
      Button(
        onClick = onNavigateHome,
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("result_home_button"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
      ) {
        Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("হোম পেজে ফিরুন", fontSize = 15.sp, fontWeight = FontWeight.Bold)
      }

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedButton(
        onClick = onNavigateLeaderboard,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("result_leaderboard_button"),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(Icons.Default.Leaderboard, contentDescription = null, modifier = Modifier.size(18.dp), tint = PrimaryGreen)
        Spacer(modifier = Modifier.width(8.dp))
        Text("লিডারবোর্ডে অবস্থান দেখুন", fontSize = 14.sp, color = PrimaryGreen, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
fun StatPill(
  label: String,
  value: String,
  color: Color
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = color.copy(alpha = 0.12f),
    modifier = Modifier.padding(horizontal = 4.dp)
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = value,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = color
      )
      Text(
        text = label,
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
