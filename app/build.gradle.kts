plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint)
}

// ktlint 规则配置。本地 `./gradlew ktlintCheck` 和 CI 跑的是同一套规则、同一条命令。
// 不用 reporters 配置——默认 PLAIN reporter 内置，不额外拉依赖。
ktlint {
    // 采用 Android 风格约定（如缩进 4 空格）
    android.set(true)
    // 有违规就让构建失败。设成 true 等于装了个不干活的门禁
    ignoreFailures.set(false)
    filter {
        // 生成的代码不参与检查
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
    lint {
        // ⚠️ 默认行为是「假门禁」的来源：只有 error 级别才拦构建，warning 全部放行。
        // 实测：埋入 NewApi 靶子前 `./gradlew lintDebug` 报 0 errors / 30 warnings，
        // 退出码 0，CI 全绿——报告里躺着 30 条问题，没有一条拦得住人。
        abortOnError = true

        // 把警告也提升为错误。实测代价：32 errors，其中 17 条是 GradleDependency。
        // 那类警告**不随代码变化**，上游发个新版本你的 CI 就自己红了。
        // 门禁一旦开始误报，团队很快就学会无脑重跑——那时它比没有门禁更糟。
        // 所以配合下面的降级白名单一起用。
        warningsAsErrors = true

        // 降级为 informational（只记录、不拦构建）的两类：
        //
        // 1. 「时间炸弹」类——不改代码也会自己变红，属于依赖治理而非代码质量，
        //    应该交给 Renovate/Dependabot 定期处理，不该进 CI 门禁。
        // 2. 已知存量——UnusedResources / RedundantLabel 来自 Android Studio 模板，
        //    确实该清，但清理不在 Day 2 范围内。
        //    这是**明示的豁免，不是遗漏**：写在这里、有理由、可以被 review 和撤销。
        //    对比「偷偷 ignoreFailures = true」——同样是放过，区别在于有没有留下档案。
        //    TODO: 清理模板遗留资源后，把 UnusedResources / RedundantLabel 移出本列表。
        informational +=
            listOf(
                "GradleDependency",
                "AndroidGradlePluginVersion",
                "OldTargetApi",
                "UnusedResources",
                "RedundantLabel",
            )
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
