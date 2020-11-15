package at.shockbytes.corey.data.nutrition.lookup.usda

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface UsdaApi {

    @GET("foods")
    fun lookup(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("fdcIds") fdcIds: List<String>,
        @Query("format") format: String = UsdaApiFormat.ABRIDGED.format,
        @Query("nutrients") nutrients: List<Int>
    ): Single<List<UsdaLookupResponse>>

    @GET("foods/search")
    fun search(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("query") query: String
    ): Single<UsdaSearchResponse>

    companion object {

        const val SERVICE_ENDPOINT = "https://api.nal.usda.gov/fdc/v1/"

        const val API_KEY = "vRXQi3L5pPmN15RvfgjHe6ddwIqJM88lPJod05da"
    }
}