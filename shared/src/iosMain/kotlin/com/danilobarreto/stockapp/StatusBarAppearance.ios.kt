package com.danilobarreto.stockapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleDefault
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.setStatusBarStyle

@Composable
actual fun SetStatusBarAppearance(useLightIcons: Boolean) {
    DisposableEffect(useLightIcons) {
        val style = if (useLightIcons) UIStatusBarStyleLightContent else UIStatusBarStyleDefault
        UIApplication.sharedApplication.setStatusBarStyle(style, animated = true)
        onDispose { }
    }
}