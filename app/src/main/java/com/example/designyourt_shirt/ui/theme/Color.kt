package com.example.designyourt_shirt.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Define the same colors for both light and dark themes
val AppColorScheme = lightColorScheme(
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
val DarkAppColorScheme = darkColorScheme(
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