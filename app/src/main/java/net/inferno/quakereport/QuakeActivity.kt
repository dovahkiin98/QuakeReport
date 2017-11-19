package net.inferno.quakereport

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.android.volley.NetworkError
import com.android.volley.ServerError
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_quake.*
import net.inferno.quakereport.adapters.QuakeListAdapter
import net.inferno.quakereport.data.EarthQuake
import net.inferno.quakereport.data.QueryUtils

class QuakeActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quake)

        QueryUtils.init(this)
        earthquakeListView.emptyView = emptyList

        earthquakeListView.setOnItemClickListener { parent, _, position, _ ->
            val quake = parent.adapter.getItem(position) as EarthQuake
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(quake.url))
            ActivityCompat.startActivity(this@QuakeActivity, intent, null)
        }

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        QueryUtils.params(sharedPreferences)
        if (key == getString(R.string.location_key) && sharedPreferences.getBoolean(key, false)) {
            val intent = Intent(this, LocationService::class.java)
            startService(intent)
        } else loadData()
    }

    private fun loadData() {
        QueryUtils.Queue.add(JsonObjectRequest(QueryUtils.url, null,
                {
                    val earthquakes = mutableListOf<EarthQuake>()

                    val quakes = it.getJSONArray("features")
                    for (i in 0 until quakes.length()) {
                        val quake = quakes.getJSONObject(i).getJSONObject("properties")
                        earthquakes += Gson().fromJson(quake.toString(), EarthQuake::class.java)
                    }

                    val adapter = QuakeListAdapter(this, earthquakes)
                    earthquakeListView.adapter = adapter
                    earthquakeListView.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                },
                {
                    if (it is ServerError || it is NetworkError)
                        handler.postDelayed({ loadData() }, 1000)
                }))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            startActivity(Intent(this, SettingsActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
    }
}
