package at.shockbytes.corey.ui.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import at.shockbytes.core.viewmodel.BaseViewModel
import at.shockbytes.corey.data.reminder.ReminderManager
import javax.inject.Inject

class ReminderViewModel @Inject constructor(
    private val reminderManager: ReminderManager
) : BaseViewModel() {

    private val isWorkoutReminderEnabled = MutableLiveData<Boolean>()
    fun isWorkoutReminderEnabled(): LiveData<Boolean> = isWorkoutReminderEnabled

    private val isWeighReminderEnabled = MutableLiveData<Boolean>()
    fun isWeighReminderEnabled(): LiveData<Boolean> = isWeighReminderEnabled

    init {
        isWorkoutReminderEnabled.postValue(reminderManager.isWorkoutReminderEnabled)
        isWeighReminderEnabled.postValue(reminderManager.isWeighReminderEnabled)
    }

    fun enableWorkoutReminder(isEnabled: Boolean) {
        reminderManager.isWorkoutReminderEnabled = isEnabled
        isWorkoutReminderEnabled.postValue(isEnabled)
    }

    fun enableWeighReminder(isEnabled: Boolean) {
        reminderManager.isWeighReminderEnabled = isEnabled
        isWeighReminderEnabled.postValue(isEnabled)
    }
}