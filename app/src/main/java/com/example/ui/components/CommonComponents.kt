package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.NavRoutes
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
  title: String,
  showBack: Boolean = false,
  onBack: () -> Unit = {},
  points: Int? = null,
  onNotificationsClick: (() -> Unit)? = null,
  unreadNotificationsCount: Int = 0,
  onAdminClick: (() -> Unit)? = null
) {
  CenterAlignedTopAppBar(
    title = {
      Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    },
    navigationIcon = {
      if (showBack) {
        IconButton(onClick = onBack, modifier = Modifier.testTag("top_bar_back_button")) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back"
          )
        }
      }
    },
    actions = {
      if (points != null) {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = GoldenAmber.copy(alpha = 0.15f),
          border = androidx.compose.foundation.BorderStroke(1.dp, GoldenAmber.copy(alpha = 0.4f)),
          modifier = Modifier
            .padding(end = 6.dp)
            .testTag("points_badge")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Stars,
              contentDescription = "Points",
              tint = GoldenAmber,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "$points",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = GoldenAmber
            )
          }
        }
      }

      if (onNotificationsClick != null) {
        IconButton(
          onClick = onNotificationsClick,
          modifier = Modifier.testTag("top_bar_notification_button")
        ) {
          BadgedBox(
            badge = {
              if (unreadNotificationsCount > 0) {
                Badge { Text("$unreadNotificationsCount") }
              }
            }
          ) {
            Icon(
              imageVector = Icons.Default.Notifications,
              contentDescription = "Notifications"
            )
          }
        }
      }

      if (onAdminClick != null) {
        IconButton(
          onClick = onAdminClick,
          modifier = Modifier.testTag("top_bar_admin_button")
        ) {
          Icon(
            imageVector = Icons.Default.AdminPanelSettings,
            contentDescription = "Admin Panel",
            tint = MaterialTheme.colorScheme.primary
          )
        }
      }
    },
    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  )
}

@Composable
fun AppBottomNav(
  currentRoute: String,
  onNavigate: (String) -> Unit
) {
  NavigationBar(
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 8.dp,
    modifier = Modifier.testTag("app_bottom_nav")
  ) {
    NavigationBarItem(
      icon = {
        Icon(
          imageVector = if (currentRoute == NavRoutes.HOME) Icons.Filled.Home else Icons.Outlined.Home,
          contentDescription = "Home"
        )
      },
      label = { Text("হোম", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
      selected = currentRoute == NavRoutes.HOME,
      onClick = { onNavigate(NavRoutes.HOME) },
      modifier = Modifier.testTag("nav_home")
    )
    NavigationBarItem(
      icon = {
        Icon(
          imageVector = if (currentRoute == NavRoutes.CATEGORIES) Icons.Filled.Category else Icons.Outlined.Category,
          contentDescription = "Categories"
        )
      },
      label = { Text("ক্যাটাগরি", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
      selected = currentRoute == NavRoutes.CATEGORIES,
      onClick = { onNavigate(NavRoutes.CATEGORIES) },
      modifier = Modifier.testTag("nav_categories")
    )
    NavigationBarItem(
      icon = {
        Icon(
          imageVector = if (currentRoute == NavRoutes.EXAM_LIST) Icons.Filled.School else Icons.Outlined.School,
          contentDescription = "Exams"
        )
      },
      label = { Text("পরীক্ষা", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
      selected = currentRoute == NavRoutes.EXAM_LIST,
      onClick = { onNavigate(NavRoutes.EXAM_LIST) },
      modifier = Modifier.testTag("nav_exams")
    )
    NavigationBarItem(
      icon = {
        Icon(
          imageVector = if (currentRoute == NavRoutes.LEADERBOARD) Icons.Filled.Leaderboard else Icons.Outlined.Leaderboard,
          contentDescription = "Leaderboard"
        )
      },
      label = { Text("লিডারবোর্ড", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
      selected = currentRoute == NavRoutes.LEADERBOARD,
      onClick = { onNavigate(NavRoutes.LEADERBOARD) },
      modifier = Modifier.testTag("nav_leaderboard")
    )
    NavigationBarItem(
      icon = {
        Icon(
          imageVector = if (currentRoute == NavRoutes.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
          contentDescription = "Profile"
        )
      },
      label = { Text("প্রোফাইল", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
      selected = currentRoute == NavRoutes.PROFILE,
      onClick = { onNavigate(NavRoutes.PROFILE) },
      modifier = Modifier.testTag("nav_profile")
    )
  }
}

/**
 * Quiz option button with feedback styling
 */
@Composable
fun QuizOptionItem(
  optionIndex: Int,
  optionText: String,
  isSelected: Boolean,
  isCorrect: Boolean?,
  isSubmitted: Boolean,
  onClick: () -> Unit
) {
  val prefix = when (optionIndex) {
    0 -> "ক"
    1 -> "খ"
    2 -> "গ"
    else -> "ঘ"
  }

  val containerColor by animateColorAsState(
    targetValue = when {
      isSubmitted && isCorrect == true -> SuccessGreen.copy(alpha = 0.15f)
      isSubmitted && isSelected && isCorrect == false -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
      isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
      else -> MaterialTheme.colorScheme.surface
    },
    label = "optionColor"
  )

  val borderColor by animateColorAsState(
    targetValue = when {
      isSubmitted && isCorrect == true -> SuccessGreen
      isSubmitted && isSelected && isCorrect == false -> MaterialTheme.colorScheme.error
      isSelected -> MaterialTheme.colorScheme.primary
      else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
    },
    label = "optionBorder"
  )

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 5.dp)
      .clip(RoundedCornerShape(14.dp))
      .clickable(enabled = !isSubmitted, onClick = onClick)
      .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
      .testTag("quiz_option_$optionIndex"),
    color = containerColor,
    shape = RoundedCornerShape(14.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(
            when {
              isSubmitted && isCorrect == true -> SuccessGreen
              isSubmitted && isSelected && isCorrect == false -> MaterialTheme.colorScheme.error
              isSelected -> MaterialTheme.colorScheme.primary
              else -> MaterialTheme.colorScheme.surfaceVariant
            }
          ),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = prefix,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = if (isSelected || (isSubmitted && isCorrect == true)) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Text(
        text = optionText,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.weight(1f)
      )

      if (isSubmitted) {
        if (isCorrect == true) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Correct",
            tint = SuccessGreen,
            modifier = Modifier.size(22.dp)
          )
        } else if (isSelected && isCorrect == false) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Wrong",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(22.dp)
          )
        }
      }
    }
  }
}
