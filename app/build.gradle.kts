android {
    namespace = "cl.mipromedio.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "cl.mipromedio.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "1.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            // Lee las variables de entorno seguras que inyecta GitHub Actions
            storeFile = file("mipromedio-release.jks")
            storePassword = System.getenv("KEY_STORE_PASSWORD") ?: "mipromedio123"
            keyAlias = System.getenv("ALIAS") ?: "mipromedio"
            keyPassword = System.getenv("KEY_PASSWORD") ?: "mipromedio123"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isDebuggable = false
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isDebuggable = true
        }
    }

    // Código para cambiar automáticamente el nombre del APK a MiPromedio
    applicationVariants.all {
        val variant = this
        variant.outputs.map { it as com.android.build.gradle.internal.api.ApkVariantOutputImpl }.forEach { output ->
            val outputFileName = "MiPromedio-${variant.name}-${variant.versionName}.apk"
            output.outputFileName = outputFileName
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}
