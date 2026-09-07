package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamPlayScreen(
  viewModel: QuizViewModel,
  onNavigateBack: () -> Unit,
  onExamFinished: () -> Unit
) {
  val exam by viewModel.activeExam.collectAsStateWithLifecycle()
  val questions by viewModel.examQuestions.collectAsStateWithLifecycle()
  val currentIndex by viewModel.examCurrentIndex.collectAsStateWithLifecycle()
  val answersMap by viewModel.examAnswers.collectAsStateWithLifecycle()
  val remainingSeconds by viewModel.examRemainingSeconds.collectAsStateWithLifecycle()
  val lastResult by viewModel.lastExamResult.collectAsStateWithLifecycle()

  var showSubmitDialog by remember { mutableStateOf(false) }
  var showExitDialog by remember { mutableStateOf(false) }

  LaunchedEffect(lastResult) {
    if (lastResult != null) {
      viewModel.adsManager.showSmartInterstitial {
        onExamFinished()
      }
    }
  }

  val minutes = remainingSeconds / 60
  val seconds = remainingSeconds % 60
  val timeString = "%02d:%02d".format(minutes, seconds)

  val currentQuestion = questions.getOrNull(currentIndex)
  val selectedAnswer = answersMap[currentIndex]

  if (showExitDialog) {
    AlertDialog(
      onDismissRequest = { showExitDialog = false },
      title = { Text("পরীক্ষা বাতিল করতে চান?") },
      text = { Text("পরীক্ষা সম্পন্ন না করে বের হলে আপনার স্কোর বিবেচনা করা হবে না।") },
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

  if (showSubmitDialog) {
    val answeredCount = answersMap.values.count { it != -1 }
    val unansweredCount = questions.size - answeredCount

    AlertDialog(
      onDismissRequest = { showSubmitDialog = false },
      title = { Text("পরীক্ষা জমা দিতে চান?") },
      text = {
        Column {
          Text("উত্তর দেওয়া হয়েছে: $answeredCount টি")
          Text("উত্তর বাকি: $unansweredCount টি")
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            "ভুল উত্তরের জন্য নেগেটিভ মার্ক প্রযোজ্য। নিশ্চিত হলে 'জমা দিন' চাপুন।",
            color = MaterialTheme.colorScheme.error,
            fontSize = 12.sp
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showSubmitDialog = false
            viewModel.submitExam()
          },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
        ) {
          Text("জমা দিন")
        }
      },
      dismissButton = {
        TextButton(onClick = { showSubmitDialog = false }) {
          Text("ফিরে যান")
        }
      }
    )
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = exam?.titleBn ?: "মডেল টেস্ট",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
          )
        },
        navigationIcon = {
          IconButton(
            onClick = { showExitDialog = true },
            modifier = Modifier.testTag("exam_exit_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Exit")
          }
        },
        actions = {
          // Timer pill
          val timerColor = if (remainingSeconds < 120) MaterialTheme.colorScheme.error else GoldenAmber
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = timerColor.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(1.dp, timerColor),
            modifier = Modifier.padding(end = 12.dp).testTag("exam_timer_display")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Timer, contentDescription = null, tint = timerColor, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(timeString, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = timerColor)
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    },
    bottomBar = {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedButton(
            onClick = {
              if (currentIndex > 0) viewModel.setExamCurrentIndex(currentIndex - 1)
            },
            enabled = currentIndex > 0,
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("পূর্ববর্তী")
          }

          Button(
            onClick = { showSubmitDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("submit_exam_action_button")
          ) {
            Text("পরীক্ষা জমা দিন", fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = {
              if (currentIndex < questions.size - 1) viewModel.setExamCurrentIndex(currentIndex + 1)
            },
            enabled = currentIndex < questions.size - 1,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
          ) {
            Text("পরবর্তী")
          }
        }
      }
    },
    modifier = Modifier.testTag("exam_play_screen")
  ) { padding ->
    if (currentQuestion == null) {
      Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        Text("পরীক্ষার প্রশ্নপত্র প্রস্তুত হচ্ছে...")
      }
      return@Scaffold
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      // Question Navigator Strip
      LazyRow(
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
      ) {
        itemsIndexed(questions) { idx, _ ->
          val isAnswered = answersMap.containsKey(idx) && answersMap[idx] != -1
          val isCurrent = idx == currentIndex

          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(
                when {
                  isCurrent -> PrimaryGreen
                  isAnswered -> SuccessGreen
                  else -> MaterialTheme.colorScheme.surface
                }
              )
              .border(
                1.dp,
                if (isCurrent) GoldenAmber else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                CircleShape
              )
              .clickable { viewModel.setExamCurrentIndex(idx) },
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "${idx + 1}",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (isCurrent || isAnswered) Color.White else MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }

      // Question body & options
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(16.dp)
      ) {
        // Exam Question Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "প্রশ্ন ${currentIndex + 1} / ${questions.size}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen
              )
              Text(
                text = "নেগেটিভ মার্ক: -${exam?.negativeMarkingPerWrong ?: 0.25f}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.error
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = currentQuestion.questionBn,
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              lineHeight = 24.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Options List (User can change answers until submission)
        currentQuestion.options.forEachIndexed { optIndex, optText ->
          val isSelected = selectedAnswer == optIndex
          val prefix = when (optIndex) {
            0 -> "ক"
            1 -> "খ"
            2 -> "গ"
            else -> "ঘ"
          }

          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 5.dp)
              .clip(RoundedCornerShape(14.dp))
              .clickable { viewModel.selectExamAnswer(currentIndex, optIndex) }
              .border(
                1.5.dp,
                if (isSelected) PrimaryGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                RoundedCornerShape(14.dp)
              )
              .testTag("exam_option_$optIndex"),
            color = if (isSelected) PrimaryGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(14.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(if (isSelected) PrimaryGreen else MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = prefix,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Text(
                text = optText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    }
  }
}
