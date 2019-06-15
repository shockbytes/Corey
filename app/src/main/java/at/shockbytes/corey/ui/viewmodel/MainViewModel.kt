package at.shockbytes.corey.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.shockbytes.core.model.LoginUserEvent
import at.shockbytes.core.viewmodel.BaseViewModel
import at.shockbytes.corey.R
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.common.core.util.CoreySettings
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.data.goal.Goal
import at.shockbytes.corey.data.goal.GoalsRepository
import at.shockbytes.corey.data.schedule.ScheduleRepository
import at.shockbytes.corey.data.user.UserRepository
import at.shockbytes.corey.data.workout.WorkoutRepository
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val goalsRepository: GoalsRepository,
    private val coreySettings: CoreySettings,
    private val scheduleRepository: ScheduleRepository,
    workoutRepository: WorkoutRepository
) : BaseViewModel() {

    private val userEvent = MutableLiveData<LoginUserEvent>()
    fun getUserEvent(): LiveData<LoginUserEvent> = userEvent

    private val weatherForecastEnabled = MutableLiveData<Boolean>()
    fun isWeatherForecastEnabled(): LiveData<Boolean> = weatherForecastEnabled

    private val toastSubject = PublishSubject.create<Int>()
    fun getToastMessages(): Observable<Int> = toastSubject

    init {
        workoutRepository.poke()

        userEvent.postValue(LoginUserEvent.SuccessEvent(userRepository.user, false))
        weatherForecastEnabled.postValue(coreySettings.isWeatherForecastEnabled)
    }

    fun storeWorkout(w: Workout) {
        TODO("storeWorkout() not implemented")
    }

    fun updateWorkout(w: Workout) {
        TODO("updateWorkout() not implemented")
    }

    fun resetSchedule() {
        scheduleRepository.deleteAll()
                .subscribe({
                    toastSubject.onNext(R.string.schedule_deletion_successful)
                }, { throwable ->
                    toastSubject.onNext(R.string.schedule_deletion_error)
                    Timber.e(throwable)
                })
                .addTo(compositeDisposable)
    }

    fun logout() {
        userRepository.signOut()
    }

    fun storeBodyGoal(goal: Goal) {
        goalsRepository.storeBodyGoal(goal)
    }

    fun enableWeatherForecast(isEnabled: Boolean) {
        coreySettings.isWeatherForecastEnabled = isEnabled
    }
}