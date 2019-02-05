package at.shockbytes.corey.data.workout

/**
 * Author:  Martin Macheiner
 * Date:    15.03.2017
 */
class PulseLogger {

    private val list: MutableList<Int> = mutableListOf()

    fun logPulse(pulse: Int) {
        if (pulse > 0) {
            list.add(pulse)
        }
    }

    fun getAveragePulse(resetBuffer: Boolean): Int {

        val avg = list.average().toInt()
        if (resetBuffer) {
            reset()
        }
        return avg
    }

    private fun reset() {
        list.clear()
    }
}
