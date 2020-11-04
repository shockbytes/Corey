package at.shockbytes.corey.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.shockbytes.core.model.LoginUserEvent
import at.shockbytes.core.viewmodel.BaseViewModel
import at.shockbytes.corey.R
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.common.core.util.UserSettings
import at.shockbytes.corey.common.core.util.WatchInfo
import at.shockbytes.corey.data.body.BodyRepository
import at.shockbytes.corey.data.nutrition.NutritionRepository
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
    private val userSettings: UserSettings,
    private val scheduleRepository: ScheduleRepository,
    private val reminderManager: ReminderManager,
    private val wearableManager: WearableManager,
    private val bodyRepository: BodyRepository,
    private val nutritionRepository: NutritionRepository,
    private val workoutRepository: WorkoutRepository
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
        userEvent.postValue(LoginUserEvent.SuccessEvent(userRepository.user, false))

        userSettings.isWeatherForecastEnabled
            .doOnError { weatherForecastEnabled.postValue(false) }
            .subscribe(weatherForecastEnabled::postValue, Timber::e)
            .addTo(compositeDisposable)

        wearableManager.onStart(::onWatchInfoAvailable)
    }

    private fun onWatchInfoAvailable(wi: WatchInfo) {
        watchInfo.postValue(wi)
    }

    fun pokeReminderManager(context: Context) {
        reminderManager.poke(context)
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
        userSettings.setWeatherForecastEnabled(isEnabled)
            .subscribe { Timber.d("Weather forecast flag successfully synced to $isEnabled!") }
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        wearableManager.onPause()
    }

    fun cleanUp() {
        bodyRepository.cleanUp()
    }

    fun prefetch() {
        prefetchNutritionHistory()
        prefetchWorkouts()
    }

    private fun prefetchNutritionHistory() {
        val start = System.currentTimeMillis()
        nutritionRepository.prefetchNutritionHistory()
            .subscribe({
                val diff = System.currentTimeMillis() - start
                Timber.d("Successfully pre-fetched nutrition data after ${diff}ms.")
            }, { throwable ->
                Timber.e(throwable)
            })
            .addTo(compositeDisposable)
    }

    private fun prefetchWorkouts() {
        workoutRepository.poke()
    }
}