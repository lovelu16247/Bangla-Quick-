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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import com.example.data.model.Category
import com.example.data.model.Exam
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCategoryExamScreen(
  viewModel: AdminViewModel,
  onNavigateBack: () -> Unit
) {
  val categories by viewModel.allCategories.collectAsStateWithLifecycle()
  val exams by viewModel.allExams.collectAsStateWithLifecycle()

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Categories, 1: Exams
  var showAddCategoryDialog by remember { mutableStateOf(false) }
  var showAddExamDialog by remember { mutableStateOf(false) }

  // Category Form
  var catNameBn by remember { mutableStateOf("") }
  var catNameEn by remember { mutableStateOf("") }

  // Exam Form
  var examTitleBn by remember { mutableStateOf("") }
  var examDescBn by remember { mutableStateOf("") }
  var examDuration by remember { mutableIntStateOf(15) }
  var examQuestionsCount by remember { mutableIntStateOf(15) }
  var examPassingScore by remember { mutableIntStateOf(10) }

  if (showAddCategoryDialog) {
    AlertDialog(
      onDismissRequest = { showAddCategoryDialog = false },
      title = { Text("নতুন ক্যাটাগরি তৈরি") },
      text = {
        Column {
          OutlinedTextField(value = catNameBn, onValueChange = { catNameBn = it }, label = { Text("বাংলা নাম") }, modifier = Modifier.fillMaxWidth())
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(value = catNameEn, onValueChange = { catNameEn = it }, label = { Text("ইংরেজি নাম") }, modifier = Modifier.fillMaxWidth())
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (catNameBn.isNotBlank()) {
              viewModel.saveCategory(Category(nameBn = catNameBn.trim(), nameEn = catNameEn.trim(), iconName = "quiz", colorHex = "#0D7A4A", questionCount = 0))
              showAddCategoryDialog = false
              catNameBn = ""
              catNameEn = ""
            }
          }
        ) { Text("তৈরি করুন") }
      },
      dismissButton = { TextButton(onClick = { showAddCategoryDialog = false }) { Text("বাতিল") } }
    )
  }

  if (showAddExamDialog) {
    AlertDialog(
      onDismissRequest = { showAddExamDialog = false },
      title = { Text("নতুন মডেল টেস্ট এক্সাম তৈরি") },
      text = {
        Column {
          OutlinedTextField(value = examTitleBn, onValueChange = { examTitleBn = it }, label = { Text("পরীক্ষার শিরোনাম") }, modifier = Modifier.fillMaxWidth())
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(value = examDescBn, onValueChange = { examDescBn = it }, label = { Text("বিবরণ ও নির্দেশাবলী") }, modifier = Modifier.fillMaxWidth())
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (examTitleBn.isNotBlank()) {
              viewModel.saveExam(
                Exam(
                  titleBn = examTitleBn.trim(),
                  descriptionBn = examDescBn.trim(),
                  categoryId = 15L,
                  totalQuestions = examQuestionsCount,
                  durationMinutes = examDuration,
                  passingScore = examPassingScore,
                  negativeMarkingPerWrong = 0.25f,
                  isPublished = true
                )
              )
              showAddExamDialog = false
              examTitleBn = ""
              examDescBn = ""
            }
          }
        ) { Text("সংরক্ষণ করুন") }
      },
      dismissButton = { TextButton(onClick = { showAddExamDialog = false }) { Text("বাতিল") } }
    )
  }

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = { Text("ক্যাটাগরি ও মডেল টেস্ট", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        }
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = {
          if (selectedTab == 0) showAddCategoryDialog = true else showAddExamDialog = true
        },
        containerColor = PrimaryGreen,
        contentColor = androidx.compose.ui.graphics.Color.White
      ) {
        Icon(Icons.Default.Add, contentDescription = "Add")
      }
    },
    modifier = Modifier.testTag("admin_category_exam_screen")
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      TabRow(selectedTabIndex = selectedTab) {
        Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("ক্যাটাগরি (${categories.size})") })
        Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("মডেল টেস্ট (${exams.size})") })
      }

      if (selectedTab == 0) {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(categories) { cat ->
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
                Column {
                  Text(cat.nameBn, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                  Text(cat.nameEn, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Switch(
                    checked = cat.isActive,
                    onCheckedChange = { viewModel.saveCategory(cat.copy(isActive = it)) }
                  )
                  IconButton(onClick = { viewModel.deleteCategory(cat.id) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                  }
                }
              }
            }
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(exams) { ex ->
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
                  Text(ex.titleBn, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                  Switch(
                    checked = ex.isPublished,
                    onCheckedChange = { viewModel.saveExam(ex.copy(isPublished = it)) }
                  )
                }
                Text(
                  "প্রশ্ন: ${ex.totalQuestions} টি | সময়: ${ex.durationMinutes} মিনিট | পাস: ${ex.passingScore} | নেগেটিভ: -${ex.negativeMarkingPerWrong}",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.padding(vertical = 4.dp)
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                  IconButton(onClick = { viewModel.deleteExam(ex.id) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
