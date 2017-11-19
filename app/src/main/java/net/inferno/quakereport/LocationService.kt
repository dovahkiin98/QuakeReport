package net.inferno.quakereport

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat

class LocationService : Service(), LocationListener {

    private lateinit var locationManager: LocationManager

    companion object {
        var willUpdate = false
        val LOCATION = "com.inferno.quakeReport.location"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        getLocation()
        return Service.START_NOT_STICKY
    }

    private fun getLocation() {
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        when {
            isGPSEnabled -> {
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null)
            }
            isNetworkEnabled -> {
                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null)
            }
            else -> return
        }
    }

    override fun onLocationChanged(location: Location) {
        if (willUpdate) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this).edit()

            prefs.putString("latitude", location.latitude.toString())
            prefs.putString("longitude", location.longitude.toString())

            prefs.apply()

            sendBroadcast(Intent("com.inferno.quakeReport.location"))
        } else {
            stopSelf()
            sendBroadcast(Intent("com.inferno.quakeReport.location"))
            locationManager.removeUpdates(this)
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
    override fun onProviderEnabled(provider: String?) = Unit
    override fun onProviderDisabled(provider: String?) = Unit

    override fun onBind(intent: Intent) = null
}
