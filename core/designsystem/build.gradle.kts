plugins {
    id("org.example.plotapp.kotlinMultiplatform")
    id("org.example.plotapp.composeMultiplatform")
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:model"))

            // Compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}