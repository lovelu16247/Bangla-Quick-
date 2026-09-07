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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Level
import com.example.ui.components.AppTopBar
import com.example.ui.navigation.NavRoutes
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.QuizViewModel

@Composable
fun LevelSelectionScreen(
  viewModel: QuizViewModel,
  onNavigateBack: () -> Unit,
  onNavigateToQuiz: () -> Unit
) {
  val levels by viewModel.levels.collectAsStateWithLifecycle()
  val user by viewModel.currentUser.collectAsStateWithLifecycle()

  Scaffold(
    topBar = {
      AppTopBar(
        title = "১০০টি লেভেল চ্যালেঞ্জ",
        showBack = true,
        onBack = onNavigateBack,
        points = user?.totalPoints ?: 0
      )
    },
    modifier = Modifier.testTag("level_selection_screen")
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      // Header banner explaining unlock rules
      Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = GoldenAmber,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "প্রতিটি লেভেলে ৬০% বা তার বেশি সঠিক উত্তর দিলে পরবর্তী লেভেল স্বয়ংক্রিয়ভাবে আনলক হবে।",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            lineHeight = 16.sp
          )
        }
      }

      LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(levels) { level ->
          LevelGridItem(
            level = level,
            onClick = {
              if (level.isUnlocked) {
                viewModel.startQuizForLevel(level)
                onNavigateToQuiz()
              }
            }
          )
        }
      }
    }
  }
}

@Composable
fun LevelGridItem(
  level: Level,
  onClick: () -> Unit
) {
  val isUnlocked = level.isUnlocked
  val isCompleted = level.isCompleted

  val containerColor = when {
    isCompleted -> SuccessGreen.copy(alpha = 0.15f)
    isUnlocked -> MaterialTheme.colorScheme.surface
    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
  }

  val borderColor = when {
    isCompleted -> SuccessGreen
    isUnlocked -> PrimaryGreen
    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
  }

  Surface(
    modifier = Modifier
      .size(76.dp)
      .clip(RoundedCornerShape(16.dp))
      .clickable(enabled = isUnlocked, onClick = onClick)
      .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
      .testTag("level_item_${level.levelNumber}"),
    color = containerColor,
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      if (isCompleted) {
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = "Completed",
          tint = SuccessGreen,
          modifier = Modifier.size(20.dp)
        )
      } else if (!isUnlocked) {
        Icon(
          imageVector = Icons.Default.Lock,
          contentDescription = "Locked",
          tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
          modifier = Modifier.size(20.dp)
        )
      }

      Text(
        text = "${level.levelNumber}",
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
      )

      if (isCompleted && level.bestScore > 0) {
        Text(
          text = "${level.bestScore}%",
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          color = SuccessGreen
        )
      } else if (isUnlocked && !isCompleted) {
        Text(
          text = "খেলুন",
          fontSize = 9.sp,
          fontWeight = FontWeight.SemiBold,
          color = PrimaryGreen
        )
      }
    }
  }
}
