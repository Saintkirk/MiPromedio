package cl.mipromedio.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Taste-skill driven palette: dark premium + single calibrated accent (magenta)
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFC026D3),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFFE879F9),
    onSecondary = Color(0xFF1A001F),
    tertiary = Color(0xFFA21CAF),
    background = Color(0xFF0D0D12),
    onBackground = Color(0xFFF8F8FC),
    surface = Color(0xFF16161F),
    onSurface = Color(0xFFE4E4ED),
    surfaceVariant = Color(0xFF1E1E2A),
    onSurfaceVariant = Color(0xFFB0B0C0),
    outline = Color(0xFF3A3A4A),
    error = Color(0xFFEF4444),
    onError = Color(0xFFFFFFFF)
)

@Composable
fun MiPromedioTheme(
    darkTheme: Boolean = true, // forced dark for the premium look
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
