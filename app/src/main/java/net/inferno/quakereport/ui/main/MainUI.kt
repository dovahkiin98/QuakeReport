package net.inferno.quakereport.ui.main

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.KEY_ROUTE
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
            startDestination = Route.QuakesList.id,
        ) {
            Route::class.sealedSubclasses.forEach { route ->
                composable(route.objectInstance!!.id) { Contents(it) }
            }
//            composable(QUAKES_LABEL) {
//                QuakesList()
//            }
//            composable(SETTINGS_LABEL) {
//                Settings()
//            }
        }
    }
}

const val QUAKES_LABEL = "quakes"
const val SETTINGS_LABEL = "settings"

sealed class Route(val id: String) {
    object QuakesList : Route(QUAKES_LABEL)
    object Settings : Route(SETTINGS_LABEL)
}

@Composable
fun Contents(route: NavBackStackEntry) {
    Crossfade(current = route) {
        when (it.arguments?.getString(KEY_ROUTE)) {
            Route.QuakesList.id -> QuakesList()
            Route.Settings.id -> Settings()
            else -> Text("UNKNOWN PAGE")
        }
    }
}