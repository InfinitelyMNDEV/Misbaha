package com.misbaha.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misbaha.app.data.preferences.LocaleController
import com.misbaha.app.data.preferences.SettingsRepository
import com.misbaha.app.data.repository.ZikrRepository
import com.misbaha.app.domain.model.AppLanguage
import com.misbaha.app.domain.model.AppSettings
import com.misbaha.app.domain.model.FontSizeOption
import com.misbaha.app.domain.model.ThemeMode
import com.misbaha.app.domain.model.Zikr
import com.misbaha.app.domain.model.ZikrType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val customZikrs: List<Zikr> = emptyList(),
    val allTexts: List<String> = emptyList()
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val zikrRepository: ZikrRepository,
    private val localeController: LocaleController
) : ViewModel() {
    val settings: StateFlow<AppSettings> = settingsRepository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AppSettings()
    )

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.settings,
        zikrRepository.observeAll()
    ) { settings, zikrs ->
        SettingsUiState(
            settings = settings,
            customZikrs = zikrs.filter { it.type == ZikrType.CUSTOM },
            allTexts = zikrs.map { it.text }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = SettingsUiState()
    )

    fun setTheme(theme: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setTheme(theme)
        }
    }

    fun setFontSize(fontSize: FontSizeOption) {
        viewModelScope.launch {
            settingsRepository.setFontSize(fontSize)
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)
            localeController.apply(language)
        }
    }

    fun addCustom(text: String) {
        viewModelScope.launch {
            zikrRepository.addCustom(text)
        }
    }

    fun updateCustom(id: Long, text: String) {
        viewModelScope.launch {
            zikrRepository.updateCustom(id, text)
        }
    }

    fun deleteCustom(id: Long) {
        viewModelScope.launch {
            zikrRepository.deleteCustom(id)
        }
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
