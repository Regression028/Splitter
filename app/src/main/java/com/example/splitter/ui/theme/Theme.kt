package com.example.splitter.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Emerald60,
    onPrimary = Emerald20,
    primaryContainer = Emerald20,
    onPrimaryContainer = Emerald80,
    secondary = Blue80,
    onSecondary = Blue20,
    error = CoralRed,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurface
)

private val LightColorScheme = lightColorScheme(
    primary = Emerald40,
    onPrimary = LightSurface,
    primaryContainer = Emerald80.copy(alpha = 0.3f),
    onPrimaryContainer = Emerald20,
    secondary = Blue40,
    onSecondary = LightSurface,
    error = CoralRed,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightBackground
)

@Composable
fun SplitterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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
