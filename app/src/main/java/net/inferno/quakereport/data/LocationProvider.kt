package net.inferno.quakereport.data

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import net.inferno.quakereport.extension.hasPermission
import java.util.*

/**
 * A class responsible of requesting the user's location (for one time only).
 * The parameter is used to check for location permission, launch intents, and request the GPS' activation using Play Services
 * This extends LifecycleObserver so it can automatically cancel the location request when the `mainActivity` stops
 * @param activity Used for intents, and lifecycle handling.
 */
class LocationProvider(
    private val activity: AppCompatActivity,
) : LifecycleObserver {

    private var onFailure: ((Int) -> Unit)? = null
    private var onSuccess: ((Location) -> Unit)? = null

    // Callback for the locationProviderClient. Called when a location is received through GPS
    private val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            // Remove this callback from the locationProviderClient, so the callback is only called once
            locationProviderClient.removeLocationUpdates(this)
            if (result == null) onFailure?.invoke(ERROR_LOCATION_UNAVAILABLE)
            else {
                onSuccess?.invoke(result.lastLocation)
            }
        }
    }
    private val locationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(activity)
    }
    private val locationManager by lazy { activity.getSystemService<LocationManager>()!! }

    private val locationRequest = LocationRequest().apply {
        interval = LOCATION_INTERVAL_TIME
        fastestInterval = LOCATION_INTERVAL_TIME / 2
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        numUpdates = 1
    }

    init {
        activity.lifecycle.addObserver(this)
    }

    fun addOnSuccessListener(onSuccess: (Location) -> Unit): LocationProvider {
        this.onSuccess = onSuccess
        return this
    }

    fun addOnFailureListener(onFailure: (Int) -> Unit): LocationProvider {
        this.onFailure = onFailure
        return this
    }

    @SuppressLint("MissingPermission")
    fun requestLocation() {
        // Check for permission before advancing any further
        if (!checkPermissionRequest()) return
        // Ready the location request, and use 'priority = `LocationRequest.PRIORITY_HIGH_ACCURACY`' so the request is performed over GPS
        // Check if GPS is enabled on the device. If so, perform the location request
        if (isGpsEnabled()) {
            when (checkPlayServicesAvailability()) {
                ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED -> {
                    GoogleApiAvailability.getInstance().showErrorDialogFragment(
                        activity,
                        ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED,
                        RC_UPDATE_PLAY_SERVICES
                    ) {
                        requestLocationCompat()
                    }
                }
                ConnectionResult.API_UNAVAILABLE, ConnectionResult.SERVICE_INVALID -> {
                    requestLocationCompat()
                }
                ConnectionResult.SUCCESS -> {
                    locationProviderClient.requestLocationUpdates(
                        locationRequest,
                        callback,
                        Looper.getMainLooper()
                    )
                }
                else -> {
                    onFailure?.invoke(ERROR_PLAY_SERVICES_NOT_AVAILABLE)
                }
            }
        } else {
            when (checkPlayServicesAvailability()) {
                ConnectionResult.SUCCESS -> {
                    // Show the location activation request dialog for the user
                    LocationServices.getSettingsClient(activity).checkLocationSettings(
                        LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
                            .setAlwaysShow(true).build()
                    ).addOnSuccessListener {
                        locationProviderClient.requestLocationUpdates(
                            locationRequest,
                            callback,
                            Looper.myLooper()
                        )
                    }.addOnFailureListener {
                        when ((it as ApiException).statusCode) {
                            // Using Play Services' dialog is unavailable. Resort to default settings mainActivity
                            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                                try {
                                    val launcher = activity.activityResultRegistry.register(
                                        UUID.randomUUID().toString(),
                                        ActivityResultContracts.StartIntentSenderForResult()
                                    ) { result ->
                                        if(result.resultCode == Activity.RESULT_OK) requestLocation()
                                        else onFailure?.invoke(ERROR_GPS_UNAVAILABLE)
                                    }

                                    launcher.launch(
                                        IntentSenderRequest.Builder(
                                            (it as ResolvableApiException).resolution.intentSender
                                        ).build()
                                    )
                                } catch (sie: IntentSender.SendIntentException) {
                                    onFailure?.invoke(ERROR_GPS_UNAVAILABLE)
                                }
                            }
                            // The user has rejected activating the GPS
                            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                                onFailure?.invoke(ERROR_GPS_UNAVAILABLE)
                            }
                        }
                    }
                }
                else -> {
                    onFailure?.invoke(ERROR_GPS_UNAVAILABLE)
                }
            }
        }
    }

    // Check if the application has permission to access location
    private fun checkPermissionRequest() =
        if (activity.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) true
        else {
            onFailure?.invoke(ERROR_PERMISSION_UNAVAILABLE)
            false
        }

    @SuppressLint("MissingPermission")
    private fun requestLocationCompat() {
        locationRequestCallbackCompat = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationManager.removeUpdates(this)

                onSuccess?.invoke(location)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
        }

        locationManager.requestLocationUpdates(
            LOCATION_INTERVAL_TIME / 2,
            1f,
            Criteria().apply {
                powerRequirement = Criteria.POWER_HIGH
                accuracy = Criteria.ACCURACY_FINE
            },
            locationRequestCallbackCompat!!,
            null,
        )
    }

    fun isGpsEnabled() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    private fun checkPlayServicesAvailability() =
        GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity)

    // Automatically cancel the location request when the lifecycle is over
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    @Suppress("Unused")
    fun removeLocationUpdates() {
        locationProviderClient.removeLocationUpdates(callback)
        if (locationRequestCallbackCompat != null)
            locationManager.removeUpdates(locationRequestCallbackCompat!!)
    }

    private var locationRequestCallbackCompat: LocationListener? = null

    companion object {
        const val LOCATION_PERMISSION_RC = 0x0A

        const val RC_ENABLE_LOCATION_SETTINGS = 0x1B
        const val RC_UPDATE_PLAY_SERVICES = 0x2B

        const val ERROR_GPS_UNAVAILABLE = 0x1C
        const val ERROR_LOCATION_UNAVAILABLE = 0x2C
        const val ERROR_PERMISSION_UNAVAILABLE = 0x3C

        // To check why this is `Not Available` instead of `Unavailable`, refer to https://english.stackexchange.com/questions/50276/the-service-is-temporarily-unavailable-vs-not-available.
        const val ERROR_PLAY_SERVICES_NOT_AVAILABLE = 0x4C

        const val LOCATION_INTERVAL_TIME = 5_000L
    }
}