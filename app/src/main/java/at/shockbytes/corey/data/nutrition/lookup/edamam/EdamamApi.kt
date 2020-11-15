package at.shockbytes.corey.data.nutrition.lookup.edamam

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface EdamamApi {

    @GET("parser")
    fun textSearch(
        @Query("app_key") appKey: String = APP_KEY,
        @Query("app_id") appId: String = APP_ID,
        @Query("ingr") keyword: String
    ): Single<EdamamLookupResponse>

    companion object {

        const val SERVICE_ENDPOINT = "https://api.edamam.com/api/food-database/v2/"

        const val APP_KEY = "dd66f5b90816cc5203948fa5381761db"
        const val APP_ID = "bba5bbd9"
    }
}