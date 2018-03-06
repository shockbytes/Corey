package at.shockbytes.corey.workout

import at.shockbytes.corey.common.core.workout.model.Exercise
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.storage.StorageManager
import at.shockbytes.corey.storage.live.LiveWorkoutUpdateListener
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @author  Martin Macheiner
 * Date:    22.02.2017.
 */

class DefaultWorkoutManager(private val storageManager: StorageManager) : WorkoutManager {

    override val workouts: Observable<List<Workout>>
        get() = storageManager.workouts
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())

    override val exercises: Observable<List<Exercise>>
        get() = storageManager.exercises
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())

    override fun poke() {
        storageManager.pokeExercisesAndSchedulingItems()
    }

    override fun addWorkout(w: Workout) {
        storageManager.storeWorkout(w)
    }

    override fun deleteWorkout(w: Workout) {
        storageManager.deleteWorkout(w)
    }

    override fun updateWorkout(w: Workout) {
        storageManager.updateWorkout(w)
    }

    override fun updatePhoneWorkoutInformation(workouts: Int, workoutTime: Int) {
        storageManager.updatePhoneWorkoutInformation(workouts, workoutTime)
    }

    override fun registerLiveForWorkoutUpdates(listener: LiveWorkoutUpdateListener) {
        storageManager.registerLiveWorkoutUpdates(listener)
    }

    override fun unregisterLiveForWorkoutUpdates() {
        storageManager.unregisterLiveWorkoutUpdates()
    }

}
