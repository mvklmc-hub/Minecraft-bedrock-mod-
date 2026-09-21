package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MinecraftEmerald,
    onPrimary = Color(0xFF003816),
    primaryContainer = Color(0xFF005322),
    onPrimaryContainer = EmeraldLight,
    secondary = AmethystLight,
    onSecondary = Color(0xFF38006B),
    secondaryContainer = AmethystDark,
    onSecondaryContainer = Color(0xFFEADBFF),
    tertiary = LapisCyan,
    onTertiary = Color(0xFF003831),
    tertiaryContainer = Color(0xFF005349),
    onTertiaryContainer = Color(0xFF72EFDD),
    background = BedrockObsidian,
    onBackground = TextPrimary,
    surface = BedrockDeepslate,
    onSurface = TextPrimary,
    surfaceVariant = BedrockSurface,
    onSurfaceVariant = TextSecondary,
    outline = BedrockBorder,
    error = RedstonePulse,
    onError = Color.White
)

private val LightColorScheme = darkColorScheme(
    primary = MinecraftEmerald,
    onPrimary = Color(0xFF003816),
    primaryContainer = Color(0xFF005322),
    onPrimaryContainer = EmeraldLight,
    secondary = AmethystLight,
    onSecondary = Color(0xFF38006B),
    secondaryContainer = AmethystDark,
    onSecondaryContainer = Color(0xFFEADBFF),
    tertiary = LapisCyan,
    onTertiary = Color(0xFF003831),
    tertiaryContainer = Color(0xFF005349),
    onTertiaryContainer = Color(0xFF72EFDD),
    background = BedrockObsidian,
    onBackground = TextPrimary,
    surface = BedrockDeepslate,
    onSurface = TextPrimary,
    surfaceVariant = BedrockSurface,
    onSurfaceVariant = TextSecondary,
    outline = BedrockBorder,
    error = RedstonePulse,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep distinctive Bedrock fantasy theme
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
