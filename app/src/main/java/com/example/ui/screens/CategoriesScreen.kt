package com.example.ui.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.model.Category
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppTopBar
import com.example.ui.navigation.NavRoutes
import com.example.ui.theme.PrimaryGreen
import com.example.ui.viewmodel.QuizViewModel

@Composable
fun CategoriesScreen(
  viewModel: QuizViewModel,
  onNavigate: (String) -> Unit,
  onCategorySelected: (Category) -> Unit
) {
  val categories by viewModel.categories.collectAsStateWithLifecycle()
  val user by viewModel.currentUser.collectAsStateWithLifecycle()
  var searchQuery by remember { mutableStateOf("") }

  val filteredCategories = remember(categories, searchQuery) {
    if (searchQuery.isBlank()) categories
    else categories.filter {
      it.nameBn.contains(searchQuery, ignoreCase = true) ||
        it.nameEn.contains(searchQuery, ignoreCase = true)
    }
  }

  Scaffold(
    topBar = {
      AppTopBar(
        title = "কুইজ ক্যাটাগরি",
        points = user?.totalPoints ?: 0,
        showBack = false
      )
    },
    bottomBar = {
      AppBottomNav(
        currentRoute = NavRoutes.CATEGORIES,
        onNavigate = onNavigate
      )
    },
    modifier = Modifier.testTag("categories_screen")
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
        placeholder = { Text("ক্যাটাগরি অনুসন্ধান করুন (উদাঃ মুক্তিযুদ্ধ, বিসিএস)...", fontSize = 13.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp)
          .testTag("categories_search_input"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )

      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(filteredCategories) { category ->
          val accentColor = when (category.id.toInt() % 5) {
            0 -> PrimaryGreen
            1 -> Color(0xFF0284C7)
            2 -> Color(0xFFD97706)
            3 -> Color(0xFF7C3AED)
            else -> Color(0xFFDC2626)
          }

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                viewModel.startQuizForCategory(category)
                onNavigate(NavRoutes.QUIZ_PLAY)
              }
              .testTag("category_item_${category.id}"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              horizontalAlignment = Alignment.Start
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Quiz,
                  contentDescription = category.nameBn,
                  tint = accentColor,
                  modifier = Modifier.size(24.dp)
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = category.nameBn,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )

              Text(
                text = category.nameEn,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = "${category.questionCount}টি প্রশ্ন",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = accentColor
              )
            }
          }
        }
      }
    }
  }
}
