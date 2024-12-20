plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.yunnext.bluetooth.sample"
    compileSdk = 34

    val versionCodeCommon = 1
    defaultConfig {
        applicationId = "com.yunnext.bluetooth.sample"
        minSdk = 28
        targetSdk = 31
        versionCode = versionCodeCommon
        versionName = "1.0.$versionCodeCommon"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    flavorDimensions("server")

    productFlavors {

        create("demo1") {
            dimension = "server"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }

        create("demo2") {
            dimension = "server"
            applicationIdSuffix = ""
            versionNameSuffix = ""
        }
    }

    buildFeatures.buildConfig = true

    lintOptions {
        disable("GoogleAppIndexingWarning")
        baseline(file("lint-baseline.xml"))// your choice of filename/path here
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.bundles.mlkit)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.navigation.compose)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.bundles.zeeeeej)
    implementation(libs.bundles.kotlinx)
    //implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.http)
    implementation(libs.fastble)
    implementation(libs.androidx.bluetooth)
    implementation(libs.zeeeeej.yunext.common.android)
    implementation(libs.zeeeeej.yunext.compose.android)


}