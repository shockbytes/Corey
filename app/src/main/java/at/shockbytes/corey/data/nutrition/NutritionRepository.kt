package at.shockbytes.corey.data.nutrition

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface NutritionRepository {

    val bmrComputationName: String

    fun loadDailyNutritionEntries(): Observable<List<NutritionPerDay>>

    fun addNutritionEntry(entry: NutritionEntry): Completable

    fun deleteNutritionEntry(id: String): Completable
}