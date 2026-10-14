plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.zoryakai.app"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.zoryakai.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 9
        versionName = "9.0"
    }
}
