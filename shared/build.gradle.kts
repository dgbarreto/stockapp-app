import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
            freeCompilerArgs += listOf("-Xbinary=bundleId=com.danilobarreto.stockapp.shared")
        }
    }
    
    androidLibrary {
       namespace = "com.danilobarreto.stockapp.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.androidx.core.ktx)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.navigation.compose)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.serialization.kotlinxJson)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.auth)

            implementation("com.danilobarreto.stockapp:auth:0.3.0")
            implementation("com.danilobarreto.stockapp:quotes:0.2.9")
            implementation("com.danilobarreto.stockapp:designsystem:0.3.0")
            implementation("com.danilobarreto.stockapp:portfolio:0.2.0")
            implementation("com.danilobarreto.stockapp:orders:0.1.1")
            implementation("com.danilobarreto.stockapp:imports:0.1.3")
            implementation("com.danilobarreto.stockapp:valuation:0.1.2")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

tasks.matching {
    it.name == "linkDebugTestIosSimulatorArm64" || it.name == "iosSimulatorArm64Test"
}.configureEach {
    enabled = false
}

val fullIosMatrix = project.hasProperty("fullIosMatrix")

tasks.matching { it.name == "linkDebugFrameworkIosArm64" }.configureEach {
    enabled = fullIosMatrix
}
tasks.matching { it.name == "linkReleaseFrameworkIosSimulatorArm64" }.configureEach {
    enabled = fullIosMatrix
}