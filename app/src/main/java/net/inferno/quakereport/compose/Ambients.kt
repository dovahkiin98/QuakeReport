package net.inferno.quakereport.compose

import androidx.activity.result.ActivityResultRegistry
import androidx.compose.runtime.staticAmbientOf
import androidx.navigation.NavController

val AmbientNavController = staticAmbientOf<NavController>()

val AmbientActivityResultRegistry = staticAmbientOf<ActivityResultRegistry>()