package cl.mipromedio.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.mipromedio.app.ui.theme.MiPromedioTheme
import kotlin.math.round

/**
 * Lógica exacta solicitada:
 * - 4 notas con 4 porcentajes que representan el 75% de la nota final
 * - Examen vale 25%
 * - Si promedio de presentación ≥ 5.0 → exención (no da examen)
 * - EXCEPTO si el curso es Online → el examen final SIEMPRE se realiza
 */
data class NotaInput(
    val nota: String = "",
    val porcentaje: String = ""
)

@Composable
fun MiPromedioApp() {
    var notas by remember {
        mutableStateOf(
            listOf(
                NotaInput(), NotaInput(), NotaInput(), NotaInput()
            )
        )
    }
    var esOnline by remember { mutableStateOf(false) }
    var notaExamen by remember { mutableStateOf("") }
    var metaFinal by remember { mutableStateOf("4.0") }

    val scrollState = rememberScrollState()

    // Cálculos reactivos
    val resultado = remember(notas, esOnline, notaExamen, metaFinal) {
        calcularResultado(notas, esOnline, notaExamen, metaFinal)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header
        Header()

        Spacer(Modifier.height(20.dp))

        // Toggle Online / Presencial
        ModoCursoCard(
            esOnline = esOnline,
            onToggle = { esOnline = it }
        )

        Spacer(Modifier.height(16.dp))

        // 4 Notas (75%)
        NotasCard(
            notas = notas,
            onNotaChange = { index, value ->
                notas = notas.toMutableList().also { it[index] = it[index].copy(nota = value) }
            },
            onPorcentajeChange = { index, value ->
                notas = notas.toMutableList().also { it[index] = it[index].copy(porcentaje = value) }
            }
        )

        Spacer(Modifier.height(12.dp))

        // Info de porcentajes
        val sumaPct = notas.sumOf { it.porcentaje.toDoubleOrNull() ?: 0.0 }
        InfoChip(
            text = if (sumaPct in 74.5..75.5) "✓ Porcentajes suman ~75%"
            else "Los 4 porcentajes deben sumar 75% (actual: ${"%.1f".format(sumaPct)}%)",
            isOk = sumaPct in 74.5..75.5
        )

        Spacer(Modifier.height(16.dp))

        // Examen (solo si no hay exención o es Online)
        AnimatedVisibility(
            visible = resultado.requiereExamen,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut()
        ) {
            ExamenCard(
                notaExamen = notaExamen,
                onNotaChange = { notaExamen = it },
                metaFinal = metaFinal,
                onMetaChange = { metaFinal = it },
                notaNecesaria = resultado.notaNecesariaExamen
            )
        }

        Spacer(Modifier.height(16.dp))

        // Resultados
        ResultadosCard(resultado = resultado)

        Spacer(Modifier.height(24.dp))

        // Botones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    notas = listOf(NotaInput(), NotaInput(), NotaInput(), NotaInput())
                    notaExamen = ""
                    metaFinal = "4.0"
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Limpiar")
            }
        }

        Spacer(Modifier.height(32.dp))

        // Footer taste-skill
        Text(
            text = "Diseñado con taste-skill · Escala 1.0 – 7.0",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
    }
}

// (Resto de composables y lógica de cálculo en el archivo completo del proyecto local)
