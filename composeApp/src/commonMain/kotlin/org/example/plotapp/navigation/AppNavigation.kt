package org.example.plotapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import org.example.plotapp.feature.hierarchyeditor.HIERARCHY_EDITOR_ROUTE
import org.example.plotapp.feature.hierarchyeditor.hierarchyEditorScreen

/**
 * Main navigation component for the app.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = HIERARCHY_EDITOR_ROUTE) {

        hierarchyEditorScreen()
    }
}