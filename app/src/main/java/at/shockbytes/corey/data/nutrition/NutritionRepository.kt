package at.shockbytes.corey.data.nutrition

import at.shockbytes.corey.data.body.bmr.Bmr
import io.reactivex.Completable
import io.reactivex.Observable

interface NutritionRepository {

    fun computeCurrentBmr(): Observable<Bmr>

    /**
     * Can't be Completable, since the fetching is not a one-off operation but delivers continuous
     * due to empty deliverables in the data sources.
     */
    fun prefetchNutritionHistory(): Observable<*>

    fun loadNutritionHistory(): Observable<List<NutritionPerDay>>

    fun addNutritionEntry(entry: NutritionEntry): Completable

    fun deleteNutritionEntry(id: String): Completable
}