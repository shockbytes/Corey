package at.shockbytes.corey.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import at.shockbytes.corey.core.CoreyApp;
import at.shockbytes.corey.common.core.util.ResourceManager;
import at.shockbytes.corey.util.schedule.ScheduleManager;

public class NotificationReceiver extends BroadcastReceiver {

    @Inject
    protected ScheduleManager manager;

    @Override
    public void onReceive(Context context, Intent intent) {
        ((CoreyApp) context.getApplicationContext()).getAppComponent().inject(this);

        if (manager.isWeighNotificationDeliveryEnabled() &&
                ResourceManager.getDayOfWeek() == manager.getDayOfWeighNotificationDelivery()) {
            manager.postWeighNotification();
        }
        if (manager.isWorkoutNotificationDeliveryEnabled()) {
            manager.tryPostWorkoutNotification();
        }
    }

}
