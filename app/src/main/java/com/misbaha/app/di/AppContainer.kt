package com.misbaha.app.di

import android.content.Context
import com.misbaha.app.data.local.MisbahaDatabase
import com.misbaha.app.data.preferences.AndroidLocaleController
import com.misbaha.app.data.preferences.LocaleController
import com.misbaha.app.data.preferences.SettingsRepository
import com.misbaha.app.data.preferences.settingsDataStore
import com.misbaha.app.data.repository.ZikrRepository
import com.misbaha.app.domain.time.AppClock
import com.misbaha.app.domain.time.SystemAppClock

class AppContainer(
    context: Context,
    val clock: AppClock = SystemAppClock()
) {
    private val applicationContext = context.applicationContext
    private val database = MisbahaDatabase.create(applicationContext, clock.today())

    val zikrRepository = ZikrRepository(database.zikrDao(), database, clock)
    val settingsRepository = SettingsRepository(applicationContext.settingsDataStore)
    val localeController: LocaleController = AndroidLocaleController()
}
