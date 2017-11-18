package net.inferno.quakereport.data

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.preference.PreferenceManager
import android.text.format.DateFormat
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import java.util.*

object QueryUtils {

    private val startDate get() = DateFormat.format("yyyy-MM-dd", Calendar.getInstance())
    var url = ""
    private val uri = "https://earthquake.usgs.gov/fdsnws/event/1/query"
    lateinit var Queue: RequestQueue

    fun init(context: Context) {
        Queue = Volley.newRequestQueue(context)
        params(PreferenceManager.getDefaultSharedPreferences(context))
    }

    private fun params(prefs: SharedPreferences) {
        val uri = Uri.parse(uri).buildUpon()

        val orderby = prefs.getString("orderby", "time")
        val minmag = prefs.getString("minmag", "0")
        val latitude = prefs.getString("latitude", "")
        val longitude = prefs.getString("longitude", "")

        uri.appendQueryParameter("format", "geojson")
        uri.appendQueryParameter("minmag", minmag)
        uri.appendQueryParameter("orderby", orderby)
        uri.appendQueryParameter("starttime", DateFormat.format("yyyy-MM-dd", Calendar.getInstance()).toString())

        if (longitude != "" || latitude != "") {
            uri.appendQueryParameter("latitude", latitude)
            uri.appendQueryParameter("longitude", longitude)
            uri.appendQueryParameter("maxradiuskm", "50")
        }

        url = uri.toString()
    }
}