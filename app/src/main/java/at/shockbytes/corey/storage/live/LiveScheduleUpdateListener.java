package at.shockbytes.corey.storage.live;

import at.shockbytes.corey.util.schedule.ScheduleItem;

/**
 * @author Martin Macheiner
 *         Date: 27.02.2017.
 */

public interface LiveScheduleUpdateListener {

    void onScheduleItemAdded(ScheduleItem item);

    void onScheduleItemDeleted(ScheduleItem item);

    void onScheduleItemChanged(ScheduleItem item);

}
