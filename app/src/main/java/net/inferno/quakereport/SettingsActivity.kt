package net.inferno.quakereport

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.*
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

    class GeneralPreferenceFragment : PreferenceFragment() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)

            val minMagnitude = findPreference(getString(R.string.minMag_key)) as EditTextPreference
            val orderBy = findPreference(getString(R.string.sortBy_key)) as ListPreference
            val location = findPreference(getString(R.string.location_key)) as SwitchPreference

            minMagnitude.setOnPreferenceChangeListener { _, newValue -> minMagnitude.summary = newValue.toString(); true }
            orderBy.setOnPreferenceChangeListener { listPreference, _ ->
                orderBy.summary = (listPreference as ListPreference).entry
                true
            }

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
