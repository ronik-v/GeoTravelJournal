package com.ronik.geotraveljournal.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun GeoTravelTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = Color(0xFFA8D5A2),
        primaryContainer = Color(0xFF7CB67A),
        secondary = Color(0xFFE6F5E5),
        background = Color(0xFFF0FAF0),
        surface = Color.White,
        error = Color.Red,
        onPrimary = Color.Black,
        onSecondary = Color.Black,
        onBackground = Color.Black,
        onSurface = Color.Black
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
