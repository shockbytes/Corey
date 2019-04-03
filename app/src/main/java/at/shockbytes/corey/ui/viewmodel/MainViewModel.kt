package at.shockbytes.corey.ui.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import at.shockbytes.core.model.LoginUserEvent
import at.shockbytes.core.viewmodel.BaseViewModel
import at.shockbytes.corey.common.core.util.CoreySettings
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.data.goal.Goal
import at.shockbytes.corey.data.goal.GoalsRepository
import at.shockbytes.corey.data.user.UserRepository
import at.shockbytes.corey.data.workout.WorkoutRepository
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val goalsRepository: GoalsRepository,
    private val coreySettings: CoreySettings,
    workoutRepository: WorkoutRepository
) : BaseViewModel() {

    private val userEvent = MutableLiveData<LoginUserEvent>()
    fun getUserEvent(): LiveData<LoginUserEvent> = userEvent

    private val weatherForecastEnabled = MutableLiveData<Boolean>()
    fun isWeatherForecastEnabled(): LiveData<Boolean> = weatherForecastEnabled

    init {
        workoutRepository.poke()

        userEvent.postValue(LoginUserEvent.SuccessEvent(userRepository.user, false))
        weatherForecastEnabled.postValue(coreySettings.isWeatherForecastEnabled)
    }

    fun storeWorkout(w: Workout) {
        // TODO
    }

    fun updateWorkout(w: Workout) {
        // TODO
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