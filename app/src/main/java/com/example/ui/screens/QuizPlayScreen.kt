package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.QuizOptionItem
import com.example.ui.navigation.NavRoutes
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizPlayScreen(
  viewModel: QuizViewModel,
  onNavigateBack: () -> Unit,
  onQuizFinished: () -> Unit
) {
  val title by viewModel.activeQuizTitle.collectAsStateWithLifecycle()
  val questions by viewModel.quizQuestions.collectAsStateWithLifecycle()
  val currentIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
  val selectedOption by viewModel.selectedOptionIndex.collectAsStateWithLifecycle()
  val isSubmitted by viewModel.isAnswerSubmitted.collectAsStateWithLifecycle()
  val timerSeconds by viewModel.quizTimerSeconds.collectAsStateWithLifecycle()
  val earnedPoints by viewModel.quizEarnedPoints.collectAsStateWithLifecycle()
  val isCompleted by viewModel.isQuizCompleted.collectAsStateWithLifecycle()

  var showExitDialog by remember { mutableStateOf(false) }

  val currentQuestion = questions.getOrNull(currentIndex)

  LaunchedEffect(isCompleted) {
    if (isCompleted) {
      // Smart interstitial ad check before showing result
      viewModel.adsManager.showSmartInterstitial {
        onQuizFinished()
      }
    }
  }

  if (showExitDialog) {
    AlertDialog(
      onDismissRequest = { showExitDialog = false },
      title = { Text("কুইজ থেকে প্রস্থান করতে চান?") },
      text = { Text("এখন বের হয়ে গেলে বর্তমান অগ্রগতি সংরক্ষিত নাও হতে পারে।") },
      confirmButton = {
        Button(
          onClick = {
            showExitDialog = false
            onNavigateBack()
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("বের হয়ে যান")
        }
      },
      dismissButton = {
        TextButton(onClick = { showExitDialog = false }) {
          Text("চালিয়ে যান")
        }
      }
    )
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = title,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1
            )
            Text(
              text = "প্রশ্ন: ${currentIndex + 1} / ${questions.size}",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = { showExitDialog = true },
            modifier = Modifier.testTag("quiz_exit_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Exit")
          }
        },
        actions = {
          // Points badge
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = GoldenAmber.copy(alpha = 0.15f),
            modifier = Modifier.padding(end = 12.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Stars, contentDescription = null, tint = GoldenAmber, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("+$earnedPoints", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldenAmber)
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    },
    modifier = Modifier.testTag("quiz_play_screen")
  ) { padding ->
    if (currentQuestion == null) {
      Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        Text("প্রশ্ন লোড হচ্ছে...")
      }
      return@Scaffold
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      // Progress Bar
      val progress = if (questions.isNotEmpty()) (currentIndex.toFloat() / questions.size) else 0f
      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier.fillMaxWidth().height(4.dp),
        color = PrimaryGreen,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
      )

      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Countdown Timer & Difficulty
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = "কঠিনতা: ${currentQuestion.difficulty}",
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }

          // Timer Pill
          val timerColor = if (timerSeconds <= 5) MaterialTheme.colorScheme.error else PrimaryGreen
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = timerColor.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(1.dp, timerColor.copy(alpha = 0.4f)),
            modifier = Modifier.testTag("quiz_timer_pill")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Timer, contentDescription = null, tint = timerColor, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "${timerSeconds}s",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = timerColor
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Question Card
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("quiz_question_card"),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
          Column(modifier = Modifier.padding(20.dp)) {
            Text(
              text = currentQuestion.questionBn,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              lineHeight = 26.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Options List
        currentQuestion.options.forEachIndexed { optIndex, optText ->
          val isSelected = selectedOption == optIndex
          val isCorrect = if (isSubmitted) (optIndex == currentQuestion.correctOptionIndex) else null

          QuizOptionItem(
            optionIndex = optIndex,
            optionText = optText,
            isSelected = isSelected,
            isCorrect = isCorrect,
            isSubmitted = isSubmitted,
            onClick = { viewModel.selectOption(optIndex) }
          )
        }

        // Bengali Explanation Box (Revealed when submitted)
        AnimatedVisibility(visible = isSubmitted) {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 16.dp)
              .testTag("quiz_explanation_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f))
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "সঠিক উত্তরের ব্যাখ্যা:",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSecondaryContainer
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = currentQuestion.explanationBn,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Next Button
        AnimatedVisibility(visible = isSubmitted) {
          Button(
            onClick = { viewModel.nextQuestion() },
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("quiz_next_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
          ) {
            Text(
              text = if (currentIndex < questions.size - 1) "পরবর্তী প্রশ্ন" else "ফলাফল দেখুন",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
          }
        }
      }
    }
  }
}
