plugins {
    id("org.example.plotapp.kotlinMultiplatform")
    id("org.example.plotapp.composeMultiplatform")
    alias(libs.plugins.buildConfig)
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:model"))
            implementation(project(":core:common"))

            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.android)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }

    buildConfig {
        useKotlinOutput { internalVisibility = true }
        packageName = "org.example.plotapp.core.network"
        buildConfigField(
            "Boolean",
            "mockModeEnabled",
            propOrDef<Boolean>("mockModeEnabled"),
        )
    }
}

inline fun <reified T : Any> Project.propOrDef(propertyName: String): T {
    val rawValue = properties[propertyName] ?: error("Property $propertyName not found")

    return when (T::class) {
        String::class -> rawValue.toString() as T
        Int::class -> rawValue.toString().toIntOrNull() as? T
            ?: error("Property $propertyName is not a valid Int")

        Long::class -> rawValue.toString().toLongOrNull() as? T
            ?: error("Property $propertyName is not a valid Long")

        Boolean::class -> rawValue.toString().toBooleanStrictOrNull() as? T
            ?: error("Property $propertyName is not a valid Boolean")

        Double::class -> rawValue.toString().toDoubleOrNull() as? T
            ?: error("Property $propertyName is not a valid Double")

        Float::class -> rawValue.toString().toFloatOrNull() as? T
            ?: error("Property $propertyName is not a valid Float")

        else -> error("Unsupported type ${T::class.simpleName} for property $propertyName")
    }
}