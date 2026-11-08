package com.misbaha.app

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.misbaha.app.ui.navigation.MisbahaNavHost
import com.misbaha.app.ui.settings.SettingsViewModel
import com.misbaha.app.ui.theme.MisbahaTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
            MisbahaTheme(
                themeMode = settings.theme,
                fontSize = settings.fontSize
            ) {
                MisbahaNavHost(settingsViewModel = settingsViewModel)
            }
        }
    }
}
