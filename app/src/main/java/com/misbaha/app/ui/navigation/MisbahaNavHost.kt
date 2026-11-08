package com.misbaha.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.misbaha.app.AppViewModelProvider
import com.misbaha.app.ui.counter.CounterScreen
import com.misbaha.app.ui.counter.CounterViewModel
import com.misbaha.app.ui.home.HomeScreen
import com.misbaha.app.ui.home.HomeViewModel
import com.misbaha.app.ui.settings.SettingsScreen
import com.misbaha.app.ui.settings.SettingsViewModel

private object Routes {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val COUNTER = "counter/{zikrId}"

    fun counter(zikrId: Long): String = "counter/$zikrId"
}

@Composable
fun MisbahaNavHost(settingsViewModel: SettingsViewModel) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            val homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
            HomeScreen(
                viewModel = homeViewModel,
                onOpenCounter = { zikrId ->
                    navController.navigate(Routes.counter(zikrId))
                },
                onOpenSettings = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }
        composable(
            route = Routes.COUNTER,
            arguments = listOf(
                navArgument(CounterViewModel.ZIKR_ID_ARGUMENT) {
                    type = NavType.LongType
                }
            )
        ) {
            val counterViewModel: CounterViewModel =
                viewModel(factory = AppViewModelProvider.Factory)
            CounterScreen(
                viewModel = counterViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
