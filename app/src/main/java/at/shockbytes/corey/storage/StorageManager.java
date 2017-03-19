package at.shockbytes.corey.storage;

import java.util.List;

import at.shockbytes.corey.body.BodyInfo;
import at.shockbytes.corey.body.goal.Goal;
import at.shockbytes.corey.storage.live.LiveBodyUpdateListener;
import at.shockbytes.corey.storage.live.LiveScheduleUpdateListener;
import at.shockbytes.corey.storage.live.LiveWorkoutUpdateListener;
import at.shockbytes.corey.util.schedule.ScheduleItem;
import at.shockbytes.corey.common.core.workout.model.Exercise;
import at.shockbytes.corey.common.core.workout.model.Workout;
import rx.Observable;

/**
 * @author Martin Macheiner
 *         Date: 28.12.2016.
 */

public interface StorageManager {

    // -------------- Workouts and exercises -------------
    Observable<List<Workout>> getWorkouts();

    void storeWorkout(Workout workout);

    void deleteWorkout(Workout workout);

    void updateWorkout(Workout workout);

    void pokeExercisesAndSchedulingItems();

    Observable<List<Exercise>> getExercises();
    // ---------------------------------------------------

    // --------------- Workout information ---------------

    void updateWorkoutInformation(int avgPulse, int workoutCountWithPulse,
                                  int workoutCountSum, int workoutTime);
    // ---------------------------------------------------

    // -------------------- Schedules --------------------
    Observable<List<ScheduleItem>> getSchedule();

    Observable<List<String>> getItemsForScheduling();

    ScheduleItem insertScheduleItem(ScheduleItem item);

    void updateScheduleItem(ScheduleItem item);

    void deleteScheduleItem(ScheduleItem item);
    // ---------------------------------------------------

    // -------------------- Body Info --------------------
    Observable<BodyInfo> getLocalBodyInfo();

    void appendAndStoreLocalBodyInfo(final BodyInfo storedInfo,
                                     final BodyInfo appendedInfo);

    void appendToLocalBodyInfo(BodyInfo info);

    int getDesiredWeight();

    void setDesiredWeight(int desiredWeight);

    long getLastBodyInfoPull();

    void setLatestBodyInfoPull(long time);

    String getWeightUnit();

    void setWeightUnit(String unit);

    Observable<List<Goal>> getBodyGoals();

    void updateBodyGoal(Goal g);

    void removeBodyGoal(Goal g);

    void storeBodyGoal(Goal g);

    // ---------------------------------------------------

    // -------------- Live Update listener ---------------
    void registerLiveWorkoutUpdates(LiveWorkoutUpdateListener listener);

    void unregisterLiveWorkoutUpdates(LiveWorkoutUpdateListener listener);

    void registerLiveScheduleUpdates(LiveScheduleUpdateListener listener);

    void unregisterLiveScheduleUpdates(LiveScheduleUpdateListener listener);

    void registerLiveBodyUpdates(LiveBodyUpdateListener listener);

    void unregisterLiveBodyUpdates(LiveBodyUpdateListener listener);
    // ---------------------------------------------------

}
