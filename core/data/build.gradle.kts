plugins {
    id("org.example.plotapp.kotlinMultiplatform")
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:model"))
            implementation(project(":core:network"))
            implementation(project(":core:domain"))
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}