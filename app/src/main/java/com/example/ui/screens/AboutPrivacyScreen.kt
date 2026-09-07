package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppTopBar
import com.example.ui.theme.PrimaryGreen

@Composable
fun AboutPrivacyScreen(
  onNavigateBack: () -> Unit
) {
  Scaffold(
    topBar = {
      AppTopBar(
        title = "পরিচিতি ও গোপনীয়তা নীতি",
        showBack = true,
        onBack = onNavigateBack
      )
    },
    modifier = Modifier.testTag("about_privacy_screen")
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "বাংলা কুইজ মাস্টার সম্পর্কে",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = PrimaryGreen
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "বাংলা কুইজ মাস্টার হলো একটি আধুনিক, গতিশীল এবং শিক্ষণীয় বাংলা কুইজ ও মডেল টেস্ট প্ল্যাটফর্ম। বিসিএস, ব্যাংক, প্রাথমিক শিক্ষক নিবন্ধন, বিশ্ববিদ্যালয় ভর্তি এবং বিভিন্ন সরকারি-বেসরকারি চাকরির পরীক্ষার প্রস্তুতির জন্য এটি বিশেষভাবে তৈরি করা হয়েছে।",
              fontSize = 13.sp,
              lineHeight = 20.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }

      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "গোপনীয়তা নীতি (Privacy Policy)",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = PrimaryGreen
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "আমরা আপনার ব্যক্তিগত তথ্যের সুরক্ষাকে সর্বোচ্চ অগ্রাধিকার দিই। আপনার অ্যাকাউন্ট তথ্য (যেমন নাম ও ইমেইল) শুধুমাত্র কুইজের স্কোর সংরক্ষণ ও লিডারবোর্ডে মেধা তালিকা প্রদর্শনের উদ্দেশ্যে ব্যবহৃত হয়। আমরা কোনো তৃতীয় পক্ষের সাথে আপনার সংবেদনশীল তথ্য শেয়ার করি না।",
              fontSize = 13.sp,
              lineHeight = 20.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "বিজ্ঞাপন নেটওয়ার্ক: এই অ্যাপ্লিকেশনে বিজ্ঞাপন পরিবেশনের জন্য Start.io এবং Advertica নেটওয়ার্ক ব্যবহৃত হতে পারে, যা তাদের নীতিমালা অনুযায়ী নিরাপদ বিজ্ঞাপন প্রদর্শন করে।",
              fontSize = 12.sp,
              lineHeight = 18.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "যোগাযোগ ও সহায়তা",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = PrimaryGreen
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "যেকোনো মতামত বা সহায়তার জন্য আমাদের ইমেইল করুন:\nsupport@banglaquizmaster.com\nঢাকা, বাংলাদেশ।",
              fontSize = 13.sp,
              lineHeight = 20.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }
    }
  }
}
