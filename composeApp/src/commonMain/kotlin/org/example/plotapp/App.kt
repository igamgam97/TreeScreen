package org.example.plotapp

import androidx.compose.runtime.Composable
import org.example.plotapp.navigation.AppNavigation
import org.example.plotapp.theme.PlotAppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinMultiplatformApplication
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.koinConfiguration

@OptIn(KoinExperimentalAPI::class)
@Composable
@Preview
fun App() {

    KoinMultiplatformApplication(
        config = koinConfiguration {
            modules(appModule)
        },
    ) {

        PlotAppTheme {
            AppNavigation()
        }
    }
}