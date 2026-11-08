import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)   // Genera el grafo de dependencias de Dagger a partir de las anotaciones de Hilt
    alias(libs.plugins.ksp)    // Procesa anotaciones en compilación (Hilt y Room generan código con esto)
}

// local.properties es personal de cada compu y no se sube a git, así que cada integrante
// puede elegir a qué servidor se conecta la app sin cambiar código ni pisar a los demás.
// Para usar el mock local desde el emulador, agregar ahí: vora.apiUrl=http://10.0.2.2:8000/
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}
val apiBaseUrl: String = localProperties.getProperty("vora.apiUrl") ?: "https://tp3.martinrivas.ar/"

android {
    namespace = "com.example.voracoach"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.voracoach"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // La URL queda en BuildConfig.API_BASE_URL, que es lo que va a leer Retrofit
        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true   // Necesario para usar BuildConfig.API_BASE_URL
    }
}

// Room exporta el esquema de la base para poder revisar cambios entre versiones
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // AndroidX base y ciclo de vida
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

  // Navegación entre pantallas
    implementation(libs.androidx.navigation.compose)

    // Hilt: evita crear a mano repositorios, Retrofit y DAOs; los recibe cada clase por constructor
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)   // hiltViewModel() dentro de los Composables

    // Retrofit: catálogo y detalle de entrenamientos
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

    // Room: caché de entrenamientos y registro de comidas
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.kotlinx.coroutines.android)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
