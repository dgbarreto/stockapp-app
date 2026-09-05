package com.danilobarreto.stockapp

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
actual fun SetStatusBarAppearance(useLightIcons: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return

    LaunchedEffect(useLightIcons) {
        val activity = view.context as? Activity ?: return@LaunchedEffect
        val window = activity.window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useLightIcons
    }
}