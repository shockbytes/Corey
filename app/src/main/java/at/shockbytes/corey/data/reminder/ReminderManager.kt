package at.shockbytes.corey.data.reminder

import android.content.Context
import at.shockbytes.corey.data.schedule.ScheduleItem
import io.reactivex.Completable
import io.reactivex.Single

interface ReminderManager {

    var isWorkoutReminderEnabled: Boolean

    var isWeighReminderEnabled: Boolean

    var dayOfWeighReminder: Int

    var hourOfWorkoutReminder: Int

    var hourOfWeighReminder: Int

    fun poke(context: Context)

    fun postWeighNotification(context: Context): Completable

    fun postWorkoutNotification(context: Context): Single<ScheduleItem>

    fun shouldScheduleWeighReminder(): Boolean
}