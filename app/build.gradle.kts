plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)   // Genera el grafo de dependencias de Dagger a partir de las anotaciones de Hilt
    alias(libs.plugins.ksp)    // Procesa anotaciones en compilación (Hilt y Room generan código con esto)
}

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

        // URL de la API en BuildConfig: se cambia en un solo lugar si usamos el servidor local
        // (en el emulador sería http://10.0.2.2:8000/).
        buildConfigField("String", "API_BASE_URL", "\"https://tp3.martinrivas.ar/\"")
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
