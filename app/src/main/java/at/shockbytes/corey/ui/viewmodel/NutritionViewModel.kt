package at.shockbytes.corey.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.shockbytes.core.viewmodel.BaseViewModel
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.data.nutrition.NutritionBalance
import at.shockbytes.corey.data.nutrition.NutritionEntry
import at.shockbytes.corey.data.nutrition.NutritionPerDay
import at.shockbytes.corey.data.nutrition.NutritionRepository
import at.shockbytes.corey.ui.adapter.nutrition.NutritionAdapterItem
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject

class NutritionViewModel @Inject constructor(
        private val nutritionRepository: NutritionRepository
) : BaseViewModel() {

    data class WeekOverview(
            val week: Int,
            val year: Int,
            val balance: NutritionBalance,
            val kcalIntake: Int,
            val percentageToPreviousWeek: Double?
    )

    private val currentWeekOverview = MutableLiveData<WeekOverview>()
    fun getCurrentWeekOverview(): LiveData<WeekOverview> = currentWeekOverview

    private lateinit var weekOverviewCache: List<WeekOverview>

    fun requestNutritionHistory(): Observable<List<NutritionAdapterItem>> {
        return nutritionRepository
                .loadDailyNutritionEntries()
                .doOnNext(::computeWeekOverviews)
                .map { data ->
                    data.map(NutritionAdapterItem.Companion::fromNutritionPerDay)
                }
    }

    fun showHeaderFor(weekOfYear: Int, year: Int) {
        Timber.e("Nutrition: $weekOfYear / $year")
        weekOverviewCache
                .find { overview -> overview.week == weekOfYear && overview.year == year }
                ?.let(currentWeekOverview::postValue)
    }

    private data class DateGroup(
            val year: Int,
            val weekOfYear: Int
    )

    private fun computeWeekOverviews(data: List<NutritionPerDay>) {
        data
                .groupBy { DateGroup(it.date.year, it.date.weekOfYear) }
                .map { (dateGroup, weekData) ->
                    val weekBalance = NutritionBalance.fromRawKcal(weekData.sumBy { it.balance.rawKcal })

                    WeekOverview(
                            week = dateGroup.weekOfYear,
                            year = dateGroup.year,
                            balance = weekBalance,
                            kcalIntake = weekData.sumBy { day -> day.intake.sumBy { it.kcal } },
                            percentageToPreviousWeek = null // TODO Get reference to previous week
                    )
                }
                .let { weekOverview ->
                    weekOverviewCache = weekOverview
                }
    }

    fun addNutritionEntry(nutritionEntry: NutritionEntry) {
        nutritionRepository.addNutritionEntry(nutritionEntry)
                .subscribe({
                    // TODO Close fragment
                    Timber.d("Nutrition: Entry successfully created")
                }, { throwable ->
                    Timber.e(throwable)
                    // TODO Inform user that something went wrong
                })
                .addTo(compositeDisposable)
    }
}