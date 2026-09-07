package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.LeaderboardEntry
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppTopBar
import com.example.ui.navigation.NavRoutes
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.PrimaryGreen
import com.example.ui.viewmodel.QuizViewModel

@Composable
fun LeaderboardScreen(
  viewModel: QuizViewModel,
  onNavigate: (String) -> Unit
) {
  val timeframe by viewModel.leaderboardTimeframe.collectAsStateWithLifecycle()
  val leaderboardList by viewModel.leaderboardList.collectAsStateWithLifecycle()
  val user by viewModel.currentUser.collectAsStateWithLifecycle()

  val tabs = listOf("দৈনিক" to "DAILY", "সাপ্তাহিক" to "WEEKLY", "মাসিক" to "MONTHLY", "সর্বকালীন" to "ALL_TIME")
  val selectedTabIndex = tabs.indexOfFirst { it.second == timeframe }.coerceAtLeast(0)

  Scaffold(
    topBar = {
      AppTopBar(
        title = "জাতীয় মেধা তালিকা",
        points = user?.totalPoints ?: 0,
        showBack = false
      )
    },
    bottomBar = {
      AppBottomNav(
        currentRoute = NavRoutes.LEADERBOARD,
        onNavigate = onNavigate
      )
    },
    modifier = Modifier.testTag("leaderboard_screen")
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      // Timeframe TabRow
      TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
      ) {
        tabs.forEachIndexed { index, pair ->
          Tab(
            selected = selectedTabIndex == index,
            onClick = { viewModel.setLeaderboardTimeframe(pair.second) },
            text = { Text(pair.first, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
            modifier = Modifier.testTag("leaderboard_tab_${pair.second}")
          )
        }
      }

      // Top 3 Podium
      if (leaderboardList.size >= 3) {
        TopPodiumView(
          first = leaderboardList.getOrNull(0),
          second = leaderboardList.getOrNull(1),
          third = leaderboardList.getOrNull(2)
        )
      }

      // Remaining list
      val remainingList = leaderboardList.drop(if (leaderboardList.size >= 3) 3 else 0)

      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(remainingList) { entry ->
          LeaderboardRowItem(entry = entry, isCurrentUser = entry.userId == user?.id)
        }
      }
    }
  }
}

@Composable
fun TopPodiumView(
  first: LeaderboardEntry?,
  second: LeaderboardEntry?,
  third: LeaderboardEntry?
) {
  Surface(
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.Bottom
    ) {
      // 2nd Place
      second?.let {
        PodiumCol(entry = it, rank = 2, rankColor = Color(0xFF94A3B8), avatarSize = 52, podiumHeight = 60)
      }
      // 1st Place (Center and Highest)
      first?.let {
        PodiumCol(entry = it, rank = 1, rankColor = GoldenAmber, avatarSize = 64, podiumHeight = 85)
      }
      // 3rd Place
      third?.let {
        PodiumCol(entry = it, rank = 3, rankColor = Color(0xFFB45309), avatarSize = 48, podiumHeight = 48)
      }
    }
  }
}

@Composable
fun PodiumCol(
  entry: LeaderboardEntry,
  rank: Int,
  rankColor: Color,
  avatarSize: Int,
  podiumHeight: Int
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.width(90.dp)
  ) {
    Box(contentAlignment = Alignment.TopCenter) {
      Box(
        modifier = Modifier
          .size(avatarSize.dp)
          .clip(CircleShape)
          .background(rankColor.copy(alpha = 0.2f))
          .border(2.dp, rankColor, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = entry.name.take(1),
          fontWeight = FontWeight.Bold,
          fontSize = (avatarSize / 3).sp,
          color = rankColor
        )
      }
      if (rank == 1) {
        Icon(
          imageVector = Icons.Default.EmojiEvents,
          contentDescription = null,
          tint = GoldenAmber,
          modifier = Modifier
            .size(20.dp)
            .align(Alignment.TopEnd)
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = entry.name,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )

    Text(
      text = "${entry.points} পয়েন্ট",
      fontSize = 10.sp,
      fontWeight = FontWeight.SemiBold,
      color = rankColor
    )

    Spacer(modifier = Modifier.height(4.dp))

    // Podium Block
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(podiumHeight.dp)
        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
        .background(rankColor.copy(alpha = 0.25f)),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "$rank",
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        color = rankColor
      )
    }
  }
}

@Composable
fun LeaderboardRowItem(
  entry: LeaderboardEntry,
  isCurrentUser: Boolean
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("leaderboard_row_${entry.rank}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isCurrentUser) PrimaryGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
    ),
    border = if (isCurrentUser) androidx.compose.foundation.BorderStroke(1.dp, PrimaryGreen) else null
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "#${entry.rank}",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.width(36.dp)
        )

        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = entry.name.take(1),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
          Text(
            text = entry.name + if (isCurrentUser) " (আপনি)" else "",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = if (isCurrentUser) PrimaryGreen else MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${entry.completedQuizzes}টি কুইজ সম্পন্ন",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Stars, contentDescription = null, tint = GoldenAmber, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "${entry.points}",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
      }
    }
  }
}
