package com.example.mydicodingsubmission.ui.theme

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

// Light Theme Color Scheme
private val LightColorScheme = lightColorScheme(
    primary = Orange,
    onPrimary = White,
    secondary = Green,
    onSecondary = Black,
    tertiary = GreenY,
    background = White,
    surface = Cream,
    onSurface = DarkOnPrimary
)

// Dark Theme Color Scheme
private val DarkColorScheme = darkColorScheme(
    primary = DarkOrange,
    onPrimary = White,
    secondary = DarkGreen,
    onSecondary = White,
    tertiary = DarkGreenY,
    background = Black,
    surface = Color(0xFF2B2B2B),
    onSurface = Color(0xFFD1D1D1)
)

@Composable
fun MyDicodingSubmissionTheme(
    useDynamicColors: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        if (isSystemInDarkTheme()) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
    }

    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
