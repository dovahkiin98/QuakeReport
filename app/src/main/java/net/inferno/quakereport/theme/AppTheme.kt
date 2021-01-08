package net.inferno.quakereport.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import net.inferno.quakereport.R

@Composable
fun AppTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = colors(isDarkTheme),
    ) {
        content()
    }
}

@Composable
fun colors(isDarkTheme: Boolean = isSystemInDarkTheme()) = if (isSystemInDarkTheme() || isDarkTheme) darkColors(
    primary = colorResource(R.color.colorPrimary),
    primaryVariant = colorResource(R.color.colorPrimaryDark),
    secondary = colorResource(R.color.colorAccent),
    onPrimary = Color.White,
) else lightColors(
    primary = colorResource(R.color.colorPrimary),
    primaryVariant = colorResource(R.color.colorPrimaryDark),
    secondary = colorResource(R.color.colorAccent),
    onPrimary = Color.White,
)