package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.ui.components.AppTopBar
import com.example.ui.navigation.NavRoutes
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.QuizViewModel

@Composable
fun DailyQuizScreen(
  viewModel: QuizViewModel,
  onNavigateBack: () -> Unit,
  onStartDailyQuiz: () -> Unit
) {
  val user by viewModel.currentUser.collectAsStateWithLifecycle()

  Scaffold(
    topBar = {
      AppTopBar(
        title = "আজকের দৈনিক কুইজ",
        showBack = true,
        onBack = onNavigateBack,
        points = user?.totalPoints ?: 0
      )
    },
    modifier = Modifier.testTag("daily_quiz_screen")
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(20.dp))

        Box(
          modifier = Modifier
            .size(90.dp)
            .clip(CircleShape)
            .background(GoldenAmber.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = "Daily Quiz",
            tint = GoldenAmber,
            modifier = Modifier.size(48.dp)
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
          text = "দৈনিক মেধা যাচাই চ্যালেঞ্জ",
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "প্রতিদিন ১০টি নির্বাচিত গুরুত্বপূর্ণ প্রশ্নের উত্তর দিন এবং নিয়মিত জ্ঞানচর্চার পাশাপাশি জিতে নিন বোনাস ৫০ পয়েন্ট!",
          fontSize = 14.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          lineHeight = 20.sp,
          modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Bonus Highlights Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            DailyFeatureRow(icon = Icons.Default.CheckCircle, text = "১০টি গুরুত্বপূর্ণ সাধারণ জ্ঞান প্রশ্ন")
            Spacer(modifier = Modifier.height(12.dp))
            DailyFeatureRow(icon = Icons.Default.Stars, text = "প্রতিটি সঠিক উত্তরে ১০ পয়েন্ট")
            Spacer(modifier = Modifier.height(12.dp))
            DailyFeatureRow(icon = Icons.Default.Stars, text = "কুইজ সমাপ্তিতে বিশেষ বোনাস ৫০ পয়েন্ট")
          }
        }
      }

      Button(
        onClick = {
          viewModel.startDailyQuiz()
          onStartDailyQuiz()
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("start_daily_quiz_btn"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
      ) {
        Icon(Icons.Default.PlayArrow, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("কুইজ শুরু করুন", fontSize = 16.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
fun DailyFeatureRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(icon, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
    Spacer(modifier = Modifier.width(10.dp))
    Text(text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
  }
}
