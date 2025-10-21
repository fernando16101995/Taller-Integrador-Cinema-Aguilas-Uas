pluginManagement {
    repositories {
        google {            content {
            includeGroupByRegex("com\\.android.*")
            includeGroupByRegex("com\\.google.*")
            includeGroupByRegex("androidx.*")
        }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    // Mueve el bloque 'plugins' aquí adentro
    plugins {
        // Es una buena práctica añadir el plugin de la aplicación y gestionarlo aquí
        id("com.android.application") version "8.4.1" apply false

        // Añade 'apply false' a cada línea para que Gradle no los aplique aquí
        id("org.jetbrains.kotlin.android") version "2.0.0" apply false
        id("org.jetbrains.kotlin.jvm") version "2.0.0" apply false
        id("org.jetbrains.kotlin.kapt") version "2.0.0" apply false
        id("org.jetbrains.kotlin.plugin.compose") version "1.6.10" apply false // Usa la versión del compilador de Compose compatible con Kotlin 2.0.0
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Taller Integrador"
include(":app")
