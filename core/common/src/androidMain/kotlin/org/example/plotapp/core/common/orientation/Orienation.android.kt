package org.example.plotapp.core.common.orientation

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
actual fun currentWindowSizeClass(): WindowSizeClass {
    val activity = LocalActivity.current ?: error("No activity found")
    return calculateWindowSizeClass(activity)
}