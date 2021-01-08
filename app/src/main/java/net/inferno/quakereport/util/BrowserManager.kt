package net.inferno.quakereport.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsService
import androidx.core.net.toUri

object BrowserManager {

    private var isCustomTabsSupported = false

    private val customTabsIntent = CustomTabsIntent.Builder()
        .setColorScheme(
            when (AppCompatDelegate.getDefaultNightMode()) {
                AppCompatDelegate.MODE_NIGHT_NO -> CustomTabsIntent.COLOR_SCHEME_LIGHT

                AppCompatDelegate.MODE_NIGHT_YES -> CustomTabsIntent.COLOR_SCHEME_DARK

                else -> CustomTabsIntent.COLOR_SCHEME_SYSTEM
            }
        )
        .setShowTitle(true)
        // the close button icon must be a 24dp png image
        // .setCloseButtonIcon(BitmapFactory.decodeResource(myApplication.resources, R.drawable.ic_arrow_back))
        .build()

    fun init(context: Context) {
        isCustomTabsSupported = isCustomTabsSupported(context)
    }

    private fun isCustomTabsSupported(context: Context): Boolean {
        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
        val resolveInfoList = packageManager.queryIntentActivities(intent, 0)

        for (resolveInfo in resolveInfoList) {
            Intent().apply {
                action = CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION
                setPackage(resolveInfo.activityInfo.packageName)
            }.also {
                if (packageManager.resolveService(it, 0) != null) return true
            }
        }

        return false
    }

    fun openUrl(context: Context, url: String) {
        url.toUri().also {
            if (isCustomTabsSupported) {
                customTabsIntent.launchUrl(context, it)
            } else {
                // no browser that supports CustomTabs exists on the device
                // open the url using the default browser
                context.startActivity(Intent(Intent.ACTION_VIEW, it))
            }
        }
    }
}