package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = PrimaryGreenLight,
  onPrimary = PrimaryGreenDark,
  primaryContainer = PrimaryGreenDark,
  onPrimaryContainer = PrimaryGreenLight,
  secondary = GoldenAmber,
  onSecondary = Color.Black,
  secondaryContainer = GoldenAmberDark,
  onSecondaryContainer = GoldenAmberLight,
  tertiary = TealCyan,
  background = BgDark,
  surface = SurfaceDark,
  surfaceVariant = SurfaceVariantDark,
  onBackground = TextPrimaryDark,
  onSurface = TextPrimaryDark,
  onSurfaceVariant = TextSecondaryDark,
  error = ErrorRed,
  outline = BorderDark
)

private val LightColorScheme = lightColorScheme(
  primary = PrimaryGreen,
  onPrimary = Color.White,
  primaryContainer = PrimaryGreenContainer,
  onPrimaryContainer = OnPrimaryGreenContainer,
  secondary = GoldenAmber,
  onSecondary = Color.Black,
  secondaryContainer = GoldenAmberContainer,
  onSecondaryContainer = GoldenAmberDark,
  tertiary = TealCyan,
  background = BgLight,
  surface = SurfaceLight,
  surfaceVariant = SurfaceVariantLight,
  onBackground = TextPrimaryLight,
  onSurface = TextPrimaryLight,
  onSurfaceVariant = TextSecondaryLight,
  error = ErrorRed,
  outline = BorderLight
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep consistent emerald & gold identity
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
