package at.shockbytes.corey.util

import android.app.Notification
import android.content.Context
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.util.view.model.SpinnerData
import java.util.*

/**
 * @author Martin Macheiner
 * Date: 14.03.2017.
 */

object CoreyAppUtils {

    private const val channelId: String = "corey_notifications"

    fun getWorkoutNotification(context: Context, name: String): Notification {

        return NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_tab_workout)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_notification_workout))
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setAutoCancel(true)
                .setContentText(context.getString(R.string.notification_workout_msg, name))
                .setContentTitle(context.getString(R.string.notification_workout_title))
                .setVibrate(longArrayOf(150, 150, 150))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .build()
    }

    fun getWeighNotification(context: Context): Notification {

        return NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_tab_workout)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_notification_weigh))
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setAutoCancel(true)
                .setContentText(context.getString(R.string.notification_weigh_msg))
                .setContentTitle(context.getString(R.string.notification_weigh_title))
                .setVibrate(longArrayOf(150, 150, 150))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .build()
    }

    fun getBodyRegionSpinnerData(c: Context): List<SpinnerData> {

        val data = mutableListOf<SpinnerData>()
        val bodyRegion = c.resources.getStringArray(R.array.body_region)
        data.add(SpinnerData(c.getString(R.string.spinner_body_template), 0))
        data.add(SpinnerData(bodyRegion[0], R.drawable.ic_bodyregion_legs))
        data.add(SpinnerData(bodyRegion[1], R.drawable.ic_bodyregion_core))
        data.add(SpinnerData(bodyRegion[2], R.drawable.ic_bodyregion_arms))
        data.add(SpinnerData(bodyRegion[3], R.drawable.ic_bodyregion_chest))
        data.add(SpinnerData(bodyRegion[4], R.drawable.ic_bodyregion_whole))
        return data
    }

    fun getIntensitySpinnerData(c: Context): List<SpinnerData> {

        val data = ArrayList<SpinnerData>()
        val intensity = c.resources.getStringArray(R.array.training_intensity)
        data.add(SpinnerData(c.getString(R.string.spinner_intensity_template), 0))
        for (s in intensity) {
            data.add(SpinnerData(s, 0))
        }
        return data
    }

}
