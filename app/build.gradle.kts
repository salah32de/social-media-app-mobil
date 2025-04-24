plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.socialmedia"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.socialmedia"
        minSdk = 29
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    packagingOptions {
        exclude ("META-INF/NOTICE.md")
        exclude ("META-INF/LICENSE.md")
        exclude ("META-INF/DEPENDENCIES")
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.legacy.support.v4)
    implementation(libs.recyclerview)
    implementation(libs.lifecycle.process)
    testImplementation(libs.junit)
    testImplementation(libs.monitor)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.google.firebase:firebase-database:20.2.2")
    implementation ("com.google.firebase:firebase-core:21.1.1")
    // Firebase Authentication
    implementation ("com.google.firebase:firebase-auth:21.0.1")

    // Google Sign-In
    implementation ("com.google.android.gms:play-services-auth:20.0.1")
    // Import the BoM for the Firebase platform
    implementation ("com.google.firebase:firebase-bom:33.8.0")

    implementation ("org.eclipse.angus:angus-mail:2.0.2")
    implementation ("org.eclipse.angus:angus-activation:2.0.2")


    //Glide
        implementation ("com.github.bumptech.glide:glide:4.16.0")
        annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    //firebase storage
    implementation ("com.google.firebase:firebase-storage:20.3.0")

    //Gson
    implementation ("com.google.code.gson:gson:2.8.8")

    //exoPlayer video
    implementation ("com.google.android.exoplayer:exoplayer:2.19.1")

    //room database
    implementation ("androidx.room:room-runtime:2.6.1")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")


}