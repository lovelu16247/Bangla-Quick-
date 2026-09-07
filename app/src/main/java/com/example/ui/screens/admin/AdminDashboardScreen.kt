package com.example.ui.screens.admin

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.navigation.NavRoutes
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
  viewModel: AdminViewModel,
  onNavigateBack: () -> Unit,
  onNavigateToQuestions: () -> Unit,
  onNavigateToCategoriesExams: () -> Unit,
  onNavigateToUsersAds: () -> Unit
) {
  val stats by viewModel.dashboardStats.collectAsStateWithLifecycle()
  val statusMsg by viewModel.statusMessage.collectAsStateWithLifecycle()
  val adminUser by viewModel.currentAdmin.collectAsStateWithLifecycle()

  var showBroadcastDialog by remember { mutableStateOf(false) }
  var notifTitle by remember { mutableStateOf("") }
  var notifMessage by remember { mutableStateOf("") }

  if (statusMsg != null) {
    AlertDialog(
      onDismissRequest = { viewModel.clearStatusMessage() },
      title = { Text("অ্যাডমিন নোটিশ") },
      text = { Text(statusMsg!!) },
      confirmButton = {
        Button(onClick = { viewModel.clearStatusMessage() }) {
          Text("ঠিক আছে")
        }
      }
    )
  }

  if (showBroadcastDialog) {
    AlertDialog(
      onDismissRequest = { showBroadcastDialog = false },
      title = { Text("পুশ নোটিফিকেশন পাঠান") },
      text = {
        Column {
          OutlinedTextField(
            value = notifTitle,
            onValueChange = { notifTitle = it },
            label = { Text("বিজ্ঞপ্তি শিরোনাম") },
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = notifMessage,
            onValueChange = { notifMessage = it },
            label = { Text("বিজ্ঞপ্তি বিবরণ / বার্তা") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (notifTitle.isNotBlank() && notifMessage.isNotBlank()) {
              viewModel.broadcastNotification(notifTitle, notifMessage, "ANNOUNCEMENT")
              showBroadcastDialog = false
              notifTitle = ""
              notifMessage = ""
            }
          }
        ) {
          Text("পাঠিয়ে দিন")
        }
      },
      dismissButton = {
        TextButton(onClick = { showBroadcastDialog = false }) {
          Text("বাতিল")
        }
      }
    )
  }

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Text("অ্যাডমিন ড্যাশবোর্ড", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("admin_back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(
            onClick = {
              viewModel.logout()
              onNavigateBack()
            },
            modifier = Modifier.testTag("admin_logout_button")
          ) {
            Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = MaterialTheme.colorScheme.error)
          }
        }
      )
    },
    modifier = Modifier.testTag("admin_dashboard_screen")
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Welcome Admin Banner
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(PrimaryGreen),
              contentAlignment = Alignment.Center
            ) {
              Text("A", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "স্বাগতম, ${adminUser?.fullName ?: "সুপার অ্যাডমিন"}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
              Text(
                text = "রোল: ${adminUser?.role ?: "SUPER_ADMIN"} • সিস্টেম স্ট্যাটাস: সচল",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
              )
            }
          }
        }
      }

      // Metric Cards Grid
      item {
        Text("সারসংক্ষেপ ও পরিসংখ্যান (Overview)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
      }

      item {
        val totalUsers = stats["totalUsers"] ?: 6
        val totalQuestions = stats["totalQuestions"] ?: 24
        val totalCategories = stats["totalCategories"] ?: 18
        val totalExams = stats["totalExams"] ?: 3
        val totalAttempts = (stats["totalQuizAttempts"] as? Number)?.toInt() ?: 42
        val totalImpressions = 142
        val estimatedRevenue = "$18.45"

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            AdminMetricCard(title = "মোট ব্যবহারকারী", value = "$totalUsers", icon = Icons.Default.Group, color = PrimaryGreen, modifier = Modifier.weight(1f))
            AdminMetricCard(title = "মোট প্রশ্ন সংখ্যা", value = "$totalQuestions", icon = Icons.Default.QuestionAnswer, color = Color(0xFF0284C7), modifier = Modifier.weight(1f))
          }
          Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            AdminMetricCard(title = "মোট ক্যাটাগরি", value = "$totalCategories", icon = Icons.Default.Category, color = Color(0xFFD97706), modifier = Modifier.weight(1f))
            AdminMetricCard(title = "মডেল টেস্ট এক্সাম", value = "$totalExams", icon = Icons.Default.School, color = Color(0xFFDC2626), modifier = Modifier.weight(1f))
          }
          Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            AdminMetricCard(title = "বিজ্ঞাপন ইমপ্রেশন", value = "$totalImpressions", icon = Icons.Default.Visibility, color = Color(0xFF7C3AED), modifier = Modifier.weight(1f))
            AdminMetricCard(title = "সম্ভাব্য আয় (USD)", value = estimatedRevenue, icon = Icons.Default.AttachMoney, color = SuccessGreen, modifier = Modifier.weight(1f))
          }
        }
      }

      // Quick Admin Navigation Buttons
      item {
        Spacer(modifier = Modifier.height(4.dp))
        Text("অ্যাডমিন মডিউলসমূহ", fontSize = 15.sp, fontWeight = FontWeight.Bold)
      }

      item {
        AdminNavActionCard(
          title = "প্রশ্নভাণ্ডার ব্যবস্থাপনা (Question Management)",
          subtitle = "প্রশ্ন তৈরি, সম্পাদনা, মুছে ফেলা এবং বাল্ক ইম্পোর্ট",
          icon = Icons.Default.Quiz,
          onClick = onNavigateToQuestions,
          testTag = "admin_nav_questions"
        )
      }

      item {
        AdminNavActionCard(
          title = "ক্যাটাগরি ও মডেল টেস্ট (Categories & Exams)",
          subtitle = "ক্যাটাগরি তৈরি, লাইভ এক্সাম ও নেগেটিভ মার্কিং কন্ট্রোল",
          icon = Icons.Default.School,
          onClick = onNavigateToCategoriesExams,
          testTag = "admin_nav_cat_exams"
        )
      }

      item {
        AdminNavActionCard(
          title = "ব্যবহারকারী ও বিজ্ঞাপন সেটিংস (Users & Ads)",
          subtitle = "ইউজার ব্লক/পয়েন্ট রিসেট, Start.io ও Advertica বিজ্ঞাপন কনফিগ",
          icon = Icons.Default.AdsClick,
          onClick = onNavigateToUsersAds,
          testTag = "admin_nav_users_ads"
        )
      }

      item {
        AdminNavActionCard(
          title = "পুশ নোটিফিকেশন ব্রডকাস্ট",
          subtitle = "সকল ব্যবহারকারীকে বিশেষ ঘোষণা বা বার্তা প্রেরণ করুন",
          icon = Icons.Default.Notifications,
          onClick = { showBroadcastDialog = true },
          testTag = "admin_nav_broadcast"
        )
      }
    }
  }
}

@Composable
fun AdminMetricCard(
  title: String,
  value: String,
  icon: ImageVector,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
        Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
  }
}

@Composable
fun AdminNavActionCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  onClick: () -> Unit,
  testTag: String
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag(testTag),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
  ) {
    Row(
      modifier = Modifier.padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(PrimaryGreen.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
          Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
      Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp), tint = PrimaryGreen)
    }
  }
}
