package at.shockbytes.corey.data.nutrition

import io.reactivex.Single

interface NutritionRepository {

    fun loadDailyNutritionEntries(): Single<List<DailyNutritionEntry>>

    fun addNutritionEntry()
}