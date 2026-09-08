# MiPromedio – Calculadora de Notas (APK)

App nativa Android (Kotlin + Jetpack Compose) que implementa la lógica de notas de [mipromedio.cl](https://mipromedio.cl) con reglas específicas:

## Reglas de cálculo
- **4 notas** + **4 porcentajes** (suman **75%**)
- **Examen final** = **25%**
- Si promedio de presentación **≥ 5.0** → **exención** (no se rinde examen)
- **Excepto** si el curso es **Online** → el examen final **siempre es obligatorio**
- Escala: **1.0 – 7.0**

## Visuales
Diseñado con **taste-skill** (https://github.com/Saintkirk/taste-skill):
- Dark premium
- Acento magenta calibrado
- Tipografía limpia
- Motion sutil
- Alto contraste

## Build local (recomendado)

1. Abre el proyecto en **Android Studio** (Ladybug o superior)
2. Deja que sincronice Gradle (descarga el wrapper automáticamente si falta)
3. Build → Build Bundle(s) / APK(s) → Build APK(s)

O desde terminal (con Android SDK instalado):

```bash
./gradlew assembleDebug
# APK → app/build/outputs/apk/debug/app-debug.apk
```

## GitHub Actions (build automático)

Cada push a `main` dispara el workflow **Build APK**.  
El APK queda disponible en la pestaña **Actions** → último run → Artifacts → `app-debug`.

También puedes lanzarlo manualmente: Actions → Build APK → Run workflow.

## Estructura
```
MiPromedio/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/cl/mipromedio/app/
│       │   ├── MainActivity.kt
│       │   └── ui/
│       │       ├── MiPromedioApp.kt   ← lógica + UI completa
│       │       └── theme/
│       └── res/
├── build.gradle.kts
├── settings.gradle.kts
└── .github/workflows/build-apk.yml
```

**Confirmación:** Se utilizó la skill taste-skill para las visuales del proyecto.
