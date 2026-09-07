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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Exam
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppTopBar
import com.example.ui.navigation.NavRoutes
import com.example.ui.theme.PrimaryGreen
import com.example.ui.viewmodel.QuizViewModel

@Composable
fun ExamListScreen(
  viewModel: QuizViewModel,
  onNavigate: (String) -> Unit
) {
  val exams by viewModel.publishedExams.collectAsStateWithLifecycle()
  val user by viewModel.currentUser.collectAsStateWithLifecycle()

  Scaffold(
    topBar = {
      AppTopBar(
        title = "বিসিএস ও চাকরির মডেল টেস্ট",
        points = user?.totalPoints ?: 0,
        showBack = false
      )
    },
    bottomBar = {
      AppBottomNav(
        currentRoute = NavRoutes.EXAM_LIST,
        onNavigate = onNavigate
      )
    },
    modifier = Modifier.testTag("exam_list_screen")
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      item {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.primaryContainer,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "পরীক্ষায় ভুল উত্তরের জন্য ০.২৫ নম্বর কাটা যাবে। সতর্কতার সাথে উত্তর নির্বাচন করুন।",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              lineHeight = 16.sp
            )
          }
        }
      }

      items(exams) { exam ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("exam_item_${exam.id}"),
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
                  text = "মডেল টেস্ট",
                  color = Color(0xFFDC2626),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }

              Text(
                text = "${exam.totalQuestions} টি প্রশ্ন • ${exam.durationMinutes} মিনিট",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = exam.titleBn,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = exam.descriptionBn,
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "পাস মার্ক: ${exam.passingScore}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = PrimaryGreen
                )
                Text(
                  text = "নেগেটিভ মার্ক: -${exam.negativeMarkingPerWrong}",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.error
                )
              }

              Button(
                onClick = {
                  viewModel.startExam(exam)
                  onNavigate(NavRoutes.EXAM_PLAY)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                modifier = Modifier.testTag("start_exam_btn_${exam.id}")
              ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("শুরু করুন", fontSize = 13.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}
