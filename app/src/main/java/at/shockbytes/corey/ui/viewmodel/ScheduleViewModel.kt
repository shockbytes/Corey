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

    private val schedule = MutableLiveData<List<ScheduleItem>>()
    fun getSchedule(): LiveData<List<ScheduleItem>> = schedule

    fun requestSchedule() {
        scheduleRepository.schedule
                .subscribeOn(schedulers.io)
                .observeOn(schedulers.ui)
                .map { sparseSchedule ->
                    // TODO Fix problem here...
                    fillUpScheduleList2(sparseSchedule)
                }
                .subscribe(schedule::setValue, Timber::e)
                .addTo(compositeDisposable)
    }

    fun insertScheduleItem(item: AddScheduleItemAdapter.ScheduleDisplayItem, position: Int) {
        val scheduleItem = ScheduleItem(
                item.item.title,
                position,
                locationType = item.item.locationType,
                workoutIconType = item.item.workoutType
        )

        scheduleRepository.insertScheduleItem(scheduleItem)
    }

    fun updateScheduleItem(item: ScheduleItem) {
        scheduleRepository.updateScheduleItem(item)
    }

    fun deleteScheduleItem(item: ScheduleItem) {
        scheduleRepository.deleteScheduleItem(item)
    }

    @Deprecated(message = "Use ViewModel implementation instead")
    private fun fillUpScheduleList2(items: List<ScheduleItem>): List<ScheduleItem> {
        val def = Array(MAX_SCHEDULE_DAYS) { createEmptyScheduleItem(it) }.toMutableList()
        items.forEach { item ->
            def[item.day] = item
        }
        return def
    }

    fun createEmptyScheduleItem(idx: Int) = ScheduleItem("", idx, locationType = LocationType.NONE)

    companion object {
        const val MAX_SCHEDULE_DAYS = 7
    }
}