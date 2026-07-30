plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint)
}

// ktlint 配置：CI 里跑的就是这里的规则，和本地 `./gradlew ktlintCheck` 完全一致。
// 👉 CI 原则：不要在 workflow 里写只有 CI 才有的检查逻辑。
//    所有检查都应该是「本地能跑的同一条命令」，否则开发者无法在提交前自检，
//    CI 就退化成一个只会说「你错了但你没法在本地复现」的黑盒。
// 注意：这里没有配 reporters。默认的 PLAIN reporter 是内置的，够用了。
// 一开始我加了 CHECKSTYLE reporter，结果它会额外拉 sarif4k → kotlinx-serialization-json，
// 本地网络握手 Maven Central 失败直接把构建打挂了。
// 👉 教训：**每加一个依赖就多一个失败点**。CI 里能少一个网络请求就少一个。
ktlint {
    android.set(true)
    ignoreFailures.set(false)
    filter {
        // 生成的代码不参与格式检查
        exclude { it.file.path.contains("/build/") }
    }
}

android {
    namespace = "com.example.firstapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.firstapplication"
        minSdk = 26
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
                "proguard-rules.pro",
            )
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
