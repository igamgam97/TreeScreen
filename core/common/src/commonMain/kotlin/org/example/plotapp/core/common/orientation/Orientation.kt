package org.example.plotapp.core.common.orientation

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable

sealed interface Orientation {
    data object Portrait : Orientation
    data object Landscape : Orientation
}

@Composable
expect fun currentWindowSizeClass(): WindowSizeClass

@Composable
fun getOrientation(): Orientation {
    val windowSizeClass = currentWindowSizeClass()
    return if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
        Orientation.Portrait
    } else {
        Orientation.Landscape
    }
}