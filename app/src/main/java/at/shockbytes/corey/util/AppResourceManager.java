package at.shockbytes.corey.util;

import android.app.Notification;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import at.shockbytes.corey.R;
import at.shockbytes.corey.common.core.util.ResourceManager;
import at.shockbytes.corey.common.core.util.view.model.SpinnerData;

/**
 * @author Martin Macheiner
 *         Date: 14.03.2017.
 */

public class AppResourceManager extends ResourceManager {

    public static Notification getWorkoutNotification(Context context, String name) {

        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_tab_workout)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_notification_workout))
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setAutoCancel(true)
                .setContentText(context.getString(R.string.notification_workout_msg, name))
                .setContentTitle(context.getString(R.string.notification_workout_title))
                .setVibrate(new long[]{150,150,150})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .build();
    }

    public static Notification getWeighNotification(Context context) {

        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_tab_workout)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_notification_weigh))
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setAutoCancel(true)
                .setContentText(context.getString(R.string.notification_weigh_msg))
                .setContentTitle(context.getString(R.string.notification_weigh_title))
                .setVibrate(new long[]{150,150,150})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .build();
    }

    public static List<SpinnerData> getBodyRegionSpinnerData(Context c) {

        List<SpinnerData> data = new ArrayList<>();
        String[] bodyRegion = c.getResources().getStringArray(R.array.body_region);
        data.add(new SpinnerData(c.getString(R.string.spinner_body_template), 0));
        data.add(new SpinnerData(bodyRegion[0], R.drawable.ic_bodyregion_legs));
        data.add(new SpinnerData(bodyRegion[1], R.drawable.ic_bodyregion_core));
        data.add(new SpinnerData(bodyRegion[2], R.drawable.ic_bodyregion_arms));
        data.add(new SpinnerData(bodyRegion[3], R.drawable.ic_bodyregion_chest));
        data.add(new SpinnerData(bodyRegion[4], R.drawable.ic_bodyregion_whole));
        return data;
    }

    public static List<SpinnerData> getIntensitySpinnerData(Context c) {

        List<SpinnerData> data = new ArrayList<>();
        String[] intensity = c.getResources().getStringArray(R.array.training_intensity);
        data.add(new SpinnerData(c.getString(R.string.spinner_intensity_template), 0));
        for (String s : intensity) {
            data.add(new SpinnerData(s, 0));
        }
        return data;
    }

}
