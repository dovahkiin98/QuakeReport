package net.inferno.quakereport.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Providers
import androidx.compose.ui.platform.setContent
import net.inferno.quakereport.compose.AmbientActivityResultRegistry
import net.inferno.quakereport.theme.AppTheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Providers(AmbientActivityResultRegistry provides activityResultRegistry) {
                AppTheme {
                    MainUI()
                }
            }
        }
    }
}