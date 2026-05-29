import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

/*
 * CORRIGIDO: A chave da API do Google Maps NÃO deve ficar hardcoded
 * no AndroidManifest.xml (fica exposta no repositório Git).
 *
 * Como configurar:
 * 1. Abra (ou crie) o arquivo local.properties na raiz do projeto.
 * 2. Adicione a linha:  MAPS_API_KEY=SuaChaveAqui
 * 3. Certifique-se de que local.properties está no .gitignore (já está por padrão).
 *
 * O AndroidManifest.xml usa ${MAPS_API_KEY} para referenciar essa variável.
 */
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

android {
    namespace  = "com.example.eventosibirama"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.eventosibirama"
        minSdk        = 24
        targetSdk     = 35
        versionCode   = 1
        versionName   = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Injeta a chave como placeholder do Manifest (nunca versionada)
        manifestPlaceholders["MAPS_API_KEY"] =
            localProperties.getProperty("MAPS_API_KEY", "")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // Necessário para BuildConfig (desabilitado por padrão no AGP moderno)
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // AndroidX
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.cardview:cardview:1.0.0")

    // Lifecycle (ViewModel + LiveData)
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.9.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.9.0")

    // Navigation
    implementation("androidx.navigation:navigation-fragment:2.9.0")
    implementation("androidx.navigation:navigation-ui:2.9.0")

    // Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-messaging")

    // Google Maps
    implementation("com.google.android.gms:play-services-maps:19.2.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // CircleImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // WorkManager
    implementation("androidx.work:work-runtime:2.10.1")

    // Testes
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
