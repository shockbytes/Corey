package at.shockbytes.corey.common.core.running

import android.location.Location

/**
 * @author Martin Macheiner
 * Date: 05.09.2017.
 */

interface RunningManager {

    val currentPace: String

    val finishedRun: Run

    val isRecording: Boolean

    fun startRunRecording()

    fun stopRunRecord(timeInMs: Long)

    fun updateCurrentRun(location: Location): Run

}
