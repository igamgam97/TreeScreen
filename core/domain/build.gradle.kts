plugins {
    id("org.example.plotapp.kotlinMultiplatform")
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:model"))
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}