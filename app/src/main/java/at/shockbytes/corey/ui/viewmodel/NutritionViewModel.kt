package at.shockbytes.corey.ui.viewmodel

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.core.util.CoreUtils.colored
import at.shockbytes.core.viewmodel.BaseViewModel
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.common.roundDouble
import at.shockbytes.corey.data.nutrition.NutritionBalance
import at.shockbytes.corey.data.nutrition.NutritionEntry
import at.shockbytes.corey.data.nutrition.NutritionPerDay
import at.shockbytes.corey.data.nutrition.NutritionRepository
import at.shockbytes.corey.data.nutrition.lookup.KcalLookup
import at.shockbytes.corey.data.nutrition.lookup.KcalLookupResult
import at.shockbytes.corey.ui.adapter.nutrition.NutritionAdapterItem
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class NutritionViewModel @Inject constructor(
        private val nutritionRepository: NutritionRepository,
        private val schedulers: SchedulerFacade,
        private val kcalLookup: KcalLookup
) : BaseViewModel() {

    data class WeekOverview(
            val week: Int,
            val year: Int,
            val balance: NutritionBalance,
            val kcalIntake: Int,
            private val percentageToPreviousWeek: Double?
    ) {
        fun percentageToPreviousWeekFormatted(): CharSequence? {
            return if (percentageToPreviousWeek != null) {
                if (percentageToPreviousWeek > 0) {
                    "+${percentageToPreviousWeek}%".colored(Color.parseColor("#F44336")) // material red
                } else {
                    "-${percentageToPreviousWeek}%".colored(Color.parseColor("#8BC34A")) // colorPrimary
                }
            } else {
                null
            }
        }
    }

    private val currentWeekOverview = MutableLiveData<WeekOverview>()
    fun getCurrentWeekOverview(): LiveData<WeekOverview> = currentWeekOverview

    sealed class SaveEntryEvent {

        data class Success(val entryName: String) : SaveEntryEvent()

        data class Error(val throwable: Throwable) : SaveEntryEvent()
    }

    sealed class KcalLookupResultState {

        data class Success(val result: KcalLookupResult) : KcalLookupResultState()

        data class NoResults(val searchedText: String) : KcalLookupResultState()

        data class Error(val throwable: Throwable) : KcalLookupResultState()
    }

    private val kcalLookupSubject = PublishSubject.create<KcalLookupResultState>()
    fun onKcalLookupEvent(): Observable<KcalLookupResultState> = kcalLookupSubject

    private val saveEntrySubject = PublishSubject.create<SaveEntryEvent>()
    fun onSaveEntryEvent(): Observable<SaveEntryEvent> = saveEntrySubject

    private lateinit var weekOverviewCache: List<WeekOverview>

    fun requestNutritionHistory(): Observable<List<NutritionAdapterItem>> {
        return nutritionRepository
                .loadNutritionHistory()
                .doOnNext(::computeWeekOverviews)
                .map { data ->
                    data.map(NutritionAdapterItem.Companion::fromNutritionPerDay)
                }
    }

    fun showHeaderFor(weekOfYear: Int, year: Int) {
        weekOverviewCache
                .find { overview -> overview.week == weekOfYear && overview.year == year }
                ?.let(currentWeekOverview::postValue)
    }

    private data class DateGroup(
            val year: Int,
            val weekOfYear: Int
    )

    private fun computeWeekOverviews(data: List<NutritionPerDay>) {
        val intermediateWeekOverview = data
                .groupBy { DateGroup(it.date.year, it.date.weekOfYear) }
                .map { (dateGroup, weekData) ->
                    val weekBalance = NutritionBalance.fromRawKcal(weekData.sumBy { it.balance.rawKcal })

                    WeekOverview(
                            week = dateGroup.weekOfYear,
                            year = dateGroup.year,
                            balance = weekBalance,
                            kcalIntake = weekData.sumBy { day -> day.intake.sumBy { it.kcal } },
                            percentageToPreviousWeek = null
                    )
                }

        weekOverviewCache = intermediateWeekOverview
                .mapIndexed { index, weekOverview ->

                    // TODO Clean up!
                    val previousKcal = intermediateWeekOverview.getOrNull(index.inc())?.kcalIntake
                    if (previousKcal != null) {
                        val percentage = ((weekOverview.kcalIntake - previousKcal) * 100.toDouble()) / previousKcal
                        weekOverview.copy(percentageToPreviousWeek = percentage.roundDouble(2))
                    } else {
                        weekOverview
                    }
                }
    }

    fun addNutritionEntry(nutritionEntry: NutritionEntry) {
        nutritionRepository.addNutritionEntry(nutritionEntry)
                .observeOn(schedulers.ui)
                .subscribe({
                    saveEntrySubject.onNext(SaveEntryEvent.Success(nutritionEntry.name))
                }, { throwable ->
                    Timber.e(throwable)
                    saveEntrySubject.onNext(SaveEntryEvent.Error(throwable))
                })
                .addTo(compositeDisposable)
    }

    fun lookupEstimatedKcal(query: String) {
        kcalLookup.lookup(foodName = query)
                .map { result ->
                    if (result.items.isEmpty()) {
                        KcalLookupResultState.NoResults(query)
                    } else {
                        KcalLookupResultState.Success(result)
                    }
                }
                .doOnError { throwable ->
                    kcalLookupSubject.onNext(KcalLookupResultState.Error(throwable))
                }
                .subscribe(kcalLookupSubject::onNext, Timber::e)
                .addTo(compositeDisposable)
    }
}