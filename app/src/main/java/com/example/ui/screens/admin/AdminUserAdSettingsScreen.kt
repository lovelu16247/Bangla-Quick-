package com.example.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AdSettings
import com.example.data.model.AppSettings
import com.example.data.model.User
import com.example.ui.screens.SettingToggleRow
import com.example.ui.theme.PrimaryGreen
import com.example.ui.viewmodel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserAdSettingsScreen(
  viewModel: AdminViewModel,
  onNavigateBack: () -> Unit
) {
  val users by viewModel.allUsers.collectAsStateWithLifecycle()
  val adSettings by viewModel.adSettings.collectAsStateWithLifecycle()
  val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()
  val auditLogs by viewModel.adminLogs.collectAsStateWithLifecycle()

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Users, 1: Ads, 2: App Config & Logs

  var targetUserForPoints by remember { mutableStateOf<User?>(null) }
  var newPointsInput by remember { mutableStateOf("") }

  if (targetUserForPoints != null) {
    AlertDialog(
      onDismissRequest = { targetUserForPoints = null },
      title = { Text("ব্যবহারকারীর পয়েন্ট পরিবর্তন") },
      text = {
        Column {
          Text("ব্যবহারকারী: ${targetUserForPoints?.fullName}")
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = newPointsInput,
            onValueChange = { newPointsInput = it },
            label = { Text("নতুন পয়েন্ট") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val pts = newPointsInput.toIntOrNull()
            if (pts != null && targetUserForPoints != null) {
              viewModel.adjustUserPoints(targetUserForPoints!!.id, pts)
              targetUserForPoints = null
              newPointsInput = ""
            }
          }
        ) { Text("সংরক্ষণ করুন") }
      },
      dismissButton = { TextButton(onClick = { targetUserForPoints = null }) { Text("বাতিল") } }
    )
  }

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = { Text("ইউজার ও বিজ্ঞাপন কনফিগারেশন", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        }
      )
    },
    modifier = Modifier.testTag("admin_users_ads_screen")
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      TabRow(selectedTabIndex = selectedTab) {
        Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("ব্যবহারকারী (${users.size})") })
        Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("বিজ্ঞাপন সেটিংস") })
        Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("অ্যাপ কনফিগ ও লগ") })
      }

      when (selectedTab) {
        0 -> {
          // Users List
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(users) { u ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column {
                      Text(u.fullName, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                      Text("@${u.username} • ${u.email}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = if (u.isBlocked) MaterialTheme.colorScheme.error.copy(alpha = 0.15f) else PrimaryGreen.copy(alpha = 0.15f)
                    ) {
                      Text(
                        text = if (u.isBlocked) "ব্লকড" else "সক্রিয়",
                        color = if (u.isBlocked) MaterialTheme.colorScheme.error else PrimaryGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(6.dp))

                  Text(
                    text = "পয়েন্ট: ${u.totalPoints} | লেভেল: ${u.currentLevel} | কুইজ: ${u.completedQuizzes} | এক্সাম: ${u.completedExams}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )

                  Spacer(modifier = Modifier.height(8.dp))

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                  ) {
                    TextButton(
                      onClick = {
                        targetUserForPoints = u
                        newPointsInput = "${u.totalPoints}"
                      }
                    ) {
                      Text("পয়েন্ট সমন্বয়")
                    }
                    TextButton(
                      onClick = { viewModel.resetUserProgress(u.id) }
                    ) {
                      Text("অগ্রগতি রিসেট")
                    }
                    TextButton(
                      onClick = { viewModel.toggleBlockUser(u.id, !u.isBlocked) }
                    ) {
                      Text(if (u.isBlocked) "আনব্লক" else "ব্লক করুন", color = if (u.isBlocked) PrimaryGreen else MaterialTheme.colorScheme.error)
                    }
                  }
                }
              }
            }
          }
        }
        1 -> {
          // Ad Settings Form
          Column(
            modifier = Modifier
              .fillMaxSize()
              .verticalScroll(rememberScrollState())
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Text("বিজ্ঞাপন নেটওয়ার্ক কন্ট্রোল", fontSize = 15.sp, fontWeight = FontWeight.Bold)

            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                SettingToggleRow(
                  title = "Start.io নেটওয়ার্ক সক্রিয়",
                  subtitle = "Start.io ব্যানার ও ভিডিও বিজ্ঞাপন সক্ষম রাখুন",
                  checked = adSettings.startIoEnabled,
                  onCheckedChange = { viewModel.saveAdSettings(adSettings.copy(startIoEnabled = it)) }
                )
                Spacer(modifier = Modifier.height(10.dp))
                SettingToggleRow(
                  title = "Advertica নেটওয়ার্ক সক্রিয়",
                  subtitle = "Advertica ব্যানার ও ভিডিও বিজ্ঞাপন সক্ষম রাখুন",
                  checked = adSettings.adverticaEnabled,
                  onCheckedChange = { viewModel.saveAdSettings(adSettings.copy(adverticaEnabled = it)) }
                )
                Spacer(modifier = Modifier.height(10.dp))
                SettingToggleRow(
                  title = "ব্যানার বিজ্ঞাপন (Banner Ads)",
                  subtitle = "হোম ও ফলাফল স্ক্রিনের নিচের ব্যানার",
                  checked = adSettings.bannerEnabled,
                  onCheckedChange = { viewModel.saveAdSettings(adSettings.copy(bannerEnabled = it)) }
                )
                Spacer(modifier = Modifier.height(10.dp))
                SettingToggleRow(
                  title = "ইন্টারস্টিশিয়াল বিজ্ঞাপন (Interstitial Ads)",
                  subtitle = "কুইজ বা পরীক্ষার সমাপ্তিতে ফুল স্ক্রিন বিজ্ঞাপন",
                  checked = adSettings.interstitialEnabled,
                  onCheckedChange = { viewModel.saveAdSettings(adSettings.copy(interstitialEnabled = it)) }
                )
                Spacer(modifier = Modifier.height(10.dp))
                SettingToggleRow(
                  title = "পুরস্কৃত ভিডিও বিজ্ঞাপন (Rewarded Video Ads)",
                  subtitle = "ভিডিও বিজ্ঞাপন দেখে ব্যবহারকারীর পয়েন্ট অর্জন",
                  checked = adSettings.rewardedEnabled,
                  onCheckedChange = { viewModel.saveAdSettings(adSettings.copy(rewardedEnabled = it)) }
                )
              }
            }
          }
        }
        2 -> {
          // App Config & Audit Logs
          Column(
            modifier = Modifier
              .fillMaxSize()
              .verticalScroll(rememberScrollState())
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Text("অ্যাপের সাধারণ কনফিগারেশন", fontSize = 15.sp, fontWeight = FontWeight.Bold)

            var appNameState by remember { mutableStateOf(appSettings.appName) }

            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                  value = appNameState,
                  onValueChange = { appNameState = it },
                  label = { Text("অ্যাপের নাম (App Name)") },
                  modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                SettingToggleRow(
                  title = "মেইনটেন্যান্স মোড (Maintenance Mode)",
                  subtitle = "অ্যাপে সাময়িক রক্ষণাবেক্ষণ বার্তা প্রদর্শন করুন",
                  checked = appSettings.isMaintenanceMode,
                  onCheckedChange = { viewModel.saveAppSettings(appSettings.copy(isMaintenanceMode = it)) }
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                  onClick = { viewModel.saveAppSettings(appSettings.copy(appName = appNameState)) },
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Text("অ্যাপ সেটিংস সংরক্ষণ করুন")
                }
              }
            }

            Text("অ্যাডমিন অডিট লগ (Audit Logs)", fontSize = 15.sp, fontWeight = FontWeight.Bold)

            auditLogs.forEach { log ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
              ) {
                Column(modifier = Modifier.padding(10.dp)) {
                  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(log.action, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                    val date = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(log.timestamp))
                    Text(date, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  }
                  Text(log.details, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                }
              }
            }
          }
        }
      }
    }
  }
}
