package at.shockbytes.corey.data.workout.external

import io.reactivex.Observable

class GoogleFitExternalWorkoutRepository: ExternalWorkoutRepository {

    override fun loadExternalWorkouts(): Observable<List<ExternalWorkout>> {
        TODO("Not yet implemented")
    }
}