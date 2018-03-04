package at.shockbytes.corey.util.schedule;

import java.util.List;

import at.shockbytes.corey.storage.live.LiveScheduleUpdateListener;
import io.reactivex.Observable;

/**
 * @author Martin Macheiner
 *         Date: 21.02.2017.
 */

public interface ScheduleManager {

    void poke();

    Observable<List<ScheduleItem>> getSchedule();

    Observable<List<String>> getItemsForScheduling();

    ScheduleItem insertScheduleItem(ScheduleItem item);

    void updateScheduleItem(ScheduleItem item);

    void deleteScheduleItem(ScheduleItem item);

    boolean isWorkoutNotificationDeliveryEnabled();

    boolean isWeighNotificationDeliveryEnabled();

    int getDayOfWeighNotificationDelivery();

    void postWeighNotification();

    void tryPostWorkoutNotification();

    void registerLiveForScheduleUpdates(LiveScheduleUpdateListener listener);

    void unregisterLiveForScheduleUpdates(LiveScheduleUpdateListener listener);

}
