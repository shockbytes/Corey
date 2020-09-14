package at.shockbytes.corey.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.core.viewmodel.BaseViewModel
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.common.core.location.CoreyLocation
import at.shockbytes.corey.common.core.location.LocationRepository
import at.shockbytes.corey.common.core.running.Run
import at.shockbytes.corey.common.core.running.RunUpdate
import at.shockbytes.corey.common.core.running.RunningManager
import at.shockbytes.corey.data.body.BodyRepository
import at.shockbytes.corey.storage.running.RunningStorageRepository
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class RunningViewModel @Inject constructor(
    private val runningManager: RunningManager,
    private val locationRepository: LocationRepository,
    private val runningStorageRepository: RunningStorageRepository,
    private val schedulers: SchedulerFacade,
    private val bodyRepository: BodyRepository
) : BaseViewModel() {

    private val onRunStartedEvent = PublishSubject.create<Unit>()
    fun onRunStartedEvent(): Observable<Unit> = onRunStartedEvent

    private val onRunStoppedEvent = PublishSubject.create<Run>()
    fun onRunStoppedEvent(): Observable<Run> = onRunStoppedEvent

    private val onRunUpdate = MutableLiveData<RunUpdate>()
    fun onRunUpdate(): LiveData<RunUpdate> = onRunUpdate

    fun startRun() {

        runningManager.startRunRecording()

        locationRepository.requestLocationUpdates()
            .subscribeOn(schedulers.io)
            .flatMap { location ->
                enrichLocationWithUserWeight(location)
            }
            .map { (location, userWeight) ->
                updateRun(location, userWeight)
            }
            .doOnSubscribe {
                onRunStartedEvent.onNext(Unit)
            }
            .subscribe({ runUpdate ->
                onRunUpdate.postValue(runUpdate)
            }, { throwable ->
                Timber.e(throwable)
            })
            .addTo(compositeDisposable)
    }

    private fun enrichLocationWithUserWeight(location: CoreyLocation): Observable<Pair<CoreyLocation, Double>> {
        return bodyRepository.user
            .subscribeOn(schedulers.io)
            .map { Pair(location, it.currentWeight) }
            .onErrorReturn { Pair(location, 0.0) }
    }

    private fun updateRun(location: CoreyLocation, userWeight: Double): RunUpdate {
        val run = runningManager.updateCurrentRun(location)

        return RunUpdate(
            currentLocation = location,
            distance = run.distance,
            locations = run.locations.toList(),
            userWeight = userWeight,
            currentPace = runningManager.currentPace
        )
    }

    fun stopRun() {

        bodyRepository.user
            .subscribeOn(schedulers.io)
            .map { bodyInfo ->
                bodyInfo.currentWeight
            }
            .subscribe({ userWeight ->
                val run = runningManager.stopRunRecord(System.currentTimeMillis(), userWeight)
                runningStorageRepository.storeRun(run)
                onRunStoppedEvent.onNext(run)
            }, { throwable ->
                Timber.e(throwable)
            })
            .addTo(compositeDisposable)
    }
}