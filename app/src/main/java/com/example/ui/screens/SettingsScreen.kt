package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppTopBar
import com.example.ui.theme.PrimaryGreen

@Composable
fun SettingsScreen(
  onNavigateBack: () -> Unit
) {
  var soundEnabled by remember { mutableStateOf(true) }
  var vibrationEnabled by remember { mutableStateOf(true) }
  var pushNotificationsEnabled by remember { mutableStateOf(true) }
  var dailyReminderEnabled by remember { mutableStateOf(true) }

  Scaffold(
    topBar = {
      AppTopBar(
        title = "অ্যাপ সেটিংস",
        showBack = true,
        onBack = onNavigateBack
      )
    },
    modifier = Modifier.testTag("settings_screen")
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Text(
        text = "সাধারণ পছন্দসমূহ",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
      )

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          SettingToggleRow(
            title = "শব্দ ও সাউন্ড ইফেক্ট",
            subtitle = "কুইজের সঠিক বা ভুল উত্তরে সাউন্ড বাজবে",
            checked = soundEnabled,
            onCheckedChange = { soundEnabled = it }
          )

          Spacer(modifier = Modifier.height(14.dp))

          SettingToggleRow(
            title = "ভাইব্রেশন প্রতিক্রিয়া",
            subtitle = "বাটন ক্লিক ও ভুল উত্তরে মৃদু কম্পন",
            checked = vibrationEnabled,
            onCheckedChange = { vibrationEnabled = it }
          )

          Spacer(modifier = Modifier.height(14.dp))

          SettingToggleRow(
            title = "পুশ নোটিফিকেশন",
            subtitle = "নতুন মডেল টেস্ট ও কুইজের বিজ্ঞপ্তি পান",
            checked = pushNotificationsEnabled,
            onCheckedChange = { pushNotificationsEnabled = it }
          )

          Spacer(modifier = Modifier.height(14.dp))

          SettingToggleRow(
            title = "দৈনিক কুইজ রিমাইন্ডার",
            subtitle = "প্রতিদিন কুইজ খেলার জন্য সঠিক সময়ে নোটিফিকেশন",
            checked = dailyReminderEnabled,
            onCheckedChange = { dailyReminderEnabled = it }
          )
        }
      }

      Text(
        text = "অ্যাপ তথ্য",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 10.dp)
      )

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("সংস্করণ (Version)", fontSize = 14.sp)
            Text("1.0.0 (Build 2026)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
          }
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("ভাষা", fontSize = 14.sp)
            Text("বাংলা (Bengali)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
          }
        }
      }
    }
  }
}

@Composable
fun SettingToggleRow(
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
      Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
      Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(checkedTrackColor = PrimaryGreen)
    )
  }
}
