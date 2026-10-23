package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = GroceryGreenDarkPrimary,
    onPrimary = GroceryGreenDarkOnPrimary,
    primaryContainer = GroceryGreenDarkContainer,
    onPrimaryContainer = GroceryGreenDarkOnContainer,
    secondary = GroceryOrangeSecondary,
    onSecondary = GroceryOrangeOnSecondary,
    secondaryContainer = GroceryOrangeContainer,
    onSecondaryContainer = GroceryOrangeOnContainer,
    tertiary = GroceryAmberTertiary,
    background = GroceryDarkBackground,
    surface = GroceryDarkSurface,
    surfaceVariant = GroceryDarkSurfaceVariant
)

private val LightColorScheme = lightColorScheme(
    primary = GroceryGreenPrimary,
    onPrimary = GroceryGreenOnPrimary,
    primaryContainer = GroceryGreenContainer,
    onPrimaryContainer = GroceryGreenOnContainer,
    secondary = GroceryOrangeSecondary,
    onSecondary = GroceryOrangeOnSecondary,
    secondaryContainer = GroceryOrangeContainer,
    onSecondaryContainer = GroceryOrangeOnContainer,
    tertiary = GroceryAmberTertiary,
    tertiaryContainer = GroceryAmberContainer,
    background = GroceryBackground,
    surface = GrocerySurface,
    surfaceVariant = GrocerySurfaceVariant,
    outline = GroceryOutline
)

@Composable
fun AlbakalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
