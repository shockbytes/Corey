package at.shockbytes.corey.storage

import at.shockbytes.corey.body.goal.Goal
import at.shockbytes.corey.common.core.workout.model.Exercise
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.storage.live.LiveBodyUpdateListener
import at.shockbytes.corey.storage.live.LiveScheduleUpdateListener
import at.shockbytes.corey.storage.live.LiveWorkoutUpdateListener
import at.shockbytes.corey.schedule.ScheduleItem
import io.reactivex.Observable

/**
 * @author  Martin Macheiner
 * Date:    28.12.2016.
 */

interface StorageManager {

    // -------------- Workouts and exercises -------------
    val workouts: Observable<List<Workout>>

    val exercises: Observable<List<Exercise>>
    // ---------------------------------------------------

    // -------------------- Schedules --------------------
    val schedule: Observable<List<ScheduleItem>>

    val itemsForScheduling: Observable<List<String>>
    // ---------------------------------------------------

    // -------------------- Body Info --------------------

    var desiredWeight: Int

    val weightUnit: String

    val goals: Observable<List<Goal>>

    fun storeWorkout(workout: Workout)

    fun deleteWorkout(workout: Workout)

    fun updateWorkout(workout: Workout)

    fun pokeExercisesAndSchedulingItems()
    // ---------------------------------------------------

    // --------------- Workout information ---------------

    fun updatePhoneWorkoutInformation(workouts: Int, workoutTime: Int)

    fun updateWearWorkoutInformation(avgPulse: Int, workoutsWithPulse: Int, workoutTime: Int)

    fun insertScheduleItem(item: ScheduleItem): ScheduleItem

    fun updateScheduleItem(item: ScheduleItem)

    fun deleteScheduleItem(item: ScheduleItem)

    fun updateBodyGoal(g: Goal)

    fun removeBodyGoal(g: Goal)

    fun storeBodyGoal(g: Goal)

    // ---------------------------------------------------

    // -------------- Live Update listener ---------------
    fun registerLiveWorkoutUpdates(listener: LiveWorkoutUpdateListener)

    fun unregisterLiveWorkoutUpdates()

    fun registerLiveScheduleUpdates(listener: LiveScheduleUpdateListener)

    fun unregisterLiveScheduleUpdates()

    fun registerLiveBodyUpdates(listener: LiveBodyUpdateListener)

    fun unregisterLiveBodyUpdates()
    // ---------------------------------------------------

}
