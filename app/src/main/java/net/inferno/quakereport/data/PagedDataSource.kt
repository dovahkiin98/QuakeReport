package net.inferno.quakereport.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.inferno.quakereport.model.EarthQuake
import net.inferno.quakereport.model.EarthQuakesResponse

class PagedDataSource(
    private val requestCallback: suspend (Int) -> EarthQuakesResponse,
) : PagingSource<Int, EarthQuake>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EarthQuake> {
        val page = params.key ?: 1

        return try {

            val response = requestCallback(page).features.map {
                it.properties
            }

            val earthQuakes = if (response.size > 20) response.subList(0, 20) else response

            LoadResult.Page(
                data = earthQuakes,
                prevKey = null,
                nextKey = if (response.isEmpty() || response.size <= 20) null else page + 1
            )
        } catch (e: Exception) {
            Log.e("Error", e.message, e)
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, EarthQuake>): Int? = null
}