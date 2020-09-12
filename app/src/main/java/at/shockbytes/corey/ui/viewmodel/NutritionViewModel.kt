package at.shockbytes.corey.ui.viewmodel

import at.shockbytes.core.viewmodel.BaseViewModel
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.data.nutrition.NutritionBalance
import at.shockbytes.corey.data.nutrition.NutritionEntry
import at.shockbytes.corey.data.nutrition.NutritionPerDay
import at.shockbytes.corey.data.nutrition.NutritionRepository
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

    fun requestNutritionHistory() {

        nutritionRepository
                .loadDailyNutritionEntries()
                .doOnNext(::computeWeekOverviews)
                .subscribe({ entries ->
                    Timber.d(entries.toString())
                }, { throwable ->
                    Timber.e(throwable)
                })
                .addTo(compositeDisposable)
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
                    // TODO Cache this value...
                    Timber.d(weekOverview.toString())
                }
    }

    fun addNutritionEntry(nutritionEntry: NutritionEntry) {
        nutritionRepository.addNutritionEntry(nutritionEntry)
                .subscribe({
                    Timber.d("Nutrition: Entry successfully created")
                }, { throwable ->
                    Timber.e(throwable)
                    // TODO Inform user that something went wrong
                })
                .addTo(compositeDisposable)
    }
}