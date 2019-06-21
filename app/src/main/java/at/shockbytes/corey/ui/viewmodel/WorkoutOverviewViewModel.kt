package at.shockbytes.corey.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.core.viewmodel.BaseViewModel
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.common.core.util.WorkoutNameComparator
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.data.workout.WorkoutRepository
import timber.log.Timber
import javax.inject.Inject

class WorkoutOverviewViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val schedulers: SchedulerFacade
) : BaseViewModel() {

    sealed class RetrieveWorkoutState {
        data class Success(val workouts: List<Workout>) : RetrieveWorkoutState()
        data class Error(val throwable: Throwable) : RetrieveWorkoutState()
    }

    private val workoutState = MutableLiveData<RetrieveWorkoutState>()

    init {
        loadWorkouts()
    }

    fun getWorkouts(): LiveData<RetrieveWorkoutState> = workoutState

    private fun loadWorkouts() {
        workoutRepository.workouts
                .subscribeOn(schedulers.io)
                .observeOn(schedulers.io)
                .map { workouts ->
                    workouts.sortedWith(WorkoutNameComparator()).toMutableList()
                }
                .subscribe({ w ->
                    workoutState.postValue(RetrieveWorkoutState.Success(w))
                }, { throwable ->
                    Timber.e(throwable)
                    workoutState.postValue(RetrieveWorkoutState.Error(throwable))
                })
                .addTo(compositeDisposable)
    }

    fun deleteWorkout(w: Workout) {
        workoutRepository.deleteWorkout(w)
    }
}