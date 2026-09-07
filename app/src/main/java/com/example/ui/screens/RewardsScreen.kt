package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AppTopBar
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.PrimaryGreen
import com.example.ui.viewmodel.QuizViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RewardsScreen(
  viewModel: QuizViewModel,
  onNavigateBack: () -> Unit
) {
  val user by viewModel.currentUser.collectAsStateWithLifecycle()
  val rewardHistory by viewModel.userRewardHistory.collectAsStateWithLifecycle()
  val rewardMessage by viewModel.rewardMessage.collectAsStateWithLifecycle()

  if (rewardMessage != null) {
    AlertDialog(
      onDismissRequest = { viewModel.clearRewardMessage() },
      title = { Text("রিওয়ার্ড বিজ্ঞপ্তি") },
      text = { Text(rewardMessage!!) },
      confirmButton = {
        Button(
          onClick = { viewModel.clearRewardMessage() },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
        ) {
          Text("ঠিক আছে")
        }
      }
    )
  }

  Scaffold(
    topBar = {
      AppTopBar(
        title = "রিওয়ার্ড ও পয়েন্ট অর্জন",
        showBack = true,
        onBack = onNavigateBack,
        points = user?.totalPoints ?: 0
      )
    },
    modifier = Modifier.testTag("rewards_screen")
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Current Balance Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(
                text = "আপনার বর্তমান ব্যালেন্স",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
              Text(
                text = "${user?.totalPoints ?: 0} পয়েন্ট",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen
              )
            }
            Box(
              modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(GoldenAmber),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Stars,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(32.dp)
              )
            }
          }
        }
      }

      // 2. Watch Rewarded Ad Action Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(GoldenAmber.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.PlayCircle,
                  contentDescription = null,
                  tint = GoldenAmber,
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "বিজ্ঞাপন দেখে বোনাস পয়েন্ট নিন",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "প্রতিটি ভিডিও বিজ্ঞাপনে পাবেন +১০ পয়েন্ট",
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
              onClick = { viewModel.watchRewardedAdForPoints() },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("watch_rewarded_ad_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber)
            ) {
              Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.Black)
              Spacer(modifier = Modifier.width(8.dp))
              Text("ভিডিও দেখুন (+১০ পয়েন্ট)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
          }
        }
      }

      // 3. Reward History Section
      item {
        Text(
          text = "পয়েন্ট অর্জনের ইতিহাস",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(top = 10.dp)
        )
      }

      if (rewardHistory.isEmpty()) {
        item {
          Text(
            text = "এখনো কোন পয়েন্ট অর্জিত হয়নি। কুইজ খেলে পয়েন্ট অর্জন শুরু করুন!",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      items(rewardHistory) { item ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = item.titleBn,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
              )
              val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(item.timestamp))
              Text(
                text = dateStr,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = PrimaryGreen.copy(alpha = 0.15f)
            ) {
              Text(
                text = "+${item.pointsAdded}",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = PrimaryGreen,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }
    }
  }
}
