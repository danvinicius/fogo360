import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "exemplo.fogo360"
    compileSdk = 34

    defaultConfig {
        applicationId = "exemplo.fogo360"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("debug") {
            val mqttUsername: String = gradleLocalProperties(rootDir).getProperty("MQTT_USERNAME", "")
            val mqttPassword: String = gradleLocalProperties(rootDir).getProperty("MQTT_PASSWORD", "")
            val brokenUrl: String = gradleLocalProperties(rootDir).getProperty("BROKEN_URL", "")
            val googleMapsApiKey: String = gradleLocalProperties(rootDir).getProperty("GOOGLE_MAPS_API_KEY", "")

            buildConfigField("String", "MQTT_USERNAME", "\"$mqttUsername\"")
            buildConfigField("String", "MQTT_PASSWORD", "\"$mqttPassword\"")
            buildConfigField("String", "BROKEN_URL", "\"$brokenUrl\"")
            buildConfigField("String", "GOOGLE_MAPS_API_KEY", "\"$googleMapsApiKey\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")
    implementation ("androidx.activity:activity-ktx:1.8.1")
    implementation ("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1")
    implementation ("androidx.localbroadcastmanager:localbroadcastmanager:1.1.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("androidx.core:core-ktx:1.12.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation ("com.google.android.material:material:1.10.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-nearby:19.0.0")
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
}