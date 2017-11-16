package net.inferno.quakereport

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.android.volley.NetworkError
import com.android.volley.ServerError
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.activity_quake.*
import net.inferno.quakereport.adapters.QuakeListAdapter
import net.inferno.quakereport.data.EarthQuake
import net.inferno.quakereport.data.QueryUtils

class QuakeActivity : AppCompatActivity() {

    val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quake)

        QueryUtils.init(this)
        loadData()

        earthquakeListView.setOnItemClickListener { parent, _, position, _ ->
            val quake = parent.adapter.getItem(position) as EarthQuake
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(quake.url))
            ActivityCompat.startActivity(this@QuakeActivity, intent, null)
        }
    }

    private fun loadData() {
        QueryUtils.Queue.add(JsonObjectRequest(QueryUtils.url, null,
                {
                    val earthquakes = mutableListOf<EarthQuake>()

                    val quakes = it.getJSONArray("features")
                    for (i in 0 until quakes.length()) {
                        val quake = quakes.getJSONObject(i).getJSONObject("properties")
                        val mag = quake.getDouble("mag")
                        val title = quake.getString("place").substringBefore("of ") + "of"
                        val place = quake.getString("place").substringAfter(" of ")
                        val date = quake.getLong("time")
                        val uri = quake.getString("url")
                        earthquakes += EarthQuake(mag, title, place, date, uri)
                    }

                    val adapter = QuakeListAdapter(this, earthquakes)
                    earthquakeListView.adapter = adapter
                },
                {
                    if (it is ServerError || it is NetworkError)
                        handler.postDelayed({ loadData() }, 1000)
                }))
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
    }
}
