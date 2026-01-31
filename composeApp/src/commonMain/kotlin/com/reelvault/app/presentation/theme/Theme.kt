package com.reelvault.app.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Aurora Dark Theme for ReelVault
 * Material3 dark color scheme implementing the Midnight Aurora palette.
 */
private val AuroraDarkColorScheme = darkColorScheme(
    // Primary colors
    primary = AuroraColors.BrightIndigo,
    onPrimary = AuroraColors.TextPrimary,
    primaryContainer = AuroraColors.RichIndigo,
    onPrimaryContainer = AuroraColors.TextPrimary,

    // Secondary colors
    secondary = AuroraColors.SoftViolet,
    onSecondary = AuroraColors.TextPrimary,
    secondaryContainer = AuroraColors.DarkViolet,
    onSecondaryContainer = AuroraColors.VioletGlow,

    // Tertiary colors
    tertiary = AuroraColors.LightViolet,
    onTertiary = AuroraColors.TextPrimary,
    tertiaryContainer = AuroraColors.DarkViolet,
    onTertiaryContainer = AuroraColors.VioletGlow,

    // Background
    background = AuroraColors.MidnightIndigo,
    onBackground = AuroraColors.TextPrimary,

    // Surface
    surface = AuroraColors.DeepIndigo,
    onSurface = AuroraColors.TextPrimary,
    surfaceVariant = AuroraColors.MediumCharcoal,
    onSurfaceVariant = AuroraColors.TextSecondary,

    // Surface containers
    surfaceContainer = AuroraColors.DarkCharcoal,
    surfaceContainerHigh = AuroraColors.MediumCharcoal,
    surfaceContainerHighest = AuroraColors.LightCharcoal,
    surfaceContainerLow = AuroraColors.DeepIndigo,
    surfaceContainerLowest = AuroraColors.MidnightIndigo,

    // Error
    error = AuroraColors.AuroraRed,
    onError = AuroraColors.TextPrimary,
    errorContainer = Color(0xFF5C0000),
    onErrorContainer = AuroraColors.AuroraRed,

    // Outline
    outline = AuroraColors.SmokeGray,
    outlineVariant = AuroraColors.LightCharcoal,

    // Other
    scrim = Color(0x80000000),
    inverseSurface = AuroraColors.TextPrimary,
    inverseOnSurface = AuroraColors.MidnightIndigo,
    inversePrimary = AuroraColors.DarkViolet,
)

/**
 * ReelVault's Aurora Theme
 * Applies the Midnight Aurora design system to the app.
 */
@Composable
fun AuroraTheme(
    darkTheme: Boolean = true, // Always dark for now
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AuroraDarkColorScheme,
        typography = AuroraTypography,
        content = content
    )
}
