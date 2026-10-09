package com.jainpanchang.ui.theme

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
    primary = SaffronPrimaryDark,
    onPrimary = SaffronOnPrimaryDark,
    primaryContainer = SaffronPrimaryContainerDark,
    onPrimaryContainer = SaffronOnPrimaryContainerDark,
    secondary = GoldenSecondaryDark,
    onSecondary = GoldenOnSecondaryDark,
    secondaryContainer = GoldenSecondaryContainerDark,
    onSecondaryContainer = GoldenOnSecondaryContainerDark,
    tertiary = MaroonTertiaryDark,
    onTertiary = MaroonOnTertiaryDark,
    tertiaryContainer = MaroonTertiaryContainerDark,
    onTertiaryContainer = MaroonOnTertiaryContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = SaffronPrimary,
    onPrimary = SaffronOnPrimary,
    primaryContainer = SaffronPrimaryContainer,
    onPrimaryContainer = SaffronOnPrimaryContainer,
    secondary = GoldenSecondary,
    onSecondary = GoldenOnSecondary,
    secondaryContainer = GoldenSecondaryContainer,
    onSecondaryContainer = GoldenOnSecondaryContainer,
    tertiary = MaroonTertiary,
    onTertiary = MaroonOnTertiary,
    tertiaryContainer = MaroonTertiaryContainer,
    onTertiaryContainer = MaroonOnTertiaryContainer,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight
)

@Composable
fun JainPanchangTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // spiritual palette by default
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
