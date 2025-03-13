package com.example.designyourt_shirt.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

// Define the same colors for both light and dark themes
private val AppColrScheme = lightColorScheme(
    primary = Color(0xFF6200EE), // Purple 500
    onPrimary = Color(0xFFFFFFFF), // White
    secondary = Color(0xFF03DAC5), // Teal 200
    onSecondary = Color(0xFF000000), // Black
    background = Color(0xFFFFFFFF), // White
    onBackground = Color(0xFF000000), // Black
    surface = Color(0xFFFFFFFF), // White
    onSurface = Color(0xFF000000), // Black
    // Add other colors as needed
)

// Use the same colors for the dark color scheme
private val DarkAppColrScheme = darkColorScheme(
    primary = Color(0xFF6200EE), // Purple 500
    onPrimary = Color(0xFFFFFFFF), // White
    secondary = Color(0xFF03DAC5), // Teal 200
    onSecondary = Color(0xFF000000), // Black
    background = Color(0xFFFFFFFF), // White
    onBackground = Color(0xFF000000), // Black
    surface = Color(0xFFFFFFFF), // White
    onSurface = Color(0xFF000000), // Black
    // Add other colors as needed
)

@Composable
fun DesignYourTShirtTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disable dynamic color to enforce consistent colors
    content: @Composable () -> Unit
) {
    // Use the same color scheme for both light and dark themes
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkAppColrScheme
        else -> AppColrScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}