package com.misbaha.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.misbaha.app.di.AppContainer
import com.misbaha.app.domain.model.AppLanguage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MisbahaApplication : Application() {
    lateinit var container: AppContainer
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        if (AppCompatDelegate.getApplicationLocales().isEmpty) {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(AppLanguage.ARABIC.tag)
            )
        }
        container = AppContainer(this)
        applicationScope.launch {
            val language = container.settingsRepository.settings.first().language
            withContext(Dispatchers.Main) {
                val currentLanguage = AppCompatDelegate.getApplicationLocales().get(0)?.language
                if (currentLanguage != language.tag) {
                    AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags(language.tag)
                    )
                }
            }
        }
    }
}
