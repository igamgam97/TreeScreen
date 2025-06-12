package org.example.plotapp.feature.hierarchyeditor

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import org.example.plotapp.feature.hierarchyeditor.presentation.HierarchyEditorRoute
import org.koin.compose.koinInject

const val HIERARCHY_EDITOR_ROUTE = "hierarchy_editor"

/**
 * Adds the hierarchy editor screen to the navigation graph.
 */
fun NavGraphBuilder.hierarchyEditorScreen() {
    composable(HIERARCHY_EDITOR_ROUTE) {
        HierarchyEditorRoute(
            viewModel = koinInject(),
        )
    }
}

/**
 * Navigation action to open the hierarchy editor screen.
 */
fun NavHostController.navigateToHierarchyEditor() {
    this.navigate(HIERARCHY_EDITOR_ROUTE)
}