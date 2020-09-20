package at.shockbytes.corey.data.nutrition.lookup.usda

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface UsdaApi {

    @GET("search")
    fun search(
            @Query("api_key") apiKey: String,
            @Query("query") query: String
    ): Single<UsdaSearchResponse>

    companion object {

        const val SERVICE_ENDPOINT = "https://api.nal.usda.gov/fdc/v1/food/"

        const val API_KEY = "TODO"
    }
}