package at.shockbytes.corey.data.reminder

import android.app.Notification
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.workout.model.WorkoutIconType
import at.shockbytes.corey.util.toBitmap

object ReminderNotificationBuilder {

    private const val defaultChannelId: String = "corey_notifications"

    fun buildWorkoutNotification(
        context: Context,
        workoutName: String,
        workoutIconType: WorkoutIconType,
        channelId: String = defaultChannelId
    ): Notification {

        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_tab_workout)
            .setLargeIcon(getWorkoutIcon(context, workoutIconType))
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle())
            .setContentText(context.getString(R.string.notification_workout_msg, workoutName))
            .setContentTitle(context.getString(R.string.notification_workout_title))
            .setVibrate(longArrayOf(150, 150, 150))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .build()
    }

    fun buildWeighNotification(
        context: Context,
        currentDay: String,
        weight: String,
        weightUnit: String,
        channelId: String = defaultChannelId
    ): Notification {
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_tab_workout)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setAutoCancel(true)
            .setLargeIcon(getWeighIcon(context))
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setStyle(NotificationCompat.BigTextStyle())
            .setContentText(context.getString(R.string.notification_weigh_msg, weight, weightUnit))
            .setContentTitle(context.getString(R.string.notification_weigh_title, currentDay))
            .setVibrate(longArrayOf(150, 150, 150))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .build()
    }

    private fun getWeighIcon(context: Context): Bitmap? {
        return ContextCompat.getDrawable(context, R.drawable.ic_body_scale)
            ?.apply {
                setTintList(ColorStateList.valueOf(context.getColor(R.color.material_blue)))
            }
            ?.toBitmap()
    }

    private fun getWorkoutIcon(
        context: Context,
        workoutIconType: WorkoutIconType
    ): Bitmap? {
        return workoutIconType.iconRes?.let { iconRes ->
            ContextCompat.getDrawable(context, iconRes)
                ?.apply {
                    workoutIconType.notificationTint?.let { tint ->
                        setTintList(ColorStateList.valueOf(context.getColor(tint)))
                    }
                }
                ?.toBitmap()
        }
    }
}