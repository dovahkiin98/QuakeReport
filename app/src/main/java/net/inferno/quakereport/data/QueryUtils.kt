package net.inferno.quakereport.data

import android.content.Context
import android.text.format.DateFormat
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import java.util.*

object QueryUtils {

    private val startDate get() = DateFormat.format("yyyy-MM-dd", Calendar.getInstance())
    val url get() = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&minmag=2&orderby=time&starttime=$startDate"
    lateinit var Queue: RequestQueue

    fun init(context: Context) {
        Queue = Volley.newRequestQueue(context)
    }
}