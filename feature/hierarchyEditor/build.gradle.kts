plugins {
    id("org.example.plotapp.kotlinMultiplatform")
    id("org.example.plotapp.composeMultiplatform")
    alias(libs.plugins.mokkery)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Core modules
            implementation(project(":core:designsystem"))
            implementation(project(":core:domain"))
            implementation(project(":core:data"))
            implementation(project(":core:model"))
            implementation(project(":core:viewmodel"))
            implementation(project(":core:common"))
            // Compose
            implementation(libs.navigation.compose)
            implementation(compose.components.resources)
            // Koin
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            // Collections
            implementation(libs.kotlinx.collections.immutable)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.turbine)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}