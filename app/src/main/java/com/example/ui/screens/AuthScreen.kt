package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.QuizRepository
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.PrimaryGreen
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
  repository: QuizRepository,
  onAuthSuccess: () -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Register
  val coroutineScope = rememberCoroutineScope()

  // Login Form
  var loginIdentifier by remember { mutableStateOf("demo_player") }
  var loginPassword by remember { mutableStateOf("123456") }

  // Register Form
  var regFullName by remember { mutableStateOf("") }
  var regUsername by remember { mutableStateOf("") }
  var regEmail by remember { mutableStateOf("") }
  var regPassword by remember { mutableStateOf("") }

  var errorMessage by remember { mutableStateOf<String?>(null) }
  var isLoading by remember { mutableStateOf(false) }

  Surface(
    modifier = Modifier
      .fillMaxSize()
      .testTag("auth_screen"),
    color = MaterialTheme.colorScheme.background
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(72.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(PrimaryGreen),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Quiz,
          contentDescription = "Quiz Logo",
          tint = Color.White,
          modifier = Modifier.size(40.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "বাংলা কুইজ মাস্টার",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )

      Text(
        text = "কুইজ খেলুন, শিখুন এবং জাতীয় মেধা তালিকায় শীর্ষে উঠুন",
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
      )

      TabRow(
        selectedTabIndex = selectedTab,
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp)),
        containerColor = MaterialTheme.colorScheme.surfaceVariant
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0; errorMessage = null },
          text = { Text("লগইন", fontWeight = FontWeight.Bold) },
          modifier = Modifier.testTag("auth_tab_login")
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1; errorMessage = null },
          text = { Text("নতুন নিবন্ধন", fontWeight = FontWeight.Bold) },
          modifier = Modifier.testTag("auth_tab_register")
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      if (errorMessage != null) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.errorContainer,
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
        ) {
          Text(
            text = errorMessage!!,
            color = MaterialTheme.colorScheme.onErrorContainer,
            fontSize = 13.sp,
            modifier = Modifier.padding(12.dp)
          )
        }
      }

      if (selectedTab == 0) {
        // Login View
        OutlinedTextField(
          value = loginIdentifier,
          onValueChange = { loginIdentifier = it },
          label = { Text("ইমেইল বা ইউজারনেম") },
          leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("login_input_username"),
          shape = RoundedCornerShape(12.dp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = loginPassword,
          onValueChange = { loginPassword = it },
          label = { Text("পাসওয়ার্ড") },
          leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
          visualTransformation = PasswordVisualTransformation(),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("login_input_password"),
          shape = RoundedCornerShape(12.dp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
          onClick = {
            isLoading = true
            errorMessage = null
            coroutineScope.launch {
              val res = repository.login(loginIdentifier, loginPassword)
              isLoading = false
              if (res.isSuccess) {
                onAuthSuccess()
              } else {
                errorMessage = res.exceptionOrNull()?.message ?: "লগইন ব্যর্থ হয়েছে"
              }
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("login_submit_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
          enabled = !isLoading
        ) {
          if (isLoading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
          } else {
            Text("লগইন করুন", fontSize = 16.sp, fontWeight = FontWeight.Bold)
          }
        }
      } else {
        // Register View
        OutlinedTextField(
          value = regFullName,
          onValueChange = { regFullName = it },
          label = { Text("আপনার পূর্ণ নাম") },
          leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("reg_input_fullname"),
          shape = RoundedCornerShape(12.dp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = regUsername,
          onValueChange = { regUsername = it },
          label = { Text("ইউজারনেম") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("reg_input_username"),
          shape = RoundedCornerShape(12.dp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = regEmail,
          onValueChange = { regEmail = it },
          label = { Text("ইমেইল ঠিকানা") },
          leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("reg_input_email"),
          shape = RoundedCornerShape(12.dp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = regPassword,
          onValueChange = { regPassword = it },
          label = { Text("পাসওয়ার্ড (কমপক্ষে ৬ অক্ষর)") },
          leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
          visualTransformation = PasswordVisualTransformation(),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("reg_input_password"),
          shape = RoundedCornerShape(12.dp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = {
            isLoading = true
            errorMessage = null
            coroutineScope.launch {
              val res = repository.register(regUsername, regEmail, regPassword, regFullName)
              isLoading = false
              if (res.isSuccess) {
                onAuthSuccess()
              } else {
                errorMessage = res.exceptionOrNull()?.message ?: "নিবন্ধন ব্যর্থ হয়েছে"
              }
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("register_submit_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
          enabled = !isLoading
        ) {
          if (isLoading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
          } else {
            Text("নিবন্ধন করুন ও ১০০ পয়েন্ট জিতুন", fontSize = 15.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      OutlinedButton(
        onClick = onAuthSuccess,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("guest_login_button"),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text("গেস্ট হিসেবে কুইজ শুরু করুন", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
      }
    }
  }
}
