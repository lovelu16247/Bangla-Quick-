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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.example.data.model.Question
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminQuestionScreen(
  viewModel: AdminViewModel,
  onNavigateBack: () -> Unit
) {
  val questions by viewModel.allQuestions.collectAsStateWithLifecycle()
  val categories by viewModel.allCategories.collectAsStateWithLifecycle()

  var searchQuery by remember { mutableStateOf("") }
  var showAddEditDialog by remember { mutableStateOf(false) }
  var showBulkImportDialog by remember { mutableStateOf(false) }
  var editingQuestion by remember { mutableStateOf<Question?>(null) }

  // Form states
  var questionBn by remember { mutableStateOf("") }
  var option1 by remember { mutableStateOf("") }
  var option2 by remember { mutableStateOf("") }
  var option3 by remember { mutableStateOf("") }
  var option4 by remember { mutableStateOf("") }
  var correctOptionIndex by remember { mutableIntStateOf(0) }
  var explanationBn by remember { mutableStateOf("") }
  var categoryId by remember { mutableStateOf(1L) }
  var points by remember { mutableIntStateOf(10) }

  // Bulk import state
  var bulkRawText by remember { mutableStateOf("") }

  fun openCreateDialog() {
    editingQuestion = null
    questionBn = ""
    option1 = ""
    option2 = ""
    option3 = ""
    option4 = ""
    correctOptionIndex = 0
    explanationBn = ""
    categoryId = categories.firstOrNull()?.id ?: 1L
    points = 10
    showAddEditDialog = true
  }

  fun openEditDialog(q: Question) {
    editingQuestion = q
    questionBn = q.questionBn
    option1 = q.options.getOrElse(0) { "" }
    option2 = q.options.getOrElse(1) { "" }
    option3 = q.options.getOrElse(2) { "" }
    option4 = q.options.getOrElse(3) { "" }
    correctOptionIndex = q.correctOptionIndex
    explanationBn = q.explanationBn
    categoryId = q.categoryId
    points = q.points
    showAddEditDialog = true
  }

  val filteredQuestions = remember(questions, searchQuery) {
    if (searchQuery.isBlank()) questions
    else questions.filter {
      it.questionBn.contains(searchQuery, ignoreCase = true) ||
        it.explanationBn.contains(searchQuery, ignoreCase = true)
    }
  }

  if (showBulkImportDialog) {
    AlertDialog(
      onDismissRequest = { showBulkImportDialog = false },
      title = { Text("বাল্ক প্রশ্ন ইম্পোর্ট (Bulk Import)") },
      text = {
        Column {
          Text(
            "ফরমেট: প্রশ্ন | অপশন১, অপশন২, অপশন৩, অপশন৪ | সঠিক ইনডেক্স (০-৩) | ব্যাখ্যা | ক্যাটাগরি আইডি\n(প্রতি লাইনে ১টি প্রশ্ন)",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = bulkRawText,
            onValueChange = { bulkRawText = it },
            placeholder = { Text("উদাহরণ:\nপদ্মা সেতুর দৈর্ঘ্য কত? | ৬.১৫ কিমি, ৫.৮০ কিমি, ৭ কিমি, ৮ কিমি | 0 | মূল দৈর্ঘ্য ৬.১৫ কিমি | 1") },
            modifier = Modifier
              .fillMaxWidth()
              .height(180.dp),
            maxLines = 8
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (bulkRawText.isNotBlank()) {
              viewModel.bulkImportQuestions(bulkRawText)
              showBulkImportDialog = false
              bulkRawText = ""
            }
          }
        ) {
          Text("ইম্পোর্ট সম্পন্ন করুন")
        }
      },
      dismissButton = {
        TextButton(onClick = { showBulkImportDialog = false }) {
          Text("বাতিল")
        }
      }
    )
  }

  if (showAddEditDialog) {
    AlertDialog(
      onDismissRequest = { showAddEditDialog = false },
      title = { Text(if (editingQuestion == null) "নতুন প্রশ্ন তৈরি" else "প্রশ্ন সম্পাদনা") },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
        ) {
          OutlinedTextField(
            value = questionBn,
            onValueChange = { questionBn = it },
            label = { Text("বাংলা প্রশ্ন") },
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text("অপশনসমূহ এবং সঠিক উত্তর চিহ্নিত করুন:", fontSize = 12.sp, fontWeight = FontWeight.Bold)

          Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = correctOptionIndex == 0, onClick = { correctOptionIndex = 0 })
            OutlinedTextField(value = option1, onValueChange = { option1 = it }, label = { Text("অপশন ক") }, modifier = Modifier.weight(1f))
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = correctOptionIndex == 1, onClick = { correctOptionIndex = 1 })
            OutlinedTextField(value = option2, onValueChange = { option2 = it }, label = { Text("অপশন খ") }, modifier = Modifier.weight(1f))
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = correctOptionIndex == 2, onClick = { correctOptionIndex = 2 })
            OutlinedTextField(value = option3, onValueChange = { option3 = it }, label = { Text("অপশন গ") }, modifier = Modifier.weight(1f))
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = correctOptionIndex == 3, onClick = { correctOptionIndex = 3 })
            OutlinedTextField(value = option4, onValueChange = { option4 = it }, label = { Text("অপশন ঘ") }, modifier = Modifier.weight(1f))
          }

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = explanationBn,
            onValueChange = { explanationBn = it },
            label = { Text("বাংলা ব্যাখ্যা") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (questionBn.isNotBlank() && option1.isNotBlank() && option2.isNotBlank()) {
              val opts = listOf(option1, option2, option3.ifBlank { "N/A" }, option4.ifBlank { "N/A" })
              val q = Question(
                id = editingQuestion?.id ?: 0L,
                questionBn = questionBn.trim(),
                options = opts,
                correctOptionIndex = correctOptionIndex,
                explanationBn = explanationBn.trim(),
                categoryId = categoryId,
                points = points
              )
              viewModel.saveQuestion(q)
              showAddEditDialog = false
            }
          }
        ) {
          Text("সংরক্ষণ করুন")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddEditDialog = false }) {
          Text("বাতিল")
        }
      }
    )
  }

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = { Text("প্রশ্নভাণ্ডার (${questions.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(
            onClick = { showBulkImportDialog = true },
            modifier = Modifier.testTag("bulk_import_btn")
          ) {
            Icon(Icons.Default.FileUpload, contentDescription = "Bulk Import", tint = PrimaryGreen)
          }
        }
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = { openCreateDialog() },
        containerColor = PrimaryGreen,
        contentColor = androidx.compose.ui.graphics.Color.White,
        modifier = Modifier.testTag("admin_add_question_fab")
      ) {
        Icon(Icons.Default.Add, contentDescription = "Add Question")
      }
    },
    modifier = Modifier.testTag("admin_questions_screen")
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      // Search Bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("প্রশ্ন খুঁজুন...", fontSize = 13.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )

      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(filteredQuestions) { q ->
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = q.questionBn,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )

              Spacer(modifier = Modifier.height(6.dp))

              q.options.forEachIndexed { idx, opt ->
                val isCorrect = idx == q.correctOptionIndex
                Text(
                  text = "${if (idx == 0) "ক" else if (idx == 1) "খ" else if (idx == 2) "গ" else "ঘ"}. $opt ${if (isCorrect) "✓ (সঠিক)" else ""}",
                  fontSize = 12.sp,
                  color = if (isCorrect) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                  fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal
                )
              }

              if (q.explanationBn.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "ব্যাখ্যা: ${q.explanationBn}",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
              ) {
                IconButton(onClick = { viewModel.duplicateQuestion(q) }) {
                  Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = { openEditDialog(q) }) {
                  Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = { viewModel.deleteQuestion(q.id) }) {
                  Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
              }
            }
          }
        }
      }
    }
  }
}
