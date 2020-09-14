package at.shockbytes.corey.data.workout.external

import io.reactivex.Observable

class DummyExternalWorkoutRepository : ExternalWorkoutRepository {

    override fun loadExternalWorkouts(): Observable<List<ExternalWorkout>> {
        return Observable.just(listOf())
    }
}