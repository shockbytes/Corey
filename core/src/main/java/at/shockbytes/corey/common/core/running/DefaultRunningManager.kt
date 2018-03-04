package at.shockbytes.corey.common.core.running

import android.content.Context
import android.location.Location

import at.shockbytes.corey.common.core.util.RunUtils


/**
 * @author Martin Macheiner
 * Date: 05.09.2017.
 */

class DefaultRunningManager(private val context: Context) : RunningManager {

    override var isRecording: Boolean = false
        get() = run != null

    private var run: Run? = null
    private var prevLocation: Location? = null

    override val currentPace: String
        get() {
            val distance = run?.currentPaceDistance ?: -1.0
            val timeInMs = run?.currentPaceTime ?: 0
            return RunUtils.calculatePace(timeInMs, distance)
        }

    override val finishedRun: Run
        get() = if (!isRecording) {
            run!!
        } else {
            throw IllegalArgumentException("Cannot get run data while manager is recording")
        }

    override fun startRunRecording() {
        isRecording = true
        run = Run()
        prevLocation = null
    }

    override fun stopRunRecord(timeInMs: Long) {
        isRecording = false

        run?.time = timeInMs
        run?.averagePace = RunUtils.calculatePace(timeInMs, run?.distance ?: 0.0)
        val weight = 80.0 // TODO Get weight from BodyManager
        run?.calories = RunUtils.calculateCaloriesBurned(run?.distance ?: 0.0, weight)
    }

    override fun updateCurrentRun(location: Location): Run {
        var distance = 0f

        if (prevLocation != null) {
            distance = prevLocation!!.distanceTo(location) / 1000f
        }

        run?.distance = distance.toDouble() + run?.distance!!
        run?.addLocation(location)
        prevLocation = location

        return run!!
    }
}
