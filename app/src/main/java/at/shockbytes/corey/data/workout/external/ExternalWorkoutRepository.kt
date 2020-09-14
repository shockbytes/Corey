package at.shockbytes.corey.data.workout.external

import io.reactivex.Observable

interface ExternalWorkoutRepository {

    fun loadExternalWorkouts(): Observable<List<ExternalWorkout>>
}