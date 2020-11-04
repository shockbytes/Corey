package at.shockbytes.corey.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.core.viewmodel.BaseViewModel
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.common.core.workout.model.LocationType
import at.shockbytes.corey.data.schedule.ScheduleItem
import at.shockbytes.corey.data.schedule.ScheduleRepository
import at.shockbytes.corey.ui.adapter.AddScheduleItemAdapter
import timber.log.Timber
import javax.inject.Inject

/**
 * Author:  Martin Macheiner
 * Date:    09.10.2020
 */
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val schedulers: SchedulerFacade
) : BaseViewModel() {

    private var cachedSchedule = Array(MAX_SCHEDULE_DAYS, ::createEmptyScheduleItem).toList()

    private val schedule = MutableLiveData<List<ScheduleItem>>()
    fun getSchedule(): LiveData<List<ScheduleItem>> = schedule

    fun requestSchedule() {
        scheduleRepository.schedule
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .map(::fillUpSparseSchedule)
            .subscribe(schedule::setValue, Timber::e)
            .addTo(compositeDisposable)
    }

    private fun fillUpSparseSchedule(items: List<ScheduleItem>): List<ScheduleItem> {
        return cachedSchedule.mapIndexed { index, emptyItem ->
            items.find { it.day == index }
                ?: emptyItem
        }
    }

    fun insertScheduleItem(item: AddScheduleItemAdapter.ScheduleDisplayItem, position: Int) {
        val scheduleItem = ScheduleItem(
            name = item.item.title,
            day = position,
            locationType = item.item.locationType,
            workoutIconType = item.item.workoutType
        )

        scheduleRepository.insertScheduleItem(scheduleItem)
    }

    fun updateScheduleAfterMove(items: List<ScheduleItem>) {
        cacheSchedule(items)
        updateScheduleItems(items)
    }

    private fun cacheSchedule(items: List<ScheduleItem>) {
        cachedSchedule = items
    }

    private fun updateScheduleItems(items: List<ScheduleItem>) {
        items
            .filter { !it.isEmpty }
            .forEach(::updateScheduleItem)
    }

    private fun updateScheduleItem(item: ScheduleItem) {
        scheduleRepository.updateScheduleItem(item)
    }

    fun deleteScheduleItem(item: ScheduleItem) {
        scheduleRepository.deleteScheduleItem(item)
    }

    fun createEmptyScheduleItem(idx: Int): ScheduleItem {
        return ScheduleItem("", idx, locationType = LocationType.NONE)
    }

    companion object {
        const val MAX_SCHEDULE_DAYS = 7
    }
}