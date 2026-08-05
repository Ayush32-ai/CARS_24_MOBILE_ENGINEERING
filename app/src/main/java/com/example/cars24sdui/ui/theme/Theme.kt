package com.example.cars24sdui.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CarsOrange,
    secondary = Color(0xFFFFB77E),
    background = DarkBackground,
    surface = Color(0xFF1F2937),
    onPrimary = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = CarsOrange,
    secondary = CarsOrangeDark,
    background = AppBackground,
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Ink,
    onSurface = Ink
)

@Composable
fun Cars24SDUITheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
