package at.shockbytes.corey.data.nutrition

import at.shockbytes.corey.data.body.bmr.Bmr
import io.reactivex.Completable
import io.reactivex.Observable

interface NutritionRepository {

    fun computeCurrentBmr(): Observable<Bmr>

    fun loadDailyNutritionEntries(): Observable<List<NutritionPerDay>>

    fun addNutritionEntry(entry: NutritionEntry): Completable

    fun deleteNutritionEntry(id: String): Completable
}