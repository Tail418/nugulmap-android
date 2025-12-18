import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    namespace = "com.example.neogulmap"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.neogulmap"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        val baseUrl = (localProperties.getProperty("BASE_URL") ?: "http://10.0.2.2:8080").replace("\"", "")
        val kakaoKey = (localProperties.getProperty("KAKAO_NATIVE_APP_KEY") ?: "").replace("\"", "")
        
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"$kakaoKey\"")
        
        // Social Login Configs (from DevB)
        val googleWebClientId = (localProperties.getProperty("GOOGLE_WEB_CLIENT_ID") ?: "").replace("\"", "")
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$googleWebClientId\"")

        val naverClientId = (localProperties.getProperty("NAVER_CLIENT_ID") ?: "").replace("\"", "")
        val naverClientSecret = (localProperties.getProperty("NAVER_CLIENT_SECRET") ?: "").replace("\"", "")
        val naverClientName = (localProperties.getProperty("NAVER_CLIENT_NAME") ?: "").replace("\"", "")
        buildConfigField("String", "NAVER_CLIENT_ID", "\"$naverClientId\"")
        buildConfigField("String", "NAVER_CLIENT_SECRET", "\"$naverClientSecret\"")
        buildConfigField("String", "NAVER_CLIENT_NAME", "\"$naverClientName\"")
        
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = kakaoKey
        manifestPlaceholders["NAVER_CLIENT_ID"] = naverClientId
        manifestPlaceholders["NAVER_CLIENT_SECRET"] = naverClientSecret
        manifestPlaceholders["NAVER_CLIENT_NAME"] = naverClientName
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended) // From DevB
    
    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // Retrofit & OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // Coil
    implementation(libs.coil.compose)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Kakao Map & Login
    implementation(libs.kakao.map)
    implementation(libs.kakao.sdk.user)

    // Google Credential Manager & Location
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation(libs.play.services.location)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}