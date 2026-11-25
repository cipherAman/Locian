package io.locian.core.ui.theme

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
    primary = LocianRed,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = LocianRedContainerDark,
    onPrimaryContainer = LocianRedGlow,
    secondary = LocianRedLight,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    background = MidnightDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = LocianRed,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = LocianRedContainerLight,
    onPrimaryContainer = LocianRedDark,
    secondary = LocianRed,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    background = SurfaceLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    error = ErrorRed
)

@Composable
fun LocianTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
