plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.elorclass"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.elorclass"
        minSdk = 29
        //noinspection EditedTargetSdkVersion
        targetSdk = 35
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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8

    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    packaging {
        resources {
            excludes.addAll(
                mutableSetOf(
                    "META-INF/NOTICE.md",
                    "META-INF/LICENSE.md"
                )
            )
        }
    }

}

dependencies {
    val room_version = "2.6.1"

    kapt(libs.androidx.room.compiler.v250)

    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.rxjava2)
    implementation(libs.androidx.room.rxjava3)
    implementation(libs.androidx.room.guava)
    testImplementation(libs.androidx.room.testing)
    implementation(libs.androidx.room.paging)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // MAIL
    implementation(libs.jakarta.mail)

    //GIF
    implementation(libs.glide)
    annotationProcessor(libs.glideCompiler)

    // SOCKET.IO
    implementation(libs.socket.io.client)
    implementation(libs.engine.io.client)

    // GSON
    implementation(libs.gson)
}