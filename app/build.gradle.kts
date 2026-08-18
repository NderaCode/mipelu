import org.gradle.kotlin.dsl.dependencies
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
}

// Release signing credentials live outside the repo (see keystore.properties, gitignored).
// Falls back to null signing when the file is absent so debug builds / CI checkouts without
// the secret still compile - only `assembleRelease`/`bundleRelease` actually need it.
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties =
    Properties().apply {
        if (keystorePropertiesFile.exists()) {
            keystorePropertiesFile.inputStream().use { load(it) }
        }
    }

android {
    namespace = "com.cocido.mipelu"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.cocido.mipelu"
        minSdk = 29
        targetSdk = 36
        versionCode = 3
        versionName = "1.1"

        testInstrumentationRunner = "com.cocido.mipelu.HiltTestRunner"
    }

    signingConfigs {
        if (keystorePropertiesFile.exists()) {
            create("release") {
                storeFile = rootProject.file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            }
        }
    }

    buildTypes {
        debug {
            // Points at the same production API as release so debug builds never need a local
            // backend running. Trailing slash mandatory - see the comment on the release block.
            buildConfigField("String", "API_BASE_URL", "\"https://api.shacode.com.ar/\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            if (keystorePropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
            // Trailing slash is mandatory: Retrofit's baseUrl() throws IllegalArgumentException
            // at startup without it, since every @GET/@POST path in MiPeluApi is relative
            // ("auth/login", not "/auth/login"). https://api.shacode.com.ar with no slash would
            // crash the app on the very first network call.
            buildConfigField("String", "API_BASE_URL", "\"https://api.shacode.com.ar/\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        // MockK's androidTest artifact pulls in junit-jupiter transitively (unused directly -
        // this project's instrumented tests are JUnit4/Hilt), and several of its jars ship the
        // same META-INF license files, which fails the androidTest APK's resource merge.
        resources {
            excludes +=
                setOf(
                    "META-INF/LICENSE.md",
                    "META-INF/LICENSE-notice.md",
                    "META-INF/LICENSE",
                    "META-INF/LICENSE.txt",
                    "META-INF/NOTICE",
                    "META-INF/NOTICE.txt",
                )
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coil.compose)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    // implementation (not debugImplementation): NetworkModule references HttpLoggingInterceptor
    // directly for both build types, just at Level.NONE in release via BuildConfig.DEBUG.
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.androidx.security.crypto)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.turbine)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    kaptAndroidTest(libs.hilt.compiler)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

detekt {
    // buildUponDefaultConfig: detekt's own defaults are reasonable for a Compose/Hilt codebase;
    // detekt.yml only overrides the one rule (FunctionNaming) that misfires on @Composable's
    // PascalCase convention, everything else stays at detekt's default.
    buildUponDefaultConfig = true
    config.setFrom(file("detekt.yml"))
    // Retrofitted onto an existing codebase - the baseline grandfathers in every issue that
    // already existed so CI gates on new issues only, not a backlog nobody signed up to fix.
    baseline = file("detekt-baseline.xml")
    source.setFrom("src/main/java", "src/test/java", "src/androidTest/java")
}

ktlint {
    version.set("1.3.1")
    // Same reasoning as detekt's baseline: retrofitting ktlint onto an existing codebase without
    // one would mean either reformatting hundreds of already-working files in one unreviewed pass
    // (tried it, the diff touched 100+ files with no behavior change - reverted) or starting CI
    // permanently red. The baseline grandfathers today's style as-is; only new code is held to it.
    baseline.set(file("ktlint-baseline.xml"))
}
