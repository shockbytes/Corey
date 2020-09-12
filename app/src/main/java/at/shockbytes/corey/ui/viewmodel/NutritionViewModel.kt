package at.shockbytes.corey.ui.viewmodel

import at.shockbytes.core.viewmodel.BaseViewModel
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.data.nutrition.NutritionPerDay
import at.shockbytes.corey.data.nutrition.NutritionRepository
import timber.log.Timber
import javax.inject.Inject

class NutritionViewModel @Inject constructor(
        private val nutritionRepository: NutritionRepository
): BaseViewModel() {

    fun requestNutritionHistory() {

        Timber.e("Nutrition: Request nutrition history")
        nutritionRepository
                .loadDailyNutritionEntries()
                .doOnNext(::computeAndPostInitialWeekOverview)
                .subscribe({ entries ->
                    Timber.e("Nutrition: No data available")
                    Timber.d(entries.toString())
                }, { throwable ->
                    Timber.e(throwable)
                })
                .addTo(compositeDisposable)
    }

    private fun computeAndPostInitialWeekOverview(data: List<NutritionPerDay>) {
        // TODO
    }
}