package net.inferno.quakereport.ui.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.inferno.quakereport.compose.AmbientNavController
import net.inferno.quakereport.ui.quakes.QuakesList
import net.inferno.quakereport.ui.settings.Settings

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainUI() {
    val navController = rememberNavController()

    Providers(AmbientNavController provides navController) {
        NavHost(
            navController,
            startDestination = QUAKES_LABEL,
        ) {
            composable(QUAKES_LABEL) {
                QuakesList()
            }
            composable(SETTINGS_LABEL) {
                Settings()
            }
        }
    }
}

const val QUAKES_LABEL = "quakes"
const val SETTINGS_LABEL = "settings"