package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppTopBar
import com.example.ui.navigation.NavRoutes
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.PrimaryGreenDark
import com.example.ui.viewmodel.QuizViewModel

@Composable
fun ProfileScreen(
  viewModel: QuizViewModel,
  onNavigate: (String) -> Unit
) {
  val user by viewModel.currentUser.collectAsStateWithLifecycle()

  Scaffold(
    topBar = {
      AppTopBar(
        title = "আমার প্রোফাইল",
        showBack = false,
        points = user?.totalPoints ?: 0
      )
    },
    bottomBar = {
      AppBottomNav(
        currentRoute = NavRoutes.PROFILE,
        onNavigate = onNavigate
      )
    },
    modifier = Modifier.testTag("profile_screen")
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // User Profile Header Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.verticalGradient(
                  listOf(PrimaryGreen, PrimaryGreenDark)
                )
              )
              .padding(20.dp)
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
              Box(
                modifier = Modifier
                  .size(70.dp)
                  .clip(CircleShape)
                  .background(GoldenAmber),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = user?.fullName?.take(1) ?: "ইউ",
                  fontSize = 30.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.Black
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = user?.fullName ?: "কুইজ শিক্ষার্থী",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )

              Text(
                text = "@${user?.username ?: "user"}",
                fontSize = 13.sp,
                color = GoldenAmber
              )

              Spacer(modifier = Modifier.height(16.dp))

              // Stats Row inside Header
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
              ) {
                ProfileStatCol(label = "মোট পয়েন্ট", value = "${user?.totalPoints ?: 0}")
                ProfileStatCol(label = "বর্তমান লেভেল", value = "লেভেল ${user?.currentLevel ?: 1}")
                ProfileStatCol(label = "কুইজ সম্পন্ন", value = "${user?.completedQuizzes ?: 0}")
                ProfileStatCol(label = "মডেল টেস্ট", value = "${user?.completedExams ?: 0}")
              }
            }
          }
        }
      }

      // Quick Nav Links
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(8.dp)) {
            ProfileMenuItem(
              icon = Icons.Default.History,
              title = "কুইজ অনুশীলনের ইতিহাস",
              onClick = { onNavigate(NavRoutes.QUIZ_HISTORY) },
              testTag = "menu_quiz_history"
            )
            ProfileMenuItem(
              icon = Icons.Default.CardGiftcard,
              title = "রিওয়ার্ড ও পয়েন্ট বোনাস",
              onClick = { onNavigate(NavRoutes.REWARDS) },
              testTag = "menu_rewards"
            )
            ProfileMenuItem(
              icon = Icons.Default.Notifications,
              title = "নোটিফিকেশন ও বার্তা",
              onClick = { onNavigate(NavRoutes.NOTIFICATIONS) },
              testTag = "menu_notifications"
            )
            ProfileMenuItem(
              icon = Icons.Default.Settings,
              title = "সেটিংস",
              onClick = { onNavigate(NavRoutes.SETTINGS) },
              testTag = "menu_settings"
            )
            ProfileMenuItem(
              icon = Icons.Default.Info,
              title = "আমাদের সম্পর্কে ও গোপনীয়তা নীতি",
              onClick = { onNavigate(NavRoutes.ABOUT_PRIVACY) },
              testTag = "menu_about_privacy"
            )
          }
        }
      }

      // Admin Panel Direct Access Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigate(NavRoutes.ADMIN_LOGIN) }
            .testTag("admin_panel_entry_card"),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "অ্যাডমিন প্যানেল",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                  text = "প্রশ্ন, ক্যাটাগরি, পরীক্ষা ও বিজ্ঞাপন কনফিগারেশন",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
              }
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
          }
        }
      }

      // Logout button
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              viewModel.logout()
              onNavigate(NavRoutes.AUTH)
            }
            .testTag("profile_logout_button"),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f))
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(Icons.Default.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "লগআউট করুন",
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.error
            )
          }
        }
      }
    }
  }
}

@Composable
fun ProfileStatCol(label: String, value: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = value,
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      color = Color.White
    )
    Text(
      text = label,
      fontSize = 10.sp,
      color = Color.White.copy(alpha = 0.8f)
    )
  }
}

@Composable
fun ProfileMenuItem(
  icon: ImageVector,
  title: String,
  onClick: () -> Unit,
  testTag: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 12.dp, vertical = 12.dp)
      .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
      Spacer(modifier = Modifier.width(12.dp))
      Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
    Icon(
      imageVector = Icons.AutoMirrored.Filled.ArrowForward,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
      modifier = Modifier.size(16.dp)
    )
  }
}
