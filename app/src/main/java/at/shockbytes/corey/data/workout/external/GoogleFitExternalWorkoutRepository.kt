package at.shockbytes.corey.data.workout.external

import at.shockbytes.corey.data.google.CoreyGoogleApi
import at.shockbytes.corey.util.WorkInProgress
import io.reactivex.Observable

@WorkInProgress
class GoogleFitExternalWorkoutRepository(
    private val coreyGoogleApi: CoreyGoogleApi
) : ExternalWorkoutRepository {

    override fun loadExternalWorkouts(): Observable<List<ExternalWorkout>> {
        return coreyGoogleApi.loadGoogleFitWorkouts()
            .map { googleFitWorkouts ->
                listOf()
            }
    }
}