package at.shockbytes.corey.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.shockbytes.core.model.LoginUserEvent
import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.core.viewmodel.BaseViewModel
import at.shockbytes.corey.R
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.common.core.util.CoreySettings
import at.shockbytes.corey.common.core.util.WatchInfo
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.data.reminder.ReminderManager
import at.shockbytes.corey.data.schedule.ScheduleRepository
import at.shockbytes.corey.data.user.UserRepository
import at.shockbytes.corey.data.workout.WorkoutRepository
import at.shockbytes.corey.wearable.WearableManager
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val coreySettings: CoreySettings,
    private val scheduleRepository: ScheduleRepository,
    private val reminderManager: ReminderManager,
    private val workoutRepository: WorkoutRepository,
    private val wearableManager: WearableManager,
    private val schedulers: SchedulerFacade
) : BaseViewModel() {

    private val userEvent = MutableLiveData<LoginUserEvent>()
    fun getUserEvent(): LiveData<LoginUserEvent> = userEvent

    private val weatherForecastEnabled = MutableLiveData<Boolean>()
    fun isWeatherForecastEnabled(): LiveData<Boolean> = weatherForecastEnabled

    private val toastSubject = PublishSubject.create<Int>()
    fun getToastMessages(): Observable<Int> = toastSubject

    private val watchInfo = MutableLiveData<WatchInfo>()
    fun getWatchInfo(): LiveData<WatchInfo> = watchInfo

    init {
        workoutRepository.poke()

        userEvent.postValue(LoginUserEvent.SuccessEvent(userRepository.user, false))
        weatherForecastEnabled.postValue(coreySettings.isWeatherForecastEnabled)

        wearableManager.onStart { watchInfo ->
            onWatchInfoAvailable(watchInfo)
        }
    }

    private fun onWatchInfoAvailable(wi: WatchInfo) {
        watchInfo.postValue(wi)

        if (wi.isConnected) {
            workoutRepository.workouts
                .subscribeOn(schedulers.io)
                .subscribe({ workouts ->
                    wearableManager.synchronizeWorkouts(workouts)
                }, { throwable ->
                    Timber.e(throwable)
                })
                .addTo(compositeDisposable)
        }
    }

    fun pokeReminderManager(context: Context) {
        reminderManager.poke(context)
    }

    fun storeWorkout(w: Workout) {
        workoutRepository.storeWorkout(w)
    }

    fun updateWorkout(w: Workout) {
        workoutRepository.updateWorkout(w)
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

    fun enableWeatherForecast(isEnabled: Boolean) {
        coreySettings.isWeatherForecastEnabled = isEnabled
    }

    override fun onCleared() {
        super.onCleared()
        wearableManager.onPause()
    }
}