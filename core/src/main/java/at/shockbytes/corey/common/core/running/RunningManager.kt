package at.shockbytes.corey.common.core.running

import at.shockbytes.corey.common.core.location.CoreyLocation

/**
 * @author Martin Macheiner
 * Date: 05.09.2017.
 */

interface RunningManager {

    val currentPace: String

    val finishedRun: Run

    val isRecording: Boolean

    fun startRunRecording()

    fun stopRunRecord(timeInMs: Long, userWeight: Double): Run

    fun updateCurrentRun(location: CoreyLocation): Run
}
