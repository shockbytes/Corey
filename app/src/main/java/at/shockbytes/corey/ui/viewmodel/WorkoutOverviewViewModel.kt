package at.shockbytes.corey.ui.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
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
    private val schedulerFacade: SchedulerFacade
): BaseViewModel() {


    sealed class RetrieveWorkoutState {
        data class Success(val workouts: List<Workout>): RetrieveWorkoutState()
        data class Error(val throwable: Throwable): RetrieveWorkoutState()
    }

    private val workoutState = MutableLiveData<RetrieveWorkoutState>()

    init {
        loadWorkouts()
    }

    fun getWorkouts(): LiveData<RetrieveWorkoutState> = workoutState

    private fun loadWorkouts() {
        workoutRepository.workouts
                .observeOn(schedulerFacade.io)
                .observeOn(schedulerFacade.ui)
                .subscribe ({ w ->
                    val data = w.sortedWith(WorkoutNameComparator()).toMutableList()
                    workoutState.postValue(RetrieveWorkoutState.Success(data))
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