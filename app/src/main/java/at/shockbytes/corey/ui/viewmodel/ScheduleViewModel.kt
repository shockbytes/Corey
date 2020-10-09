package at.shockbytes.corey.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.core.viewmodel.BaseViewModel
import at.shockbytes.corey.common.addTo
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
                    sparseSchedule
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
}