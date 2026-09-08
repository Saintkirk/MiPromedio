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
import androidx.compose.material.icons.filled.ArrowForward
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
import kotlin.math.round

data class NotaInput(
    val nota: String = "",
    val porcentaje: String = ""
)

@Composable
fun MiPromedioApp() {
    var notas by remember {
        mutableStateOf(listOf(NotaInput(), NotaInput(), NotaInput(), NotaInput()))
    }
    var esOnline by remember { mutableStateOf(false) }
    var notaExamen by remember { mutableStateOf("") }
    var metaFinal by remember { mutableStateOf("4.0") }
    var calculado by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val resultado = remember(notas, esOnline, notaExamen, metaFinal) {
        calcularResultado(notas, esOnline, notaExamen, metaFinal)
    }

    val notasCompletas = notas.all {
        val n = it.nota.replace(',', '.').toDoubleOrNull()
        val p = it.porcentaje.replace(',', '.').toDoubleOrNull()
        n != null && p != null && n in 1.0..7.0 && p > 0
    }
    val sumaPct = notas.sumOf { it.porcentaje.replace(',', '.').toDoubleOrNull() ?: 0.0 }
    val pctOk = sumaPct in 74.0..76.0

    LaunchedEffect(esOnline) {
        calculado = false
        notaExamen = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Header()
        Spacer(Modifier.height(20.dp))
        ModoCursoCard(esOnline = esOnline, onToggle = { esOnline = it })
        Spacer(Modifier.height(16.dp))
        NotasCard(
            notas = notas,
            onNotaChange = { index, value ->
                notas = notas.toMutableList().also { it[index] = it[index].copy(nota = value) }
                calculado = false
            },
            onPorcentajeChange = { index, value ->
                notas = notas.toMutableList().also { it[index] = it[index].copy(porcentaje = value) }
                calculado = false
            }
        )
        Spacer(Modifier.height(12.dp))
        InfoChip(
            text = if (pctOk) "✓ Porcentajes suman ~75%"
            else "Los 4 porcentajes deben sumar 75% (actual: ${"%.1f".format(sumaPct)}%)",
            isOk = pctOk
        )
        Spacer(Modifier.height(16.dp))

        if (!calculado) {
            Button(
                onClick = { calculado = true },
                enabled = notasCompletas && pctOk,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                if (esOnline) {
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Avanzar al examen final", fontWeight = FontWeight.SemiBold)
                } else {
                    Icon(Icons.Default.Calculate, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Calcular promedio (presencial)", fontWeight = FontWeight.SemiBold)
                }
            }
            if (!notasCompletas || !pctOk) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Completa las 4 notas (1.0–7.0) y porcentajes que sumen 75%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        AnimatedVisibility(
            visible = calculado,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut()
        ) {
            Column {
                Spacer(Modifier.height(8.dp))
                ResultadosCard(resultado = resultado)
                Spacer(Modifier.height(16.dp))
                val mostrarExamen = esOnline || resultado.requiereExamen
                if (mostrarExamen) {
                    ExamenCard(
                        notaExamen = notaExamen,
                        onNotaChange = { notaExamen = it },
                        metaFinal = metaFinal,
                        onMetaChange = { metaFinal = it },
                        notaNecesaria = resultado.notaNecesariaExamen,
                        esOnline = esOnline
                    )
                    Spacer(Modifier.height(16.dp))
                    if (notaExamen.isNotBlank() && resultado.promedioFinal != null) {
                        ResultadosFinalCard(resultado = resultado)
                        Spacer(Modifier.height(16.dp))
                    }
                } else if (resultado.exento) {
                    ExentoCard(promedio = resultado.promedioPresentacion)
                    Spacer(Modifier.height(16.dp))
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        OutlinedButton(
            onClick = {
                notas = listOf(NotaInput(), NotaInput(), NotaInput(), NotaInput())
                notaExamen = ""
                metaFinal = "4.0"
                calculado = false
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Limpiar todo")
        }
        Spacer(Modifier.height(32.dp))
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

@Composable
private fun Header() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "MiPromedio",
            style = MaterialTheme.typography.displayLarge.copy(
                brush = Brush.linearGradient(colors = listOf(Color(0xFFE879F9), Color(0xFFC026D3)))
            ),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "4 notas (75%) + Examen (25%)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ModoCursoCard(esOnline: Boolean, onToggle: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Modalidad del curso", style = MaterialTheme.typography.titleMedium)
                Text(
                    if (esOnline) "Online → Examen obligatorio siempre"
                    else "Presencial → Exención si ≥ 5.0",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = esOnline,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
private fun NotasCard(
    notas: List<NotaInput>,
    onNotaChange: (Int, String) -> Unit,
    onPorcentajeChange: (Int, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("Notas de presentación (75%)", style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.height(12.dp))
            notas.forEachIndexed { index, nota ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = nota.nota,
                        onValueChange = { onNotaChange(index, it.filter { c -> c.isDigit() || c == '.' || c == ',' }) },
                        label = { Text("Nota ${index + 1}") },
                        placeholder = { Text("1.0 – 7.0") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = nota.porcentaje,
                        onValueChange = { onPorcentajeChange(index, it.filter { c -> c.isDigit() || c == '.' || c == ',' }) },
                        label = { Text("%") },
                        placeholder = { Text("ej: 20") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(0.8f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ExamenCard(
    notaExamen: String,
    onNotaChange: (String) -> Unit,
    metaFinal: String,
    onMetaChange: (String) -> Unit,
    notaNecesaria: Double?,
    esOnline: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                if (esOnline) "Examen Final (25%) — Obligatorio (Online)"
                else "Examen Final (25%) — Debes rendirlo",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = notaExamen,
                    onValueChange = { onNotaChange(it.filter { c -> c.isDigit() || c == '.' || c == ',' }) },
                    label = { Text("Nota del examen") },
                    placeholder = { Text("1.0 – 7.0") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = metaFinal,
                    onValueChange = { onMetaChange(it.filter { c -> c.isDigit() || c == '.' || c == ',' }) },
                    label = { Text("Meta final") },
                    placeholder = { Text("4.0") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            if (notaNecesaria != null) {
                Spacer(Modifier.height(10.dp))
                val color = when {
                    notaNecesaria > 7.0 -> MaterialTheme.colorScheme.error
                    notaNecesaria < 1.0 -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.secondary
                }
                Text(
                    text = when {
                        notaNecesaria > 7.0 -> "Imposible alcanzar la meta (necesitas ${"%.2f".format(notaNecesaria)})"
                        notaNecesaria < 1.0 -> "Ya tienes la meta asegurada"
                        else -> "Necesitas ${"%.2f".format(notaNecesaria)} en el examen para llegar a $metaFinal"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = color,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ResultadosCard(resultado: ResultadoCalculo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Promedio de presentación", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            Text(
                resultado.promedioPresentacion?.let { "%.2f".format(it) } ?: "—",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 42.sp),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text("sobre 7.0  ·  vale 75%", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ResultadosFinalCard(resultado: ResultadoCalculo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Nota final", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            Text(
                resultado.promedioFinal?.let { "%.2f".format(it) } ?: "—",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 42.sp),
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(10.dp))
            val (estadoText, estadoColor) = when {
                resultado.promedioFinal != null && resultado.promedioFinal!! >= 4.0 -> "Aprobado" to Color(0xFF22C55E)
                resultado.promedioFinal != null -> "Reprobado" to Color(0xFFEF4444)
                else -> "—" to MaterialTheme.colorScheme.onSurfaceVariant
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(estadoColor.copy(alpha = 0.15f))
                    .border(1.dp, estadoColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(estadoText, color = estadoColor, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun ExentoCard(promedio: Double?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF22C55E).copy(alpha = 0.12f)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF22C55E).copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🎉 Exento de examen", style = MaterialTheme.typography.titleLarge, color = Color(0xFF22C55E))
            Spacer(Modifier.height(8.dp))
            Text(
                "Promedio ${promedio?.let { "%.2f".format(it) } ?: "—"} ≥ 5.0",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF22C55E)
            )
            Text(
                "Modalidad presencial → no rinde examen final",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            Text(
                promedio?.let { "%.2f".format(it) } ?: "—",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 42.sp),
                color = Color(0xFF22C55E),
                fontWeight = FontWeight.Bold
            )
            Text("Nota final", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun InfoChip(text: String, isOk: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isOk) Color(0xFF22C55E).copy(alpha = 0.12f)
                else Color(0xFF3B82F6).copy(alpha = 0.12f)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Info,
            contentDescription = null,
            tint = if (isOk) Color(0xFF22C55E) else Color(0xFF3B82F6),
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isOk) Color(0xFF22C55E) else Color(0xFF93C5FD)
        )
    }
}

data class ResultadoCalculo(
    val promedioPresentacion: Double?,
    val promedioFinal: Double?,
    val requiereExamen: Boolean,
    val exento: Boolean,
    val notaNecesariaExamen: Double?
)

private fun calcularResultado(
    notas: List<NotaInput>,
    esOnline: Boolean,
    notaExamenStr: String,
    metaStr: String
): ResultadoCalculo {
    val valores = notas.mapNotNull { n ->
        val nota = n.nota.replace(',', '.').toDoubleOrNull()
        val pct = n.porcentaje.replace(',', '.').toDoubleOrNull()
        if (nota != null && pct != null && nota in 1.0..7.0 && pct > 0) nota to pct else null
    }
    if (valores.size < 4) {
        return ResultadoCalculo(null, null, true, false, null)
    }
    val sumaPct = valores.sumOf { it.second }
    val promedioPresentacion = if (sumaPct > 0) {
        valores.sumOf { it.first * it.second } / sumaPct
    } else null
    if (promedioPresentacion == null) {
        return ResultadoCalculo(null, null, true, false, null)
    }
    val exento = promedioPresentacion >= 5.0 && !esOnline
    val requiereExamen = !exento
    val notaExamen = notaExamenStr.replace(',', '.').toDoubleOrNull()
    val meta = metaStr.replace(',', '.').toDoubleOrNull() ?: 4.0
    val promedioFinal = when {
        exento -> promedioPresentacion
        notaExamen != null && notaExamen in 1.0..7.0 -> {
            promedioPresentacion * 0.75 + notaExamen * 0.25
        }
        else -> null
    }
    val notaNecesaria = if (requiereExamen) {
        val needed = (meta - promedioPresentacion * 0.75) / 0.25
        round(needed * 100) / 100.0
    } else null
    return ResultadoCalculo(
        promedioPresentacion = round(promedioPresentacion * 100) / 100.0,
        promedioFinal = promedioFinal?.let { round(it * 100) / 100.0 },
        requiereExamen = requiereExamen,
        exento = exento,
        notaNecesariaExamen = notaNecesaria
    )
}
