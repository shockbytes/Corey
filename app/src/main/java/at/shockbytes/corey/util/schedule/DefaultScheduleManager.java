package at.shockbytes.corey.util.schedule;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import at.shockbytes.corey.R;
import at.shockbytes.corey.common.core.util.ResourceManager;
import at.shockbytes.corey.core.receiver.NotificationReceiver;
import at.shockbytes.corey.storage.StorageManager;
import at.shockbytes.corey.storage.live.LiveScheduleUpdateListener;
import at.shockbytes.corey.util.AppResourceManager;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * @author Martin Macheiner
 *         Date: 22.02.2017.
 */

public class DefaultScheduleManager implements ScheduleManager {

    private Context cxt;
    private StorageManager storageManager;
    private SharedPreferences preferences;

    @Inject
    public DefaultScheduleManager(StorageManager storageManager, Context cxt,
                                  SharedPreferences preferences) {
        this.storageManager = storageManager;
        this.preferences = preferences;
        this.cxt = cxt;
    }

    @Override
    public void poke() {

        int REQ_CODE = 0x9238;

        AlarmManager alarmManager = (AlarmManager) cxt.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(cxt, NotificationReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(cxt, REQ_CODE, intent, 0);

        // TODO v2.1 Do not hardcode hour and minute and maybe insert guard below
        int hour = 8;
        int minute = 30;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);

        //Add a day if alarm is set for before current time, so the alarm is triggered the next day
        if (cal.before(Calendar.getInstance())) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        // PendingIntent is null if already set http://stackoverflow.com/a/9575569/3111388
        if (pIntent != null) {
            // Set to fire every day
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                    TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS), pIntent);
        }
    }

    @Override
    public Observable<List<ScheduleItem>> getSchedule() {
        return storageManager.getSchedule()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<String>> getItemsForScheduling() {
        return storageManager.getItemsForScheduling();
    }

    @Override
    public ScheduleItem insertScheduleItem(ScheduleItem item) {
        return storageManager.insertScheduleItem(item);
    }

    @Override
    public void updateScheduleItem(ScheduleItem item) {
        storageManager.updateScheduleItem(item);
    }

    @Override
    public void deleteScheduleItem(ScheduleItem item) {
        storageManager.deleteScheduleItem(item);
    }

    @Override
    public boolean isWorkoutNotificationDeliveryEnabled() {
        return preferences.getBoolean(cxt.getString(R.string.prefs_workout_day_notif_key), false);
    }

    @Override
    public boolean isWeighNotificationDeliveryEnabled() {
        return preferences.getBoolean(cxt.getString(R.string.prefs_weigh_notif_key), false);
    }

    @Override
    public int getDayOfWeighNotificationDelivery() {
        return preferences.getInt(cxt.getString(R.string.prefs_weigh_notif_day_key), 0);
    }

    @Override
    public void postWeighNotification() {
        NotificationManager nm = (NotificationManager) cxt.getSystemService(NOTIFICATION_SERVICE);
        nm.notify(0x90, AppResourceManager.getWeighNotification(cxt));
    }

    @Override
    public void tryPostWorkoutNotification() {

        getSchedule().subscribe(new Action1<List<ScheduleItem>>() {
            @Override
            public void call(List<ScheduleItem> scheduleItems) {

                for (ScheduleItem item : scheduleItems) {
                    if (item.getDay() == ResourceManager.getDayOfWeek() && !item.isEmpty()) {

                        NotificationManager nm = (NotificationManager) cxt.getSystemService(NOTIFICATION_SERVICE);
                        nm.notify(0x91, AppResourceManager.getWorkoutNotification(cxt, item.getName()));
                        return;
                    }
                }

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.wtf("Corey", "Cannot retrieve workouts: " + throwable.getLocalizedMessage());
            }
        });

    }

    @Override
    public void registerLiveForScheduleUpdates(LiveScheduleUpdateListener listener) {
        storageManager.registerLiveScheduleUpdates(listener);
    }

    @Override
    public void unregisterLiveForScheduleUpdates(LiveScheduleUpdateListener listener) {
        storageManager.unregisterLiveScheduleUpdates(listener);
    }

}
