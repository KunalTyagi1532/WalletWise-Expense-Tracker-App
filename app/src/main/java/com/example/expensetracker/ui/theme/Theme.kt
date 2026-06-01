package com.example.expensetracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// =====================================================
// LIGHT COLORS
// =====================================================

private val LightColors = lightColorScheme(

    primary = Color(0xFF5C6BC0),

    onPrimary = Color.White,

    primaryContainer = Color(0xFFDDE1FF),

    onPrimaryContainer = Color(0xFF001257),

    secondary = Color(0xFF625B71),

    background = Color(0xFFF6F7FB),

    surface = Color(0xFFFFFFFF),

    surfaceVariant = Color(0xFFE8EAF6),

    onSurface = Color(0xFF1C1B1F),

    onSurfaceVariant = Color(0xFF49454F),

    error = Color(0xFFB3261E)
)

// =====================================================
// DARK COLORS
// =====================================================

private val DarkColors = darkColorScheme(

    primary = Color(0xFF8EA2FF),

    onPrimary = Color(0xFF0D1B5E),

    primaryContainer = Color(0xFF1E2D74),

    onPrimaryContainer = Color(0xFFDCE1FF),

    secondary = Color(0xFFBFC6DC),

    background = Color(0xFF05070A),

    surface = Color(0xFF11141A),

    surfaceVariant = Color(0xFF1F2430),
    onSurface = Color(0xFFF3F4F7),

    onSurfaceVariant = Color(0xFFADB3C2),

    error = Color(0xFFFF6B6B),

    errorContainer = Color(0xFF3A1212),

    tertiaryContainer = Color(0xFF102A1C)
)

@Composable
fun ExpenseTrackerTheme(

    darkTheme: Boolean = isSystemInDarkTheme(),

    dynamicColor: Boolean = true,

    content: @Composable () -> Unit
) {

    val colorScheme = when {

        dynamicColor &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {

            val context = LocalContext.current

            if (darkTheme) {

                dynamicDarkColorScheme(context)

            } else {

                dynamicLightColorScheme(context)
            }
        }

        darkTheme -> DarkColors

        else -> LightColors
    }

    MaterialTheme(

        colorScheme = colorScheme,

        typography = Typography,

        content = content
    )
}