# MiPromedio – Calculadora de Notas (APK)

App nativa Android que replica la lógica de [mipromedio.cl](https://mipromedio.cl/) con las reglas específicas solicitadas:

## Reglas de cálculo
- **4 notas** con sus **4 porcentajes** (deben sumar **75%**)
- **Examen final** = **25%**
- Si el promedio de presentación es **≥ 5.0** → **exención** (no se rinde examen)
- **Excepto** si el curso es **Online** → el examen final **siempre es obligatorio**
- Escala: **1.0 – 7.0**

## Visuales
Diseñado aplicando la skill **taste-skill** (https://github.com/Saintkirk/taste-skill):
- Dark premium background
- Acento magenta calibrado (sin púrpura AI genérico)
- Tipografía limpia y tracking controlado
- Densidad equilibrada
- Motion sutil (AnimatedVisibility)
- Alto contraste y legibilidad

## Requisitos para build
- Android Studio Ladybug+ / AGP 8.7+
- JDK 17
- SDK 35

## Generar APK

```bash
./gradlew assembleDebug
# APK en: app/build/outputs/apk/debug/app-debug.apk
```

```bash
./gradlew assembleRelease
```

## Estructura
- Kotlin + Jetpack Compose + Material 3
- Lógica de cálculo 100% en Compose (reactiva)
- Sin dependencias externas innecesarias

---

**Confirmación:** Se utilizó la skill taste-skill para las visuales del proyecto.
