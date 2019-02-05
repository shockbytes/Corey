package at.shockbytes.corey.data.workout

import at.shockbytes.corey.common.core.util.Pokeable
import at.shockbytes.corey.common.core.workout.model.Exercise
import at.shockbytes.corey.common.core.workout.model.Workout
import io.reactivex.Observable

/**
 * Author:  Martin Macheiner
 * Date:    21.02.2017
 */
interface WorkoutRepository : Pokeable {

    val workouts: Observable<List<Workout>>

    val exercises: Observable<List<Exercise>>

    fun storeWorkout(workout: Workout)

    fun deleteWorkout(workout: Workout)

    fun updateWorkout(workout: Workout)

    fun updatePhoneWorkoutInformation(workouts: Int, workoutTime: Int)

    fun updateWearWorkoutInformation(avgPulse: Int, workoutsWithPulse: Int, workoutTime: Int)
}
