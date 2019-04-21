package at.shockbytes.corey.data.reminder

import android.content.Context
import io.reactivex.Completable

interface ReminderManager {

    var isWorkoutReminderEnabled: Boolean

    var isWeighReminderEnabled: Boolean

    var dayOfWeighReminder: Int

    fun postWeighNotification(context: Context): Completable

    fun postWorkoutNotification(context: Context): Completable

    fun shouldScheduleWeighReminder(): Boolean
}