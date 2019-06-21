package at.shockbytes.corey.common.core.running

import at.shockbytes.corey.common.core.location.CoreyLocation
import at.shockbytes.corey.common.core.location.LocationRepository
import at.shockbytes.corey.common.core.util.RunUtils

/**
 * Author:  Martin Macheiner
 * Date:    05.09.2017
 */
class DefaultRunningManager(
    private val locationRepository: LocationRepository
) : RunningManager {

    override var isRecording: Boolean = false

    private lateinit var run: Run
    private var prevLocation: CoreyLocation? = null

    override val currentPace: String
        get() {
            val distance = getCurrentPaceDistance(run)
            val timeInMs = getCurrentPaceTime(run)
            return RunUtils.calculatePace(timeInMs, distance)
        }

    override val finishedRun: Run
        get() = if (!isRecording) {
            run
        } else {
            throw IllegalArgumentException("Cannot get run data while manager is recording")
        }

    override fun startRunRecording() {
        isRecording = true
        run = Run()
        prevLocation = null
    }

    override fun stopRunRecord(timeInMs: Long, userWeight: Double): Run {
        isRecording = false

        run.time = timeInMs
        run.averagePace = RunUtils.calculatePace(timeInMs, run.distance)
        run.calories = RunUtils.calculateCaloriesBurned(run.distance, userWeight)

        return run
    }

    override fun updateCurrentRun(location: CoreyLocation): Run {

        val distance = prevLocation?.let { previous ->
            locationRepository.distanceBetween(previous, location)
        } ?: 0f

        run.distance = distance.toDouble() + run.distance
        run.addLocation(location)
        prevLocation = location

        return run
    }

    private fun getCurrentPaceDistance(run: Run): Double {
        return if (run.locations.size < LOCATIONS_FOR_CURRENT_PACE) {
            0.0
        } else {
            val start = run.locations[run.locations.size - LOCATIONS_FOR_CURRENT_PACE]
            val end = run.locations[run.locations.size - 1]

            locationRepository.distanceBetween(start, end).div(1000.0)
        }
    }

    private fun getCurrentPaceTime(run: Run): Long {
        return if (run.locations.size < LOCATIONS_FOR_CURRENT_PACE) {
            0
        } else {
            run.locations[run.locations.size - 1].time - run.locations[run.locations.size - LOCATIONS_FOR_CURRENT_PACE].time
        }
    }

    companion object {

        private const val LOCATIONS_FOR_CURRENT_PACE = 10
    }
}
