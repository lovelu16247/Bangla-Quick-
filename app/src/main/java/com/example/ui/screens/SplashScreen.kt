package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.PrimaryGreenDark
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
  onNavigateNext: () -> Unit
) {
  var startAnimation by remember { mutableStateOf(false) }

  val alphaAnim by animateFloatAsState(
    targetValue = if (startAnimation) 1f else 0f,
    animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
    label = "splashAlpha"
  )

  val scaleAnim by animateFloatAsState(
    targetValue = if (startAnimation) 1f else 0.8f,
    animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
    label = "splashScale"
  )

  LaunchedEffect(Unit) {
    startAnimation = true
    delay(1800)
    onNavigateNext()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            PrimaryGreen,
            PrimaryGreenDark,
            Color(0xFF042416)
          )
        )
      )
      .testTag("splash_screen"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier
        .scale(scaleAnim)
        .alpha(alphaAnim)
        .padding(24.dp)
    ) {
      Box(
        modifier = Modifier
          .size(110.dp)
          .clip(CircleShape)
          .background(Color.White.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = R.drawable.ic_quiz_logo),
          contentDescription = "App Logo",
          modifier = Modifier
            .size(90.dp)
            .clip(CircleShape)
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      Text(
        text = "বাংলা কুইজ মাস্টার",
        fontSize = 28.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color.White
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "জ্ঞানচর্চা ও মেধা বিকাশের সেরা ঠিকানা",
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = GoldenAmber
      )

      Spacer(modifier = Modifier.height(36.dp))

      CircularProgressIndicator(
        color = GoldenAmber,
        strokeWidth = 3.dp,
        modifier = Modifier.size(28.dp)
      )
    }
  }
}
