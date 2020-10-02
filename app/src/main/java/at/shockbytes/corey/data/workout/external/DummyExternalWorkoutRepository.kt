package at.shockbytes.corey.data.workout.external

import at.shockbytes.corey.util.asObservable
import io.reactivex.Observable

class DummyExternalWorkoutRepository : ExternalWorkoutRepository {
    override fun loadExternalWorkouts(): Observable<List<ExternalWorkout>> {
        return listOf<ExternalWorkout>().asObservable()
    }
}