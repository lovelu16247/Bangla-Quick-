package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.PrimaryGreenDark

data class OnboardingStep(
  val title: String,
  val description: String,
  val icon: ImageVector,
  val color: Color
)

@Composable
fun OnboardingScreen(
  onFinishOnboarding: () -> Unit
) {
  val steps = remember {
    listOf(
      OnboardingStep(
        title = "বিশাল বাংলা প্রশ্নভাণ্ডার",
        description = "বাংলাদেশ, মুক্তিযুদ্ধ, বিসিএস, ব্যাংকিং, সাহিত্য ও বিজ্ঞানের হাজারো নির্ভুল প্রশ্ন ও ব্যাখ্যাসহ প্রস্তুতি নিন।",
        icon = Icons.Default.Quiz,
        color = PrimaryGreen
      ),
      OnboardingStep(
        title = "১০০টি লেভেল চ্যালেঞ্জ",
        description = "লেভেল ১ থেকে ১০০ পর্যন্ত প্রতিটি ধাপে নতুন জ্ঞান অর্জন করুন, স্কোর করুন এবং আনলক করুন নতুন অধ্যায়।",
        icon = Icons.Default.EmojiEvents,
        color = GoldenAmber
      ),
      OnboardingStep(
        title = "মডেল টেস্ট ও লিডারবোর্ড",
        description = "সময়সীমাবদ্ধ এক্সামে অংশ নিন, নেগেটিভ মার্কিং হিসাব দেখুন এবং সমগ্র বাংলাদেশের শিক্ষার্থীদের সাথে প্রতিযোগিতা করুন।",
        icon = Icons.Default.School,
        color = Color(0xFF0284C7)
      )
    )
  }

  var currentStepIndex by remember { mutableIntStateOf(0) }
  val currentStep = steps[currentStepIndex]

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 24.dp, vertical = 32.dp)
      .testTag("onboarding_screen"),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    // Top bar: Skip button
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.End
    ) {
      TextButton(
        onClick = onFinishOnboarding,
        modifier = Modifier.testTag("onboarding_skip_button")
      ) {
        Text("এড়িয়ে যান", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
      }
    }

    // Center visual
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      Box(
        modifier = Modifier
          .size(150.dp)
          .clip(CircleShape)
          .background(
            Brush.radialGradient(
              colors = listOf(
                currentStep.color.copy(alpha = 0.25f),
                currentStep.color.copy(alpha = 0.05f)
              )
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(currentStep.color),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = currentStep.icon,
            contentDescription = currentStep.title,
            tint = Color.White,
            modifier = Modifier.size(54.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(36.dp))

      Text(
        text = currentStep.title,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground
      )

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = currentStep.description,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp)
      )
    }

    // Bottom indicators & Next button
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 28.dp)
      ) {
        steps.indices.forEach { index ->
          val isSelected = index == currentStepIndex
          Box(
            modifier = Modifier
              .padding(horizontal = 4.dp)
              .height(8.dp)
              .width(if (isSelected) 24.dp else 8.dp)
              .clip(CircleShape)
              .background(
                if (isSelected) currentStep.color else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
              )
          )
        }
      }

      Button(
        onClick = {
          if (currentStepIndex < steps.size - 1) {
            currentStepIndex++
          } else {
            onFinishOnboarding()
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("onboarding_next_button"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
      ) {
        Text(
          text = if (currentStepIndex == steps.size - 1) "শুরু করুন" else "পরবর্তী",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = "Next",
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}
