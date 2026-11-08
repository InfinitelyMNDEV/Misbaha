package com.misbaha.app

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.misbaha.app.ui.counter.CounterViewModel
import com.misbaha.app.ui.home.HomeViewModel
import com.misbaha.app.ui.settings.SettingsViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val application = misbahaApplication()
            HomeViewModel(
                repository = application.container.zikrRepository,
                clock = application.container.clock
            )
        }
        initializer {
            val application = misbahaApplication()
            CounterViewModel(
                savedStateHandle = createSavedStateHandle(),
                repository = application.container.zikrRepository,
                clock = application.container.clock
            )
        }
        initializer {
            val application = misbahaApplication()
            SettingsViewModel(
                settingsRepository = application.container.settingsRepository,
                zikrRepository = application.container.zikrRepository,
                localeController = application.container.localeController
            )
        }
    }
}

private fun CreationExtras.misbahaApplication(): MisbahaApplication =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MisbahaApplication
