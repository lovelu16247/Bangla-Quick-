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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ads.SmartAdBanner
import com.example.data.model.Category
import com.example.data.model.Exam
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppTopBar
import com.example.ui.navigation.NavRoutes
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.GoldenAmberDark
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.PrimaryGreenDark
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TealCyan
import com.example.ui.viewmodel.QuizViewModel

@Composable
fun HomeScreen(
  viewModel: QuizViewModel,
  onNavigate: (String) -> Unit,
  onCategoryClick: (Category) -> Unit,
  onExamClick: (Exam) -> Unit
) {
  val user by viewModel.currentUser.collectAsStateWithLifecycle()
  val categories by viewModel.categories.collectAsStateWithLifecycle()
  val exams by viewModel.publishedExams.collectAsStateWithLifecycle()
  val recentAttempts by viewModel.userQuizHistory.collectAsStateWithLifecycle()
  val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()
  val notifications by viewModel.notifications.collectAsStateWithLifecycle()
  val unreadNotifs = notifications.count { !it.isRead }

  Scaffold(
    topBar = {
      AppTopBar(
        title = appSettings.appName,
        points = user?.totalPoints ?: 0,
        onNotificationsClick = { onNavigate(NavRoutes.NOTIFICATIONS) },
        unreadNotificationsCount = unreadNotifs,
        onAdminClick = { onNavigate(NavRoutes.ADMIN_LOGIN) }
      )
    },
    bottomBar = {
      AppBottomNav(
        currentRoute = NavRoutes.HOME,
        onNavigate = onNavigate
      )
    },
    modifier = Modifier.testTag("home_screen")
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentPadding = PaddingValues(bottom = 24.dp)
    ) {
      // 1. User Profile & Level Card
      item {
        UserProfileHeroCard(
          userName = user?.fullName ?: "কুইজ অভিযাত্রী",
          points = user?.totalPoints ?: 0,
          level = user?.currentLevel ?: 1,
          onLevelClick = { onNavigate(NavRoutes.LEVEL_SELECTION) }
        )
      }

      // 2. Daily Quiz & Quick Action Row
      item {
        DailyQuizBanner(
          onDailyQuizClick = {
            viewModel.startDailyQuiz()
            onNavigate(NavRoutes.QUIZ_PLAY)
          },
          onRewardClick = { onNavigate(NavRoutes.REWARDS) }
        )
      }

      // 3. Smart Ad Banner
      item {
        SmartAdBanner(
          adsManager = viewModel.adsManager,
          placementTag = "home_top"
        )
      }

      // 4. Continue Quiz / Categories Section
      item {
        SectionHeader(
          title = "জনপ্রিয় কুইজ ক্যাটাগরি",
          actionText = "সব দেখুন (${categories.size})",
          onActionClick = { onNavigate(NavRoutes.CATEGORIES) }
        )
      }

      item {
        LazyRow(
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(categories.take(8)) { category ->
            CategoryCard(
              category = category,
              onClick = {
                viewModel.startQuizForCategory(category)
                onNavigate(NavRoutes.QUIZ_PLAY)
              }
            )
          }
        }
      }

      // 5. Model Tests / Exam Section
      item {
        Spacer(modifier = Modifier.height(18.dp))
        SectionHeader(
          title = "বিসিএস ও চাকরি মডেল টেস্ট",
          actionText = "সকল পরীক্ষা",
          onActionClick = { onNavigate(NavRoutes.EXAM_LIST) }
        )
      }

      items(exams.take(2)) { exam ->
        ExamHomeCard(
          exam = exam,
          onStartExam = {
            viewModel.startExam(exam)
            onNavigate(NavRoutes.EXAM_PLAY)
          }
        )
      }

      // 6. Leaderboard Preview Banner
      item {
        Spacer(modifier = Modifier.height(18.dp))
        LeaderboardPreviewCard(
          onViewLeaderboard = { onNavigate(NavRoutes.LEADERBOARD) }
        )
      }

      // 7. Recent Quiz Results
      if (recentAttempts.isNotEmpty()) {
        item {
          Spacer(modifier = Modifier.height(18.dp))
          SectionHeader(
            title = "সাম্প্রতিক কুইজ ফলাফল",
            actionText = "ইতিহাস",
            onActionClick = { onNavigate(NavRoutes.QUIZ_HISTORY) }
          )
        }

        items(recentAttempts.take(3)) { attempt ->
          RecentAttemptCard(attempt = attempt)
        }
      }
    }
  }
}

@Composable
fun UserProfileHeroCard(
  userName: String,
  points: Int,
  level: Int,
  onLevelClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .testTag("user_profile_hero_card"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.linearGradient(
            colors = listOf(PrimaryGreen, PrimaryGreenDark, Color(0xFF042D1C))
          )
        )
        .padding(20.dp)
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(GoldenAmber),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = userName.take(1),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = userName,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "মোট পয়েন্ট: $points",
                fontSize = 13.sp,
                color = GoldenAmber
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.15f),
            modifier = Modifier.clickable(onClick = onLevelClick).testTag("home_level_badge")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.WorkspacePremium,
                contentDescription = "Level",
                tint = GoldenAmber,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "লেভেল $level",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Level progress bar
        val progressPercent = ((points % 150).toFloat() / 150f).coerceIn(0.1f, 1f)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "পরবর্তী লেভেলের অগ্রগতি",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f)
          )
          Text(
            text = "${(progressPercent * 100).toInt()}%",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = GoldenAmber
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
          progress = { progressPercent },
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
          color = GoldenAmber,
          trackColor = Color.White.copy(alpha = 0.2f)
        )
      }
    }
  }
}

@Composable
fun DailyQuizBanner(
  onDailyQuizClick: () -> Unit,
  onRewardClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Daily Quiz Button
    Card(
      modifier = Modifier
        .weight(1f)
        .clickable(onClick = onDailyQuizClick)
        .testTag("daily_quiz_button"),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
      Row(
        modifier = Modifier.padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(GoldenAmber),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = "Daily Quiz",
            tint = Color.Black,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "দৈনিক কুইজ",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSecondaryContainer
          )
          Text(
            text = "+৫০ পয়েন্ট বোনাস",
            fontSize = 11.sp,
            color = GoldenAmberDark
          )
        }
      }
    }

    // Rewards & Video Ad Button
    Card(
      modifier = Modifier
        .weight(1f)
        .clickable(onClick = onRewardClick)
        .testTag("rewards_button"),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
      Row(
        modifier = Modifier.padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(PrimaryGreen),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Stars,
            contentDescription = "Rewards",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "রিওয়ার্ড আর্ন",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
          Text(
            text = "বিজ্ঞাপন দেখে পয়েন্ট",
            fontSize = 11.sp,
            color = PrimaryGreen
          )
        }
      }
    }
  }
}

@Composable
fun SectionHeader(
  title: String,
  actionText: String? = null,
  onActionClick: () -> Unit = {}
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = title,
      fontWeight = FontWeight.Bold,
      fontSize = 16.sp,
      color = MaterialTheme.colorScheme.onBackground
    )
    if (actionText != null) {
      TextButton(onClick = onActionClick) {
        Text(
          text = actionText,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.primary
        )
      }
    }
  }
}

@Composable
fun CategoryCard(
  category: Category,
  onClick: () -> Unit
) {
  val cardColor = when (category.id.toInt() % 4) {
    0 -> PrimaryGreen
    1 -> Color(0xFF0284C7)
    2 -> Color(0xFFD97706)
    else -> Color(0xFF7C3AED)
  }

  Surface(
    modifier = Modifier
      .width(130.dp)
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick)
      .testTag("category_card_${category.id}"),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 2.dp,
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.Start
    ) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(cardColor.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Quiz,
          contentDescription = category.nameBn,
          tint = cardColor,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = category.nameBn,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Text(
        text = "${category.questionCount}টি প্রশ্ন",
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
fun ExamHomeCard(
  exam: Exam,
  onStartExam: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .testTag("exam_card_${exam.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFFDC2626).copy(alpha = 0.12f)
        ) {
          Text(
            text = "লাইভ এক্সাম",
            color = Color(0xFFDC2626),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
        Text(
          text = "সময়: ${exam.durationMinutes} মি. | ${exam.totalQuestions} প্রশ্ন",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = exam.titleBn,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        color = MaterialTheme.colorScheme.onSurface
      )

      Text(
        text = exam.descriptionBn,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "নেগেটিভ মার্ক: -${exam.negativeMarkingPerWrong}",
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.error
        )

        Button(
          onClick = onStartExam,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
          modifier = Modifier.testTag("start_exam_button_${exam.id}")
        ) {
          Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("পরীক্ষা শুরু", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun LeaderboardPreviewCard(
  onViewLeaderboard: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .clickable(onClick = onViewLeaderboard)
      .testTag("leaderboard_preview_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(GoldenAmber),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = "Leaderboard",
            tint = Color.Black,
            modifier = Modifier.size(24.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "জাতীয় মেধা তালিকা",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
          Text(
            text = "সারা দেশের প্রতিযোগীদের মাঝে আপনার অবস্থান দেখুন",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(20.dp)
      )
    }
  }
}

@Composable
fun RecentAttemptCard(attempt: com.example.data.model.QuizAttemptResult) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = attempt.quizTitle,
          fontWeight = FontWeight.SemiBold,
          fontSize = 13.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = "সঠিক: ${attempt.correctAnswers}/${attempt.totalQuestions} • স্কোর: +${attempt.totalScoreEarned}",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (attempt.isPassed) SuccessGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
      ) {
        Text(
          text = "${attempt.accuracyPercent.toInt()}%",
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          color = if (attempt.isPassed) SuccessGreen else MaterialTheme.colorScheme.error,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
      }
    }
  }
}
