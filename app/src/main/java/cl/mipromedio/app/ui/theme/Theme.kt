package cl.mipromedio.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Style F — Ember Gold: black + fiery orange + gold (iPhone premium)
private val EmberGoldScheme = darkColorScheme(
    primary = Color(0xFFFF6B00),
    onPrimary = Color(0xFF0A0A0A),
    secondary = Color(0xFFFFB800),
    onSecondary = Color(0xFF0A0A0A),
    tertiary = Color(0xFFFF8C33),
    background = Color(0xFF0A0A0A),
    onBackground = Color(0xFFFFF8F0),
    surface = Color(0xFF141414),
    onSurface = Color(0xFFF5E6D3),
    surfaceVariant = Color(0xFF1C1C1C),
    onSurfaceVariant = Color(0xFFB8A99A),
    outline = Color(0xFF3D342C),
    error = Color(0xFFEF4444),
    onError = Color(0xFFFFFFFF)
)

@Composable
fun MiPromedioTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EmberGoldScheme,
        typography = Typography,
        content = content
    )
}
