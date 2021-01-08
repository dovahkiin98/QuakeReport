package net.inferno.quakereport

import android.app.Application
import net.inferno.quakereport.data.Repository
import net.inferno.quakereport.util.BrowserManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Repository.init(this)
        BrowserManager.init(this)
    }
}