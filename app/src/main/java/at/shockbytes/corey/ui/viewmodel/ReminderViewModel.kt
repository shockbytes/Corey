package at.shockbytes.corey.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import at.shockbytes.core.viewmodel.BaseViewModel
import at.shockbytes.corey.common.util.TextFormatting
import at.shockbytes.corey.data.reminder.ReminderManager
import javax.inject.Inject

class ReminderViewModel @Inject constructor(
    private val reminderManager: ReminderManager
) : BaseViewModel() {

    private val isWorkoutReminderEnabled = MutableLiveData<Boolean>()
    fun isWorkoutReminderEnabled(): LiveData<Boolean> = isWorkoutReminderEnabled

    private val isWeighReminderEnabled = MutableLiveData<Boolean>()
    fun isWeighReminderEnabled(): LiveData<Boolean> = isWeighReminderEnabled

    private val hourOfWeighReminder = MutableLiveData<Int>()
    fun getHourOfWeighReminder(): LiveData<String> = Transformations.map(hourOfWeighReminder) { hourOfWeighReminder ->
        TextFormatting.formatHourToHourAndMinuteFormat(hourOfWeighReminder)
    }

    private val hourOfWorkoutReminder = MutableLiveData<Int>()
    fun getHourOfWorkoutReminder(): LiveData<String> = Transformations.map(hourOfWorkoutReminder) { hourOfWorkoutReminder ->
        TextFormatting.formatHourToHourAndMinuteFormat(hourOfWorkoutReminder)
    }

    private val dayOfWeighReminder = MutableLiveData<Int>()
    fun getDayOfWeighReminder(): LiveData<Int> = dayOfWeighReminder

    init {
        isWorkoutReminderEnabled.postValue(reminderManager.isWorkoutReminderEnabled)
        isWeighReminderEnabled.postValue(reminderManager.isWeighReminderEnabled)

        hourOfWeighReminder.postValue(reminderManager.hourOfWeighReminder)
        hourOfWorkoutReminder.postValue(reminderManager.hourOfWorkoutReminder)

        dayOfWeighReminder.postValue(reminderManager.dayOfWeighReminder)
    }

    fun enableWorkoutReminder(isEnabled: Boolean) {
        reminderManager.isWorkoutReminderEnabled = isEnabled
        isWorkoutReminderEnabled.postValue(isEnabled)
    }

    fun enableWeighReminder(isEnabled: Boolean) {
        reminderManager.isWeighReminderEnabled = isEnabled
        isWeighReminderEnabled.postValue(isEnabled)
    }

    fun setHourOfWeighReminder(context: Context, hour: Int) {
        reminderManager.hourOfWeighReminder = hour
        hourOfWeighReminder.postValue(reminderManager.hourOfWeighReminder)

        reminderManager.poke(context)
    }

    fun setHourOfWorkoutReminder(context: Context, hour: Int) {
        reminderManager.hourOfWorkoutReminder = hour
        hourOfWorkoutReminder.postValue(reminderManager.hourOfWorkoutReminder)

        reminderManager.poke(context)
    }

    fun setDayOfWeighReminder(context: Context, day: Int) {
        reminderManager.dayOfWeighReminder = day
        dayOfWeighReminder.postValue(reminderManager.dayOfWeighReminder)

        reminderManager.poke(context)
    }
}