package at.shockbytes.corey.common.core.running;

import android.content.Context;
import android.location.Location;

import at.shockbytes.corey.common.core.util.RunUtils;


/**
 * @author Martin Macheiner
 *         Date: 05.09.2017.
 */

public class DefaultRunningManager implements RunningManager {

    private boolean isRecording;
    private Run run;
    private Location prevLocation;
    private Context context;

    public DefaultRunningManager(Context context) {
        this.context = context;
    }

    @Override
    public void startRunRecording() {
        isRecording = true;
        run = new Run(System.currentTimeMillis());
        prevLocation = null;
    }

    @Override
    public void stopRunRecord(long timeInMs) {
        isRecording = false;

        run.setTime(timeInMs);
        run.setAvgPace(RunUtils.calculatePace(timeInMs, run.getDistance()));
        double weight = 80; // TODO Get weight from BodyManager
        run.setCalories(RunUtils.calculateCaloriesBurned(run.getDistance(), weight));
    }

    @Override
    public Run updateCurrentRun(Location location) {
        float distance = 0f;

        if (prevLocation != null) {
            distance = prevLocation.distanceTo(location) / 1000f;
        }

        run.setDistance((double) distance + run.getDistance());
        run.addLocation(location);
        prevLocation = location;

        return run;
    }

    @Override
    public String getCurrentPace() {


        double distance = run.getCurrentPaceDistance();
        long timeInMs = run.getCurrentPaceTime();

        return RunUtils.calculatePace(timeInMs, distance);
    }

    @Override
    public Run getFinishedRun() {

        if (!isRecording) {
            return run;
        } else {
            throw new IllegalArgumentException("Cannot get run data while manager is recording");
        }
    }

    @Override
    public boolean isRecording() {
        return isRecording;
    }
}
