package com.misbaha.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.misbaha.app.domain.model.AppLanguage
import com.misbaha.app.domain.model.AppSettings
import com.misbaha.app.domain.model.FontSizeOption
import com.misbaha.app.domain.model.ThemeMode
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

private const val SETTINGS_FILE_NAME = "settings"

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS_FILE_NAME)

class SettingsRepository(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val theme = stringPreferencesKey("theme")
        val fontSize = stringPreferencesKey("font_size")
        val language = stringPreferencesKey("language")
    }

    val settings: Flow<AppSettings> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            AppSettings(
                theme = preferences[Keys.theme]
                    ?.let { stored -> ThemeMode.entries.firstOrNull { it.name == stored } }
                    ?: ThemeMode.SYSTEM,
                fontSize = preferences[Keys.fontSize]
                    ?.let { stored -> FontSizeOption.entries.firstOrNull { it.name == stored } }
                    ?: FontSizeOption.DEFAULT,
                language = preferences[Keys.language]
                    ?.let { stored -> AppLanguage.entries.firstOrNull { it.name == stored } }
                    ?: AppLanguage.ARABIC
            )
        }
        .distinctUntilChanged()

    suspend fun setTheme(theme: ThemeMode) {
        dataStore.edit { preferences -> preferences[Keys.theme] = theme.name }
    }

    suspend fun setFontSize(fontSize: FontSizeOption) {
        dataStore.edit { preferences -> preferences[Keys.fontSize] = fontSize.name }
    }

    suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { preferences -> preferences[Keys.language] = language.name }
    }
}
