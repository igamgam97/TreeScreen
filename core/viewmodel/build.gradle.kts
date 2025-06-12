plugins {
    id("org.example.plotapp.kotlinMultiplatform")
    id("org.example.plotapp.composeMultiplatform")
}

kotlin {

    sourceSets {
        commonMain.dependencies {

            // Lifecycle and ViewModel
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }

        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}