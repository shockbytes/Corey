package at.shockbytes.corey.data.workout.external

import at.shockbytes.corey.data.google.CoreyGoogleApiClient
import at.shockbytes.corey.util.WorkInProgress
import io.reactivex.Observable

@WorkInProgress
class GoogleFitExternalWorkoutRepository(
    private val coreyGoogleApiClient: CoreyGoogleApiClient
) : ExternalWorkoutRepository {

    override fun loadExternalWorkouts(): Observable<List<ExternalWorkout>> {
        return coreyGoogleApiClient.onConnectionEvent()
            .filter { isConnected -> isConnected }
            .flatMap { coreyGoogleApiClient.loadGoogleFitWorkouts() }
            .map { googleFitWorkouts ->
                listOf()
            }
    }
}