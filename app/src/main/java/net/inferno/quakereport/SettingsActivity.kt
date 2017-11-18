package net.inferno.quakereport

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.preference.SwitchPreference
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

class SettingsActivity : AppCompatActivity() {

    val fragment: GeneralPreferenceFragment get() = fragmentManager.findFragmentById(R.id.container) as GeneralPreferenceFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class GeneralPreferenceFragment : PreferenceFragment(), Preference.OnPreferenceChangeListener {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)

            val minMagnitude = findPreference("minmag")
            val orderBy = findPreference("orderby")
            val location = findPreference("location") as SwitchPreference

            bindPreferenceSummaryToValue(orderBy)
            bindPreferenceSummaryToValue(minMagnitude)

            location.setOnPreferenceClickListener {
                if (location.isChecked) {
                    ActivityCompat.requestPermissions(activity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
                } else {
                    val prefs = PreferenceManager.getDefaultSharedPreferences(activity).edit()

                    prefs.putString("latitude", "")
                    prefs.putString("longitude", "")
                    prefs.putString("maxradiuskm", "50")

                    prefs.apply()
                }
                LocationService.willUpdate = location.isChecked
                true
            }
        }

        override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
            val stringValue = newValue.toString()
            preference.summary = stringValue
            return true
        }

        private fun bindPreferenceSummaryToValue(preference: Preference) {
            preference.onPreferenceChangeListener = this
            val preferences = PreferenceManager.getDefaultSharedPreferences(preference.context)
            val preferenceString = preferences.getString(preference.key, "")
            onPreferenceChange(preference, preferenceString)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
            (fragment.findPreference("location") as SwitchPreference).isChecked = false
    }
}
