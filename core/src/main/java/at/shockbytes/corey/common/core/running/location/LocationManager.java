package at.shockbytes.corey.common.core.running.location;

import android.location.Location;
import android.support.annotation.NonNull;

/**
 * @author Martin Macheiner
 *         Date: 05.09.2017.
 */

public interface LocationManager {

    interface OnLocationUpdateListener {

        void onConnected();

        void onDisconnected();

        void onError(Exception e);

        void onLocationUpdate(Location location);

    }

    // 1 seconds
    long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;

    // Half of normal update time
    long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    void start(@NonNull OnLocationUpdateListener listener);

    void stop();

    boolean isLocationUpdateRequested();

}