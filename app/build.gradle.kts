plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "org.androidtown.termproject"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.androidtown.termproject"
        minSdk = 24
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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database:20.1.0") // 추가된 의존성
    implementation ("com.firebaseui:firebase-ui-auth:7.2.0")
    implementation("com.google.android.gms:play-services-auth:20.3.0") // 추가된 종속성
    implementation ("com.google.firebase:firebase-storage:20.0.0") // 동영상 업로드 용
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("com.google.android.exoplayer:exoplayer:2.14.2")
}