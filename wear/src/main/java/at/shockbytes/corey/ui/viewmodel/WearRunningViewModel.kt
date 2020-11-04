package at.shockbytes.corey.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.shockbytes.core.viewmodel.BaseViewModel
import at.shockbytes.corey.data.workout.PulseLogger
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class WearRunningViewModel @Inject constructor() : BaseViewModel() {

    private val pulseLogger = PulseLogger()

    private val formattedHeartRate = MutableLiveData<String>()
    fun getFormattedHeartRate(): LiveData<String> = formattedHeartRate

    private val onStartEventSubject = PublishSubject.create<Unit>()
    val onStartEvent: Observable<Unit> = onStartEventSubject

    fun startRun() {
        onStartEventSubject.onNext(Unit)
    }

    fun onHeartRateAvailable(heartRate: Int) {
        pulseLogger.logPulse(heartRate)

        val fhr = if (heartRate > 0) heartRate.toString() else "---"
        formattedHeartRate.postValue(fhr)
    }
}