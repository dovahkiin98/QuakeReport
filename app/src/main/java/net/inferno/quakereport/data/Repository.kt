package net.inferno.quakereport.data

import android.content.Context
import android.content.SharedPreferences
import android.text.format.DateFormat
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.preference.PreferenceManager
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import net.inferno.quakereport.model.EarthQuake
import net.inferno.quakereport.ui.settings.PreferencesKeys
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

object Repository {

    private lateinit var client: OkHttpClient
    private lateinit var service: EarthQuakesService

    private val moshi = Moshi.Builder().build()

    private lateinit var prefs: SharedPreferences

    private const val REQUEST_TIMEOUT = 8_000L

    fun init(context: Context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        // Initialize cache directory for OkHttpClient
        val cacheDir = File(context.cacheDir, "cache")

        // Initialize OkHttpClient
        client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .cache(Cache(cacheDir, 10 * 1024 * 1024))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://earthquake.usgs.gov/fdsnws/event/1/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()

        service = retrofit.create()
    }

    fun getEarthQuakesStream(): Flow<PagingData<EarthQuake>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = {
            PagedDataSource { page ->
                getEarthQuakes(page)
            }
        }
    ).flow

    private suspend fun getEarthQuakes(page: Int) = withTimeout(REQUEST_TIMEOUT) {
        withContext(Dispatchers.IO) {
            val orderby = prefs.getString(PreferencesKeys.ORDER_BY, "time")!!
            val minmag = prefs.getFloat(PreferencesKeys.MIN_MAG, 0f)
            val latitude = prefs.getFloat(PreferencesKeys.LATITUDE, 0f)
                .takeUnless { it == 0f }
            val longitude = prefs.getFloat(PreferencesKeys.LONGITUDE, 0f)
                .takeUnless { it == 0f }

            val startdate = DateFormat.format(
                "yyyy-MM-dd",
                Calendar.getInstance(),
            ).toString()

            val offset = if (page > 1) (page - 1) * 20 + 1 else null

            val response = if (latitude == null && longitude == null) {
                service.getEarthQuakes(
                    orderby,
                    minmag,
                    startdate,
                    offset,
                )
            } else {
                service.getEarthQuakes(
                    orderby,
                    minmag,
                    startdate,
                    latitude!!,
                    longitude!!,
                    offset,
                )
            }

            response
        }
    }
}